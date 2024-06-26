package com.maple.smart.config.core.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * @author maple
 * @since 2023/12/17 13:43
 * Description:
 */

public class SmartConfigSpringContext implements ApplicationContextAware, BeanFactoryPostProcessor {

    static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SmartConfigSpringContext.applicationContext = applicationContext;
    }

    /**
     * 通过name获取 Bean
     *
     * @param <T>  Bean类型
     * @param name Bean名称
     * @return Bean
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        return (T) applicationContext.getBean(name);
    }

    /**
     * 通过class获取Bean
     *
     * @param <T>   Bean类型
     * @param clazz Bean类
     * @return Bean对象
     */
    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    public static Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotation) {
        return applicationContext.getBeansWithAnnotation(annotation);
    }


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        // 实现BeanFactoryPostProcessor为了先构建出容器对象，随时使用
        //configurableListableBeanFactory
    }
}
