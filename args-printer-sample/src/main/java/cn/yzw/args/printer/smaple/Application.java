package cn.yzw.args.printer.smaple;

import cn.yzw.cn.args.printer.spring.boot.starter.EnableArgsPrinter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 描述：// TODO
 *
 * @author w.dehi.2021-09-28
 */
@SpringBootApplication
@EnableArgsPrinter({"cn.yzw.args.printer.smaple.controller"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
