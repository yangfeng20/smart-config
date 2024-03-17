package com.maple.config.core.spring;

import com.maple.config.core.annotation.EnableSmartConfig;
import com.maple.config.core.boot.SmartConfigBootstrap;
import com.maple.config.core.boot.SpringConfigBootstrap;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Collections;
import java.util.List;

/**
 * @author maple
 * Created Date: 2024/3/8 10:20
 * Description:
 */

public class SmartConfigSpringRunListener implements SpringApplicationRunListener {

    private boolean enableSmartConfig = false;

    private SmartConfigBootstrap smartConfigBootstrap;


    public SmartConfigSpringRunListener(SpringApplication application, String[] args) {
        Class<?> mainClass = application.getMainApplicationClass();
        EnableSmartConfig annotation = mainClass.getAnnotation(EnableSmartConfig.class);
        if (annotation == null) {
            return;
        }
        enableSmartConfig = true;
        List<String> scannerPackagePathList = Collections.singletonList(mainClass.getPackage().getName());
        smartConfigBootstrap = new SpringConfigBootstrap(annotation.descInfer(), annotation.webUiPort(),
                annotation.localFilePath(), scannerPackagePathList);
        smartConfigBootstrap.init();
    }

    @Override
    public void starting() {

    }

    @Override
    public void environmentPrepared(ConfigurableEnvironment environment) {
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        if (!enableSmartConfig) {
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
        if (!enableSmartConfig) {
            return;
        }
        smartConfigBootstrap.refreshConfig();

    }

    @Override
    public void running(ConfigurableApplicationContext context) {

    }

    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {

    }
}
