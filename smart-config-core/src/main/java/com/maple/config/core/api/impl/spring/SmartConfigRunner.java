//package com.maple.config.core.api.impl.spring;
//
//import com.maple.config.core.annotation.EnableSmartConfig;
//import com.maple.config.core.api.SmartConfig;
//import com.maple.config.core.web.ServerBootstrap;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//
//import java.util.Map;
//import java.util.Optional;
//
//public class SmartConfigRunner implements ApplicationRunner {
//
//    @Override
//    public void run(ApplicationArguments args) {
//
//        Map<String, Object> beanNameObjectMap = SpringContext.getBeansWithAnnotation(EnableSmartConfig.class);
//        if (beanNameObjectMap.isEmpty()) {
//            // 没使用注解开启配置
//            return;
//        }
//
//        if (beanNameObjectMap.size() > 1) {
//            throw new RuntimeException("找到多个EnableSmartConfig配置，仅需要一个");
//        }
//
//        Object mainBean = beanNameObjectMap.values().stream().findAny().orElse(new Object());
//        Class<?> clazz = mainBean.getClass();
//        EnableSmartConfig annotation = clazz.getAnnotation(EnableSmartConfig.class);
//        annotation = Optional.ofNullable(annotation)
//                .orElseGet(() -> clazz.getSuperclass().getAnnotation(EnableSmartConfig.class));
//
//        String localFilePath = annotation.localFilePath();
//        boolean descInfer = annotation.descInfer();
//        int webUiPort = annotation.webUiPort();
//
//        SmartConfig smartConfig = new SpringBootConfig(descInfer);
//        smartConfig.init(null, Optional.ofNullable(localFilePath).filter(item -> !item.isEmpty())
//                .orElse("application.properties"));
//        ServerBootstrap serverBootstrap = new ServerBootstrap(smartConfig);
//        try {
//            serverBootstrap.start(webUiPort);
//        } catch (Exception e) {
//            throw new RuntimeException("Smart-Config:webUi启动失败", e);
//        }
//    }
//
//
//}
