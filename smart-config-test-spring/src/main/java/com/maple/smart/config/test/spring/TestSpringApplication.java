package com.maple.smart.config.test.spring;


import com.maple.config.core.annotation.EnableSmartConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author maple
 * Created Date: 2024/3/13 22:00
 * Description:
 */

@RestController
@EnableSmartConfig
@EnableAspectJAutoProxy
@SpringBootApplication
public class TestSpringApplication {
    @Resource
    private Service01 service01;


    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext application = SpringApplication.run(TestSpringApplication.class, args);

        System.out.println(application.getBean(Service01.class).list);
    }

    @RequestMapping("/test01")
    public String test01() {
        service01.test01();
        return "success";
    }


}
