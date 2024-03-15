package com.maple.config.core.repository;

import com.maple.config.core.model.ConfigEntity;
import com.maple.config.core.subscription.ConfigSubscription;
import com.maple.config.core.utils.PlaceholderResolver;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
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

    protected Map<String, ConfigEntity> configEntityMap = new HashMap<>();

    protected ConfigSubscription configSubscription;

    protected List<String> waitReleaseKeyList;

    @Override
    public void setSubscription(ConfigSubscription configSubscription) {
        this.configSubscription = configSubscription;
    }

    @Override
    public Collection<ConfigEntity> resolvedPlaceholdersConfigList() {
        return configEntityMap.values().stream()
                .peek(configEntity -> configEntity.setValue(this.resolvePlaceholders(configEntity.getValue())))
                .collect(Collectors.toList());

    }

    @Override
    public boolean addConfig(ConfigEntity configEntity) {
        // todo 合并，实现
        return false;
    }

    @Override
    public boolean updateConfig(ConfigEntity configEntity) {
        return false;
    }

    @Override
    public ConfigEntity getConfigEntity(String key) {
        return configEntityMap.get(key);
    }

    @Override
    public String getConfig(String key) {
        return configEntityMap.getOrDefault(key, new ConfigEntity()).getValue();
    }

    @Override
    public boolean containsKey(String key) {
        return configEntityMap.containsKey(key);
    }

    @Override
    public Collection<ConfigEntity> configList() {
        return configEntityMap.values();
    }

    @Override
    public String resolvePlaceholders(String text) {
        return PlaceholderResolver.defResolveText(text, key -> {
            ConfigEntity configEntity = configEntityMap.get(key);
            return configEntity == null ? key : configEntity.getValue();
        });
    }

    @Override
    public void release() {
        List<ConfigEntity> waitReleaseConfigList = waitReleaseKeyList.stream()
                .map(configKey -> configEntityMap.get(configKey)).collect(Collectors.toList());
        configSubscription.subscribe(waitReleaseConfigList);
        waitReleaseKeyList.clear();
        lastReleaseTime = LocalDateTime.now();
    }

    @Override
    public void loader(Collection<ConfigEntity> configEntityList) {
        configEntityMap.putAll(configEntityList.stream()
                .collect(Collectors.toMap(ConfigEntity::getKey, configEntity -> configEntity)));
    }

    @Override
    public void refresh() {
        configSubscription.refresh(this);
    }
}
