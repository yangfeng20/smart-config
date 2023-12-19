package com.maple.config.core.annotation;

import com.maple.config.core.api.impl.spring.MyApplicationListener;
import com.maple.config.core.api.impl.spring.SpringBeanKeyRegister;
import com.maple.config.core.api.impl.spring.SpringContext;
import org.springframework.context.annotation.Import;

/**
 * @author maple
 * Created Date: 2023/12/19 11:12
 * Description:
 */

@Import({SpringBeanKeyRegister.class, SpringContext.class, MyApplicationListener.class})
public @interface EnableSmartConfig {
}
