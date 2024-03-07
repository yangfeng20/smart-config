package com.maple.config.core.api.impl.spring;

import com.maple.config.core.annotation.SmartValue;
import com.maple.config.core.api.SmartConfig;
import com.maple.config.core.api.impl.local.LocalFileConfig;
import org.springframework.beans.factory.annotation.Value;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * @author yangfeng
 * @since : 2023/12/1 17:17
 * desc:
 */

public class SpringBootConfig extends LocalFileConfig {


    protected Map<String, List<String>> beanKeyNameMap;

    public SpringBootConfig(boolean descInfer) {
        super(descInfer);
    }

    @Override
    protected void customInit() {
        super.customInit();
        SpringBeanKeyRegister springBeanKeyRegister = SpringContext.getBean(SpringBeanKeyRegister.class);
        beanKeyNameMap = springBeanKeyRegister.getBeanKeyMap();
        configObserverMap = springBeanKeyRegister.getConfigObserverMap();
    }

    @Override
    protected String getKey(Field field) {
        SmartValue annotation = field.getAnnotation(SmartValue.class);
        if (annotation == null) {
            return null;
        }

        Matcher matcher = SpringBeanKeyRegister.PLACEHOLDER_PATTERN.matcher(annotation.value());
        if (!matcher.find()) {
            return null;
        }
        String value = matcher.group(1);
        return value.split(":")[0];
    }

    @Override
    protected String propertyInject(Field field, String value) {
        String configKey = getKey(field);
        List<Object> waitUpdateFieldBeanList = getBeanByKey(configKey);
        for (Object bean : waitUpdateFieldBeanList) {
            field.setAccessible(true);
            try {
                field.set(bean, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return value;
    }

    private List<Object> getBeanByKey(String key) {
        if (key == null || key.isEmpty()) {
            return Collections.emptyList();
        }

        if (beanKeyNameMap == null) {
            throw new RuntimeException("未初始化beanKey注册器");
        }

        List<String> beanNameList = beanKeyNameMap.get(key);
        if (beanNameList == null || beanNameList.isEmpty()) {
            return Collections.emptyList();
        }

        return beanNameList.stream()
                .map(SpringContext::getBean)
                .collect(Collectors.toList());
    }

    @Override
    public Object getObjectByKey(String key) {
        return getBeanByKey(key);
    }
}
