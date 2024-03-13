package com.maple.config.core.repository;

import com.maple.config.core.model.ConfigEntity;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author maple
 * Created Date: 2024/3/7 21:31
 * Description:
 */

public class DefaultConfigRepository extends AbsConfigRepository {

    @Override
    public boolean addConfig(ConfigEntity configEntity) {
        return false;
    }

    @Override
    public boolean updateConfig(ConfigEntity configEntity) {
        return false;
    }

    @Override
    public ConfigEntity getConfigEntity(String key) {
        return null;
    }

    @Override
    public Object getConfig(String key) {
        return null;
    }

    @Override
    public Collection<ConfigEntity> configList() {
        return null;
    }

    @Override
    public boolean containsKey(String key) {
        return false;
    }

    @Override
    public void release() {

    }

    @Override
    public void loader(Collection<ConfigEntity> configEntityList) {

    }
}
