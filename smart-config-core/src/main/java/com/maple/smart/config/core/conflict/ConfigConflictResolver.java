package com.maple.smart.config.core.conflict;

import com.maple.smart.config.core.model.ConfigEntity;

/**
 * 配置冲突解决策略接口
 * <p>
 * <p>
 * 所有默认与自定义策略均需实现该接口
 *
 * @author gaoping
 * @since 2025/06/27
 */
public interface ConfigConflictResolver {
    /**
     * 冲突解决方法
     * @param key 配置key
     * @param local 本地配置实体
     * @param temp 临时目录配置实体
     * @return 最终采用的配置实体
     */
    ConfigEntity resolve(String key, ConfigEntity local, ConfigEntity temp);
}
