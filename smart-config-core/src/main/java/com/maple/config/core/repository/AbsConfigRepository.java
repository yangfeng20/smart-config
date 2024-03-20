package com.maple.config.core.repository;

import com.maple.config.core.exp.SmartConfigApplicationException;
import com.maple.config.core.model.ConfigEntity;
import com.maple.config.core.subscription.ConfigSubscription;
import com.maple.config.core.utils.PlaceholderResolver;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author maple
 * @since 2024/3/7 21:31
 * Description:
 */

public abstract class AbsConfigRepository implements ConfigRepository {

    protected LocalDateTime lastReleaseTime;

    protected Map<String, ConfigEntity> configEntityMap = new HashMap<>();

    protected ConfigSubscription configSubscription;

    protected List<String> waitReleaseKeyList = new ArrayList<>();

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
    public void addConfig(ConfigEntity configEntity) {
        if (configEntity == null || configEntity.getKey() == null || configEntity.getKey().isEmpty()) {
            throw new SmartConfigApplicationException("config key is not empty");
        }

        boolean containsKey = configEntityMap.containsKey(configEntity.getKey());
        configEntityMap.compute(configEntity.getKey(), (configKey, oldConfigEntity) -> {
            if (oldConfigEntity == null) {
                return configEntity;
            }
            oldConfigEntity.setValue(configEntity.getValue());
            oldConfigEntity.setStatus(configEntity.getStatus());
            oldConfigEntity.setUpdateDate(new Date());
            return oldConfigEntity;
        });
        waitReleaseKeyList.add(configEntity.getKey());
    }

    @Override
    public String getConfig(String key) {
        return configEntityMap.getOrDefault(key, new ConfigEntity()).getValue();
    }

    @Override
    public ConfigEntity getConfigEntity(String key) {
        ConfigEntity configEntity = configEntityMap.get(key);
        if (configEntity == null){
            return null;
        }
        String resolveValue = this.resolvePlaceholders(configEntity.getValue());
        configEntity.setValue(resolveValue);
        return configEntity;
    }

    @Override
    public ConfigEntity getOriginalConfigEntity(String key) {
        return configEntityMap.get(key);
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
