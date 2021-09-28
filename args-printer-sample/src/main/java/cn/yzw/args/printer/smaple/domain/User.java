package cn.yzw.args.printer.smaple.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * 描述：// TODO
 *
 * @author w.dehi.2021-09-28
 */
@Data
public class User implements Serializable {
    private Long id;
    private String name;
    private String password;
}
