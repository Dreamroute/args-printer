package com.github.dreamroute.args.printer.smaple.controller.b;

import com.github.dreamroute.args.printer.smaple.domain.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 描述：
 *
 * @author w.dehai.2023/6/15.18:57
 */
@RestController
@RequestMapping("/developer")
public class DevloperController {

    @PostMapping("/selectById")
    public String selectById(@RequestBody User user) {
        return "ok";
    }

}
