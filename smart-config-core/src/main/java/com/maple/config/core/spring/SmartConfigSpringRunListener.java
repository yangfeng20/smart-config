package com.maple.config.core.spring;

import com.maple.config.core.annotation.EnableSmartConfig;
import com.maple.config.core.boot.SmartConfigBootstrap;
import com.maple.config.core.boot.SpringConfigBootstrap;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;

import java.util.Arrays;
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
        System.out.println("SmartConfigSpringRunListener.starting");
    }

    @Override
    public void environmentPrepared(ConfigurableEnvironment environment) {
        System.out.println("environment = " + environment);
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        System.out.println("context = " + context);
        if (!enableSmartConfig) {
            return;
        }

        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        beanFactory.registerSingleton("smartConfigBootstrap", smartConfigBootstrap);
    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
        System.out.println("context = " + context);

    }

    @Override
    public void started(ConfigurableApplicationContext context) {
        System.out.println("context = " + context);

    }

    @Override
    public void running(ConfigurableApplicationContext context) {
        System.out.println("context = " + context);

    }

    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {
        System.out.println("exception = " + exception);
    }
}
