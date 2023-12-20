package com.maple;


import com.maple.config.core.annotation.EnableSmartConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author yang'feng
 */
@RestController
@EnableSmartConfig
@SpringBootApplication
public class SpringbootApplication {

    @Resource
    private Service service;


    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext application = SpringApplication.run(SpringbootApplication.class, args);
    }

    @RequestMapping("/test01")
    public String test01() {
        service.test01();
        return "success";
    }


}

