package com.maple.config.core.api.impl.spring;

import com.maple.config.core.api.SmartConfig;
import com.maple.config.core.web.ServerBootstrap;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MyApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        System.out.println("开始初始化spring-smart-config");
        SmartConfig smartConfig = new SpringBootConfig(true);
        List<String> list = new ArrayList<>();
        list.add("com.maple");
        smartConfig.init(list, "application.properties");
        ServerBootstrap serverBootstrap = new ServerBootstrap(smartConfig);
        try {
            serverBootstrap.start();
        } catch (Exception e) {
            System.out.println("初始化spring-smart-config失败");
            throw new RuntimeException(e);
        }
    }

}
