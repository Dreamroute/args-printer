package com.github.dreamroute.args.printer.spring.boot.starter;

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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.alibaba.fastjson.JSON.toJSONString;

/**
 * @author w.dehai.2021/9/9.14:58
 */
@Slf4j
public class ArgsPrinterConfig implements ImportBeanDefinitionRegistrar {

private static final String PREFIX = "\r\n-------------------------------------------------";

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
            Object[] args = invocation.getArguments();
            String methodName = invocation.getMethod().getDeclaringClass().getSimpleName() + "." + invocation.getMethod().getName();
            String input = null;
            if (args != null && args.length > 0) {
                List<Object> collect = Arrays.stream(args).filter(Serializable.class::isInstance).collect(Collectors.toList());
                try {
                    input = toJSONString(collect);
                } catch (Exception e) {
                    log.error("此处参数序列化失败了，已经经过特殊处理，不会影响业务，开发人员可以尝试排查一下此处的错误原因，方法名: {}, 异常: {}", methodName, e.getMessage());
                }
            }
            StopWatch watch = new StopWatch();
            watch.start();
            String invokeTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
            Object result = invocation.proceed();
            watch.stop();

            String output = result == null ? null : toJSONString(result);

            // 如果超过1秒，那么转换成秒单位，否则就是毫秒
            long consume = watch.getTotalTimeMillis();
            String time = consume > 1000L ? watch.getTotalTimeSeconds() + " 秒" : consume + " 毫秒";

            log.info("\r\n" + PREFIX + "\r\n【 方法名称 】: {}\r\n【 执行时刻 】: {}\r\n【 执行耗时 】: {}\r\n【 方法入参 】: {}\r\n【 方法出参 】: {}" + PREFIX, methodName, invokeTime, time, input, output);

            return result;
        };
        return new DefaultPointcutAdvisor(pointcut, interceptor);
    }
}