package com.maple.config.core.api.impl.spring;

import com.maple.config.core.api.SmartConfig;
import com.maple.config.core.web.ServerBootstrap;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.util.Optional;

public class SmartConfigRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println(args);

        start("");
    }

    private void start(String localConfigPath) {
        System.out.println("开始初始化spring-smart-config");

        String filePath = Optional.ofNullable(localConfigPath)
                .filter(item -> !item.isEmpty())
                .orElse("application.properties");

        SmartConfig smartConfig = new SpringBootConfig(true);
        smartConfig.init(null, filePath);
        ServerBootstrap serverBootstrap = new ServerBootstrap(smartConfig);
        try {
            serverBootstrap.start();
        } catch (Exception e) {
            System.out.println("初始化spring-smart-config失败");
            throw new RuntimeException(e);
        }
    }

}
