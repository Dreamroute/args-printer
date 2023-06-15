package com.github.dreamroute.args.printer.smaple.controller.c;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 描述：
 *
 * @author w.dehai.2023/6/15.19:10
 */
@RestController
@RequestMapping("/component")
public class ComponentController {

    @PostMapping("/selectById")
    public String selectById() {
        return "OK";
    }

}
