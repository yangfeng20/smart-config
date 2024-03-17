package com.maple.config.core.spring;

import com.maple.config.core.boot.SpringConfigBootstrap;
import com.maple.config.core.subscription.ConfigSubscription;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author maple
 * @since 2023/12/17 14:02
 * Description:
 */

public class SpringConfigSubscriptionPostProcessor implements BeanPostProcessor, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        SpringConfigBootstrap springConfigBootstrap = applicationContext.getBean(SpringConfigBootstrap.class);
        ConfigSubscription configSubscription = springConfigBootstrap.getConfigSubscription();

        // 订阅当前bean满足条件的字段，并构建key beanName映射关系
        configSubscription.addSubscription(bean);
        return bean;
    }
}
