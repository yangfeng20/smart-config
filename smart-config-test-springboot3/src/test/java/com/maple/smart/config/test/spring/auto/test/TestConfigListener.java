package com.maple.smart.config.test.spring.auto.test;

import com.maple.smart.config.core.listener.ConfigListener;
import com.maple.smart.config.core.model.ConfigEntity;
import com.maple.smart.config.core.spring.SmartConfigSpringContext;
import com.maple.smart.config.core.subscription.ConfigSubscription;

import java.util.Collection;

/**
 * @author maple
 * Created Date: 2024/4/2 21:22
 * Description:
 */

public class TestConfigListener implements ConfigListener {



    @Override
    public void onChange(Collection<ConfigEntity> changeConfigEntityList) {
        SpringAutoTestMain springAutoTestMain = SmartConfigSpringContext.getBean(SpringAutoTestMain.class);
        springAutoTestMain.test01();
        springAutoTestMain.test02();
    }

    @Override
    public void setConfigSubscription(ConfigSubscription configSubscription) {

    }
}
