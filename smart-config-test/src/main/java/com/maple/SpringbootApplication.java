package com.maple;


import com.maple.config.core.annotation.EnableSmartConfig;
import com.maple.config.core.api.SmartConfig;
import com.maple.config.core.api.impl.spring.SpringBootConfig;
import com.maple.config.core.web.ServerBootstrap;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

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

        SmartConfig smartConfig = new SpringBootConfig(true);
        List<String> list = new ArrayList<>();
        list.add("com.maple");
        smartConfig.init(list,"application.properties");
        ServerBootstrap serverBootstrap = new ServerBootstrap(smartConfig);
        serverBootstrap.start();
    }

    @RequestMapping("/test01")
    public String test01() {
        service.test01();
        return "success";
    }


}

