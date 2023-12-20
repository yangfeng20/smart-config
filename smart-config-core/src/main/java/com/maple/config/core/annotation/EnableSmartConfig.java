package com.maple.config.core.annotation;

import com.maple.config.core.api.impl.spring.SmartConfigRunner;
import com.maple.config.core.api.impl.spring.SpringBeanKeyRegister;
import com.maple.config.core.api.impl.spring.SpringContext;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author maple
 * Created Date: 2023/12/19 11:12
 * Description:
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({SpringContext.class, SpringBeanKeyRegister.class, SmartConfigRunner.class})
public @interface EnableSmartConfig {

    String localFilePath() default "application.properties";

    boolean descInfer() default false;
}
