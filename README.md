#### 方法参数打印
> 目的是为了排查问题的时候方便一点，可以在日志文件中明确查看到各个方法的入参
#### 使用方法
1. 引入依赖：
```xml
<dependency>
    <groupId>cn.yzw</groupId>
    <artifactId>args-printer-spring-boot-starter</artifactId>
    <version>最新版本</version>
</dependency>
```
2. 如果需要打印比如`controller, service, mapper`等的调用参数，可以配置`@EnableArgsPrinter`在启动类上，并且将需要打印参数的包名设置在`value`上即可
```java
@SpringBootApplication
@EnableArgsPrinter({"cn.yzw.args.printer.smaple.controller"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

```
3. 打印的参数如下：
```
2021-09-09 16:27:45.475  INFO 37000 --- [nio-8080-exec-2] c.g.d.t.s.ArgsPrinterAutoConfiguration   : 方法: com.example.disco.controller.UserController.login, 参数: [{"name":"w.dehai","password":"123456"}]
```