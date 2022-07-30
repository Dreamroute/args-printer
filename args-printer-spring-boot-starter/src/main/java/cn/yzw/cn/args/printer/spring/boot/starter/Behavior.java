package cn.yzw.cn.args.printer.spring.boot.starter;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 行为注解
 * @author ：chengying
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface Behavior {

    /**
     * 以json形式传递user
     * @author ：chengying
     */
    String desc() default "";

}
