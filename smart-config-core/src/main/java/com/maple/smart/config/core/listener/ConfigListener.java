package com.maple.smart.config.core.listener;

import com.maple.smart.config.core.model.ConfigEntity;
import com.maple.smart.config.core.subscription.ConfigSubscription;

import java.util.Collection;

/**
 * 配置变更监听，用于实现自定义逻辑
 *
 * @author maple
 * @since 2024/3/7 21:55
 */

public interface ConfigListener {

    void onChange(Collection<ConfigEntity> changeConfigEntityList);

    void setConfigSubscription(ConfigSubscription configSubscription);
}
