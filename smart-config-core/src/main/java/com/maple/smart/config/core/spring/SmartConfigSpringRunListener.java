package com.maple.smart.config.core.spring;

import com.maple.smart.config.core.annotation.EnableSmartConfig;
import com.maple.smart.config.core.boot.AbsConfigBootstrap;
import com.maple.smart.config.core.boot.SmartConfigBootstrap;
import com.maple.smart.config.core.boot.SpringConfigBootstrap;
import com.maple.smart.config.core.conflict.ConfigConflictResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
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


    public SmartConfigSpringRunListener(SpringApplication application, String[] args) {
        Class<?> mainClass = application.getMainApplicationClass();
        EnableSmartConfig mainEnableSmartConfig = mainClass.getAnnotation(EnableSmartConfig.class);
        if (mainEnableSmartConfig == null) {
            mark:
            for (Annotation annotation : mainClass.getDeclaredAnnotations()) {
                // 当前注解所有属性字段
                Method[] annotationProperties = annotation.annotationType().getDeclaredMethods();
                for (Method annotationProperty : annotationProperties) {
                    try {
                        Object invoke = annotationProperty.invoke(annotation);
                        if (invoke.getClass().isArray()) {
                            for (Object o : (Object[]) invoke) {
                                if (o instanceof Class && ((Class<?>) o).isAnnotationPresent(EnableSmartConfig.class)) {
                                    mainEnableSmartConfig = ((Class<?>) o).getAnnotation(EnableSmartConfig.class);
                                    break mark;
                                }
                            }
                        } else if (invoke instanceof Class && ((Class<?>) invoke).isAnnotationPresent(EnableSmartConfig.class)) {
                            mainEnableSmartConfig = ((Class<?>) invoke).getAnnotation(EnableSmartConfig.class);
                            break mark;
                        }
                    } catch (Throwable ignore) {
                    }
                }
            }
        }
        if (mainEnableSmartConfig == null) {
            throw new IllegalArgumentException("EnableSmartConfig注解不存在，请检查");
        }
        enableSmartConfig = mainEnableSmartConfig;
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

        boolean defaultValEcho = enableSmartConfig.defaultValEcho();
        if (environment.getProperty("smart.config.default.echo") != null) {
            defaultValEcho = Boolean.parseBoolean(environment.getProperty("smart.config.default.echo"));
        }

        // 读取自定义冲突策略类
        Class<? extends ConfigConflictResolver> customResolverClass = enableSmartConfig.customResolver();
        ConfigConflictResolver customResolver = null;
        if (customResolverClass != null && !customResolverClass.equals(ConfigConflictResolver.class)) {
            try {
                customResolver = customResolverClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("自定义冲突策略实例化失败: " + customResolverClass, e);
            }
        }

        // 读取冲突策略，优先系统属性，其次注解默认值
        String conflictStrategy = environment.getProperty("smart.config.conflict.strategy");
        if (conflictStrategy == null || conflictStrategy.isEmpty()) {
            conflictStrategy = enableSmartConfig.conflictStrategy().name();
        }

        log.debug("Smart-Config 加载配置 descInfer: {} webUiPort: {} configLocation: {} defaultValEcho:{} conflictStrategy:{} customResolver:{}",
                descInfer, webUiPort, configLocation, defaultValEcho, conflictStrategy, customResolverClass);
        // 获取项目名（Spring环境优先 spring.application.name）
        String projectName = AbsConfigBootstrap.resolveProjectName(environment);
        smartConfigBootstrap = new SpringConfigBootstrap(descInfer, defaultValEcho, webUiPort, configLocation, Collections.emptyList(), conflictStrategy, customResolver, projectName);
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
}
