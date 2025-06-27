package com.maple.smart.config.core.conflict;

import com.maple.smart.config.core.model.ConfigEntity;

/**
 * 临时目录配置覆盖本地配置
 * @see com.maple.smart.config.core.conflict.ConflictStrategyEnum#OVERRIDE_LOCAL
 *
 * @author gaoping
 * @since 2025/06/27
 */
public class OverrideLocalResolver implements ConfigConflictResolver {
    @Override
    public ConfigEntity resolve(String key, ConfigEntity local, ConfigEntity temp) {
        return temp;
    }
}
