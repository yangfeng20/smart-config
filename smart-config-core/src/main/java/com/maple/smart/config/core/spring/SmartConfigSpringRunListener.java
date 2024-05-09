package com.maple.smart.config.core.spring;

import com.maple.smart.config.core.annotation.EnableSmartConfig;
import com.maple.smart.config.core.boot.SmartConfigBootstrap;
import com.maple.smart.config.core.boot.SpringConfigBootstrap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.time.Duration;
import java.util.Collections;

/**
 * @author maple
 * @since 2024/3/8 10:20
 * Description:
 */


@Slf4j
public class SmartConfigSpringRunListener implements SpringApplicationRunListener {

    private SmartConfigBootstrap smartConfigBootstrap;

    private final EnableSmartConfig enableSmartConfig;

    @SuppressWarnings("unused")
    public SmartConfigSpringRunListener(SpringApplication application, String[] args) {
        Class<?> mainClass = application.getMainApplicationClass();
        enableSmartConfig = mainClass.getAnnotation(EnableSmartConfig.class);
    }

    @Override
    public void starting() {

    }

    @Override
    public void environmentPrepared(ConfigurableEnvironment environment) {
        if (enableSmartConfig == null) {
            return;
        }
        String configLocation = environment.getProperty("spring.config.location");
        String activeProfiles = environment.getProperty("spring.profiles.active");

        // 配置优先级 【 location > active(会加载默认) > 默认 】
        if (configLocation == null && activeProfiles == null) {
            configLocation = enableSmartConfig.localFilePath();
        } else if (configLocation == null) {
            configLocation = "classpath:application.properties;classpath:application-" + activeProfiles + ".properties";
        }

        boolean descInfer = enableSmartConfig.descInfer();
        if (environment.getProperty("smart.config.desc.infer") != null) {
            descInfer = Boolean.parseBoolean(environment.getProperty("smart.config.desc.infer"));
        }

        int webUiPort = enableSmartConfig.webUiPort();
        String webuiPortStr = environment.getProperty("smart.config.webui.port");
        if (webuiPortStr != null) {
            webUiPort = Integer.parseInt(webuiPortStr);
        }

        log.debug("Smart-Config 加载配置 descInfer: {} webUiPort: {} configLocation: {}", descInfer, webUiPort, configLocation);
        smartConfigBootstrap = new SpringConfigBootstrap(descInfer, webUiPort, configLocation, Collections.emptyList());
        smartConfigBootstrap.init();
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        if (enableSmartConfig == null) {
            return;
        }

        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        beanFactory.registerSingleton("smartConfigBootstrap", smartConfigBootstrap);
        beanFactory.registerSingleton("configRepository", smartConfigBootstrap.getConfigRepository());
    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
    }

    @Override
    public void started(ConfigurableApplicationContext context) {
        if (enableSmartConfig == null) {
            return;
        }
        // 仅启动webUI
        smartConfigBootstrap.refreshConfig();

    }

    @Override
    public void running(ConfigurableApplicationContext context) {

    }

    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {

    }


    // 适配 SpringBoot 3
    @SuppressWarnings("all")
    public void environmentPrepared(ConfigurableBootstrapContext bootstrapContext, ConfigurableEnvironment environment) {
        this.environmentPrepared(environment);
    }

    @SuppressWarnings("all")
    public void started(ConfigurableApplicationContext context, Duration timeTaken) {
        this.started(context);
    }
}
