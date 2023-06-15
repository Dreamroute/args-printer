package com.github.dreamroute.args.printer.smaple;

import com.github.dreamroute.args.printer.spring.boot.starter.EnableArgsPrinter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 描述：// TODO
 *
 * @author w.dehi.2021-09-28
 */
@SpringBootApplication
@EnableArgsPrinter(value = {"com.github.dreamroute.args.printer.smaple.controller"}, exclude = {"com.github.dreamroute.args.printer.smaple.controller.b"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
