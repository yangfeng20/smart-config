package com.maple.config.core.repository;

import com.maple.config.core.model.ConfigEntity;
import com.maple.config.core.subscription.ConfigSubscription;

import java.util.Collection;

/**
 * @author maple
 * Created Date: 2024/3/7 21:18
 * Description:
 */

public interface ConfigRepository {

    boolean addConfig(ConfigEntity configEntity);

    String getConfig(String key);

    Collection<ConfigEntity> configList();
    Collection<ConfigEntity> resolvedPlaceholdersConfigList();

    String resolvePlaceholders(String text);

    boolean containsKey(String key);

    void release();

    void loader(Collection<ConfigEntity> configEntityList);

    void setSubscription(ConfigSubscription configSubscription);

    void refresh();
}
