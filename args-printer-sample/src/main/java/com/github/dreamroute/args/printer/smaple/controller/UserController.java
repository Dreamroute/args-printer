package com.github.dreamroute.args.printer.smaple.controller;

import cn.hutool.json.JSONUtil;
import com.github.dreamroute.args.printer.smaple.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 描述：// TODO
 *
 * @author w.dehi.2021-09-28
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @PostMapping("/selectById")
    public Object selectById(@RequestBody User user) {
        log.info(JSONUtil.toJsonStr(user));
        return user;
    }
}
