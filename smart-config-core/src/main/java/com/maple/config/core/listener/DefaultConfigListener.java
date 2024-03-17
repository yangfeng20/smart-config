package com.maple.config.core.listener;

import com.maple.config.core.model.ConfigEntity;
import com.maple.config.core.subscription.ConfigSubscription;

import java.util.Collection;

/**
 * @author maple
 * @since 2024/3/17 10:40
 * Description:
 */

public class DefaultConfigListener implements ConfigListener {

    protected ConfigSubscription configSubscription;

    @Override
    public void onChange(Collection<ConfigEntity> changeConfigEntityList) {
        System.out.println(this + " - changeConfigEntityList = " + changeConfigEntityList);
    }

    @Override
    public void setConfigSubscription(ConfigSubscription configSubscription) {
        this.configSubscription = configSubscription;
    }
}
