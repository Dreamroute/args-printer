package com.github.dreamroute.args.printer.smaple.controller;

import com.alibaba.fastjson.JSON;
import com.github.dreamroute.args.printer.smaple.domain.User;
import com.github.dreamroute.args.printer.spring.boot.starter.ArgsPrinterConfig;
import com.github.dreamroute.common.util.test.Appender;
import com.github.dreamroute.common.util.test.MvcTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest extends MvcTest {

    @Resource
    private MockMvc mockMvc;

    @Test
    void selectByIdTest() throws Exception {
        Appender appender = new Appender(ArgsPrinterConfig.class);
        User user = new User();
        user.setId(100L);
        user.setName("w.dehai");
        user.setPassword("123456");
        mvc.perform(post("/user/selectById").content(JSON.toJSONString(user)).contentType(APPLICATION_JSON_VALUE))
                .andDo(print());
        boolean contains = appender.contains("参数: [{\"id\":100,\"name\":\"w.dehai\",\"password\":\"123456\"}]");
        assertTrue(contains);
    }

}