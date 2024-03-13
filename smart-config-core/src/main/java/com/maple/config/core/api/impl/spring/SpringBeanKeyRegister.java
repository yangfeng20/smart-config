package com.maple.config.core.api.impl.spring;

import com.maple.config.core.boot.SpringConfigBootstrap;
import com.maple.config.core.subscription.ConfigSubscription;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

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

public class SpringBeanKeyRegister implements BeanPostProcessor, ApplicationContextAware {

    private ApplicationContext applicationContext;

    private final Map<String, List<String>> beanKeyMap = new ConcurrentHashMap<>();

    private final Map<String, List<Field>> configObserverMap = new ConcurrentHashMap<>();

    final static Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{([^}]*)}");


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        SpringConfigBootstrap springConfigBootstrap = applicationContext.getBean(SpringConfigBootstrap.class);
        ConfigSubscription configSubscription = springConfigBootstrap.getConfigSubscription();
        Class<?> clazz = bean.getClass();
        Field[] fields = clazz.getDeclaredFields();
        //configSubscription.addSubscription(clazz);

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

        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }


    public Map<String, List<String>> getBeanKeyMap() {
        return beanKeyMap;
    }

    public Map<String, List<Field>> getConfigObserverMap() {
        return configObserverMap;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
