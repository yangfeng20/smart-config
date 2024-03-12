package com.maple.config.core.repository;

import com.maple.config.core.model.ConfigEntity;

import java.util.Collection;

/**
 * @author maple
 * Created Date: 2024/3/7 21:18
 * Description:
 */

public interface ConfigRepository {

    boolean addConfig(ConfigEntity configEntity);

    boolean updateConfig(ConfigEntity configEntity);

    ConfigEntity getConfigEntity(String key);

    Object getConfig(String key);

    Collection<ConfigEntity> configList();

    boolean containsKey(String key);

    void release();

    void loader(Collection<ConfigEntity> configEntityList);
}
