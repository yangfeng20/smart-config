package com.maple.config.core.annotation;

import com.maple.config.core.spring.PropertySubscriptionInjectBeanPostProcessor;
import com.maple.config.core.spring.SmartConfigSpringContext;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author maple
 * @since 2023/12/19 11:12
 * Description:
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({SmartConfigSpringContext.class, PropertySubscriptionInjectBeanPostProcessor.class})
public @interface EnableSmartConfig {

    /**
     * 本地配置文件名；默认从编译之后的根路径下 target/classes
     *
     * @return 文件路径
     */
    String localFilePath() default "application.properties";

    /**
     * 配置描述推断
     * 自动根据本地配置文件上的中文注解推断描述
     *
     * @return 是否开启
     */
    boolean descInfer() default false;

    /**
     * webUi端口
     *
     * @return 端口
     */
    int webUiPort() default 6767;
}
