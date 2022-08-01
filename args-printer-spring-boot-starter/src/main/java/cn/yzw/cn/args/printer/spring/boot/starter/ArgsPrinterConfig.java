package cn.yzw.cn.args.printer.spring.boot.starter;

import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.util.StopWatch;
import org.springframework.util.StringValueResolver;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.alibaba.fastjson.JSON.toJSONString;

/**
 * @author w.dehai.2021/9/9.14:58
 */
@Slf4j
public class ArgsPrinterConfig implements ImportBeanDefinitionRegistrar{

    private final static String PREFIX = "======ArgsPrinter=======>";

    @Override
    public void registerBeanDefinitions(@NonNull AnnotationMetadata importingClassMetadata, @NonNull BeanDefinitionRegistry registry) {
        ImportBeanDefinitionRegistrar.super.registerBeanDefinitions(importingClassMetadata, registry);
        StandardAnnotationMetadata icm = (StandardAnnotationMetadata) importingClassMetadata;
        Class<?> ic = icm.getIntrospectedClass();
        String[] pkg = AnnotationUtil.getAnnotationValue(ic, EnableArgsPrinter.class);
        if (registry instanceof DefaultListableBeanFactory) {
            DefaultListableBeanFactory factory = (DefaultListableBeanFactory) registry;
            Advisor advisor = createAdvisor(pkg, factory);
            factory.registerSingleton("argsPrinter", advisor);
        }
    }

    private Advisor createAdvisor(String[] pkg, DefaultListableBeanFactory factory) {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        String execution = Arrays.stream(pkg).map(e -> "execution(* " + e.trim() + "..*.*(..))").collect(Collectors.joining(" || "));
        pointcut.setExpression(execution);

        MethodInterceptor interceptor = invocation -> {
            String methodName = invocation.getMethod().getDeclaringClass().getName() + "." + invocation.getMethod().getName();
            Object[] args = invocation.getArguments();
            if (args != null && args.length > 0) {
                List<Object> collect = Arrays.stream(args).filter(arg -> arg instanceof Serializable).collect(Collectors.toList());
                try {
                    log.info("\r\n" + PREFIX + "方法: {}, 参数: {}", methodName, toJSONString(collect));
                } catch (Exception e) {
                    log.error("此处参数序列化失败了，已经经过特殊处理，不会影响业务，开发人员可以尝试排查一下此处的错误原因，方法名: {}, 异常: {}", methodName, e);
                }
            }
            recordBehavior(invocation, factory);
            StopWatch watch = new StopWatch();
            watch.start();
            Object result = invocation.proceed();
            watch.stop();
            log.info("\r\n" + PREFIX + "方法: {}, 执行耗时: {} 毫秒", methodName, toJSONString(watch.getTotalTimeMillis()));

            return result;
        };
        return new DefaultPointcutAdvisor(pointcut, interceptor);
    }

    /**
     * 异步记录行为
     * @author ：chengying
     */
    private void recordBehavior(MethodInvocation invocation, DefaultListableBeanFactory factory){
        TimedCache timedCache;
        try {
            timedCache = factory.getBean(TimedCache.class);
        }catch (Exception e){
            return;
        }

        ThreadLocal<Map<String, Object>> tl = (ThreadLocal<Map<String, Object>>) timedCache.get("cookieUser");
        if (Objects.isNull(tl)) {
            return;
        }
        Map<String, Object> userMap = tl.get();
        new Thread(() -> {
            try {
                Behavior behavior = invocation.getMethod().getAnnotation(Behavior.class);
                if (Objects.isNull(behavior)) {
                    return;
                }
                String methodName = invocation.getMethod().getDeclaringClass().getName() + "." + invocation.getMethod().getName();
                List<Object> param = Arrays.stream(invocation.getArguments()).filter(arg -> arg instanceof Serializable).collect(Collectors.toList());
                if (Objects.nonNull(behavior)) {
                    RestTemplate restTemplate = new RestTemplate();
                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put("interfaceName", methodName);
                    paramMap.put("interfaceParam", param);
                    paramMap.put("interfaceDesc", behavior.value());
                    paramMap.put("callDate", new Date());
                    paramMap.put("organizationSysNo", userMap.get("organizationSysNo"));
                    paramMap.put("organizationCode", userMap.get("organizationCode"));
                    paramMap.put("organizationName", userMap.get("organizationName"));
                    paramMap.put("inUserSysNo", userMap.get("userSysNo"));
                    paramMap.put("inUserName", userMap.get("userDisplayName"));
                    paramMap.put("source", userMap.get("source"));

                    HttpHeaders httpHeaders = new HttpHeaders();
                    MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
                    httpHeaders.setContentType(type);
                    HttpEntity<String> httpEntity = new HttpEntity<>(JSONUtil.toJsonStr(paramMap), httpHeaders);
                    restTemplate.postForEntity(userMap.get("url").toString(), httpEntity, Object.class);
                    log.info("记录用户行为的请求已发送，desc: {}, user:{}", behavior.value(), JSONUtil.toJsonStr(userMap));
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }).start();
    }
}