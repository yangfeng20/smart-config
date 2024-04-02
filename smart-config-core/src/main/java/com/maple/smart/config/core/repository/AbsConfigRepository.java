package com.maple.smart.config.core.repository;

import com.maple.smart.config.core.exp.SmartConfigApplicationException;
import com.maple.smart.config.core.model.ConfigEntity;
import com.maple.smart.config.core.subscription.ConfigSubscription;
import com.maple.smart.config.core.utils.PlaceholderResolverUtils;

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
                .peek(configEntity -> configEntity.setValue(PlaceholderResolverUtils.resolvePlaceholders(configEntity.getValue(), this::getConfig)))
                .collect(Collectors.toList());

    }

    @Override
    public void addConfig(ConfigEntity configEntity) {
        if (configEntity == null || configEntity.getKey() == null || configEntity.getKey().isEmpty()) {
            throw new SmartConfigApplicationException("config key is not empty");
        }

        configEntityMap.compute(configEntity.getKey(), (configKey, oldConfigEntity) -> {
            if (oldConfigEntity == null) {
                configEntity.setCreateDate(new Date());
                return configEntity;
            }
            configEntity.setUpdateDate(new Date());
            configEntity.setCreateDate(oldConfigEntity.getCreateDate());
            return configEntity;
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
        if (configEntity == null) {
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
        return PlaceholderResolverUtils.resolvePlaceholders(text, this::getConfig);
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
                .collect(Collectors.toMap(ConfigEntity::getKey, configEntity -> configEntity, (old, newV) -> newV)));
    }

    @Override
    public void refresh() {
        configSubscription.refresh(this);
    }
}
