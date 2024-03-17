package com.maple.config.core.listener;

import com.maple.config.core.model.ConfigEntity;
import com.maple.config.core.subscription.ConfigSubscription;

import java.util.Collection;

/**
 * @author maple
 * Created Date: 2024/3/7 21:55
 * Description:
 */

public interface ConfigListener {

    void onChange(Collection<ConfigEntity> changeConfigEntityList);

    void setConfigSubscription(ConfigSubscription configSubscription);
}
