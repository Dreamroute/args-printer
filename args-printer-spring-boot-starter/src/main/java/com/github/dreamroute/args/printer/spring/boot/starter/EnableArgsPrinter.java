package com.github.dreamroute.args.printer.spring.boot.starter;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author w.dehi.2021-09-28
 */
@Target(TYPE)
@Retention(RUNTIME)
@Import({ArgsPrinterConfig.class})
public @interface EnableArgsPrinter {

    /**
     * 扫描需要打印参数的包，一般是controller、service和dao层的包，<br>
     * 默认是把当前路径和子路径下所有Java文件都扫描到
     *
     * @return 返回需要打印参数的包路径集合
     */
    String[] value();
}
