package com.maple.smart.config.core.persistence;

import com.maple.smart.config.core.model.ConfigEntity;
import java.util.Collection;

/**
 * 配置持久化管理接口
 */
public interface ConfigPersistenceManager {
    /**
     * 持久化配置到临时目录
     */
    void persist(Collection<ConfigEntity> configList);

    /**
     * 从临时目录加载配置
     */
    Collection<ConfigEntity> load();
}
