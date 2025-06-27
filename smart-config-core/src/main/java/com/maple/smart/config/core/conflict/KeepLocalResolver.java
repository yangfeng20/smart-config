package com.maple.smart.config.core.conflict;

import com.maple.smart.config.core.model.ConfigEntity;

/**
 * 保留本地配置，忽略临时目录配置
 * @see com.maple.smart.config.core.conflict.ConflictStrategyEnum#KEEP_LOCAL
 *
 * @author gaoping
 * @since 2025/06/27
 */
public class KeepLocalResolver implements ConfigConflictResolver {
    @Override
    public ConfigEntity resolve(String key, ConfigEntity local, ConfigEntity temp) {
        return local;
    }
}
