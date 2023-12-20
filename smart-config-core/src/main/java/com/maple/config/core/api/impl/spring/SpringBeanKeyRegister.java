package com.maple.config.core.api.impl.spring;

import com.maple.config.core.annotation.EnableSmartConfig;
import com.maple.config.core.api.SmartConfig;
import com.maple.config.core.web.ServerBootstrap;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author maple
 * Created Date: 2023/12/17 14:02
 * Description:
 */

public class SpringBeanKeyRegister implements BeanPostProcessor {

    private final Map<String, List<String>> beanKeyMap = new ConcurrentHashMap<>();

    private final Map<String, List<Field>> configObserverMap = new ConcurrentHashMap<>();

    final static Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{([^}]*)}");

    private static boolean isStarted = false;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        Field[] fields = clazz.getDeclaredFields();

        String localConfigPath = "";
        if (clazz.isAnnotationPresent(EnableSmartConfig.class)) {
            localConfigPath = clazz.getAnnotation(EnableSmartConfig.class).localFilePath();
        }

        for (Field field : fields) {
            Value annotation = field.getAnnotation(Value.class);
            if (annotation == null) {
                continue;
            }
            Matcher matcher = PLACEHOLDER_PATTERN.matcher(annotation.value());
            if (!matcher.find()) {
                continue;
            }
            String value = matcher.group(1);
            String configKey = value.split(":")[0];

            // 当前key关联beanName
            List<String> beanNameList = beanKeyMap.getOrDefault(configKey, new ArrayList<>());
            beanNameList.add(beanName);
            beanKeyMap.put(configKey, beanNameList);

            // 当前key关联观察者
            List<Field> keyLinkFieldList = configObserverMap.getOrDefault(configKey, new ArrayList<>());
            keyLinkFieldList.add(field);
            configObserverMap.put(configKey, keyLinkFieldList);
        }

        if (!isStarted) {
            //start(localConfigPath);
            isStarted = true;
        }
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
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


    public Map<String, List<String>> getBeanKeyMap() {
        return beanKeyMap;
    }

    public Map<String, List<Field>> getConfigObserverMap() {
        return configObserverMap;
    }
}
