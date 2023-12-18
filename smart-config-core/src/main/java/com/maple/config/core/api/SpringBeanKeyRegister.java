package com.maple.config.core.api;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    private final static Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{([^}]*)}");

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            Value annotation = field.getAnnotation(Value.class);
            if (annotation == null){
                continue;
            }
            Matcher matcher = PLACEHOLDER_PATTERN.matcher(annotation.value());
            if (!matcher.find()){
                continue;
            }
            String value = matcher.group(1);
            String configKey = value.split(":")[0];

            List<String> beanNameList = beanKeyMap.getOrDefault(configKey, new ArrayList<>());
            beanNameList.add(beanName);
            beanKeyMap.put(configKey, beanNameList);
        }
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }


    public Map<String, List<String>> getBeanKeyMap() {
        return beanKeyMap;
    }
}
