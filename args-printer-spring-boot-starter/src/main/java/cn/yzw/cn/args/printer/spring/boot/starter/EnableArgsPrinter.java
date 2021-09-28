package cn.yzw.cn.args.printer.spring.boot.starter;

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
     * 需要打印参数的包，一般是controller、service和dao层的包
     */
    String[] value();
}
