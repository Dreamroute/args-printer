package cn.yzw.cn.args.printer.spring.boot.starter;

import cn.hutool.core.annotation.AnnotationUtil;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.lang.NonNull;
import org.springframework.util.StopWatch;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.alibaba.fastjson.JSON.toJSONString;

/**
 * @author w.dehai.2021/9/9.14:58
 */
@Slf4j
public class ArgsPrinterConfig implements ImportBeanDefinitionRegistrar {

    private final static String PREFIX = "======ArgsPrinter=======>";

    @Override
    public void registerBeanDefinitions(@NonNull AnnotationMetadata importingClassMetadata, @NonNull BeanDefinitionRegistry registry) {
        ImportBeanDefinitionRegistrar.super.registerBeanDefinitions(importingClassMetadata, registry);
        StandardAnnotationMetadata icm = (StandardAnnotationMetadata) importingClassMetadata;
        Class<?> ic = icm.getIntrospectedClass();
        String[] pkg = AnnotationUtil.getAnnotationValue(ic, EnableArgsPrinter.class);
        if (registry instanceof DefaultListableBeanFactory) {
            DefaultListableBeanFactory factory = (DefaultListableBeanFactory) registry;
            Advisor advisor = createAdvisor(pkg);
            factory.registerSingleton("argsPrinter", advisor);
        }
    }

    private Advisor createAdvisor(String[] pkg) {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        String execution = Arrays.stream(pkg).map(e -> "execution(* " + e.trim() + "..*.*(..))").collect(Collectors.joining(" || "));
        pointcut.setExpression(execution);

        MethodInterceptor interceptor = invocation -> {
            String methodName = invocation.getMethod().getDeclaringClass().getName() + "." + invocation.getMethod().getName();
            Object[] args = invocation.getArguments();
            if (args != null && args.length > 0) {
                List<Object> collect = Arrays.stream(args).filter(arg -> arg instanceof Serializable).collect(Collectors.toList());
                try {
                    log.info("\r\n" + PREFIX + "??????: {}, ??????: {}", methodName, toJSONString(collect));
                } catch (Exception e) {
                    log.error("??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????: {}, ??????: {}", methodName, e);
                }
            }
            StopWatch watch = new StopWatch();
            watch.start();
            Object result = invocation.proceed();
            watch.stop();
            log.info("\r\n" + PREFIX + "??????: {}, ????????????: {} ??????", methodName, toJSONString(watch.getTotalTimeMillis()));

            return result;
        };
        return new DefaultPointcutAdvisor(pointcut, interceptor);
    }
}