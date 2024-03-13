package com.maple.config.core.repository;

import com.maple.config.core.model.ConfigEntity;
import com.maple.config.core.subscription.ConfigSubscription;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author maple
 * Created Date: 2024/3/7 21:31
 * Description:
 */

public abstract class AbsConfigRepository implements ConfigRepository {

    protected LocalDateTime lastReleaseTime;

    protected Map<String, ConfigEntity> configEntityMap;

    protected ConfigSubscription configSubscription;

    protected List<String> waitReleaseKeyList;

    @Override
    public void setSubscription(ConfigSubscription configSubscription) {
        this.configSubscription = configSubscription;
    }

    @Override
    public void release() {
        List<ConfigEntity> waitReleaseConfigList = waitReleaseKeyList.stream()
                .map(configKey -> configEntityMap.get(configKey)).collect(Collectors.toList());
        configSubscription.subscribe(waitReleaseConfigList);
        waitReleaseKeyList.clear();
    }

    @Override
    public void loader(Collection<ConfigEntity> configEntityList) {
        if (configEntityMap == null) {
            configEntityMap = configEntityList.stream().collect(Collectors.toMap(ConfigEntity::getKey, configEntity -> {
                //if (configEntity.getValue().)
                return configEntity;
            }));
        }

        configEntityMap.putAll(configEntityList.stream().collect(Collectors.toMap(ConfigEntity::getKey, configEntity -> configEntity)));
    }

    @Override
    public void refresh() {
        configSubscription.refresh(this);
    }
}
