package com.maple.config.core.spring;

import com.maple.config.core.boot.SpringConfigBootstrap;
import com.maple.config.core.inject.PropertyInject;
import com.maple.config.core.subscription.ConfigSubscription;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.PriorityOrdered;

import java.beans.PropertyDescriptor;

/**
 * @author maple
 * Created Date: 2024/3/18 9:55
 * Description:
 */

public class PropertySubscriptionInjectBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter implements ApplicationContextAware, PriorityOrdered {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeansException {
        SpringConfigBootstrap springConfigBootstrap = applicationContext.getBean(SpringConfigBootstrap.class);
        ConfigSubscription configSubscription = springConfigBootstrap.getConfigSubscription();
        // 订阅当前bean满足条件的字段，并构建key beanName映射关系
        configSubscription.addSubscription(bean);

        ((PropertyInject) configSubscription).propertyInject(bean);
        return pvs;
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
