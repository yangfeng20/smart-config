package com.maple.smart.config.core.conflict;

import com.maple.smart.config.core.model.ConfigEntity;

/**
 * 合并，优先本地配置
 * @see com.maple.smart.config.core.conflict.ConflictStrategyEnum#MERGE_WITH_LOCAL_PRIORITY
 *
 * @author maple
 * @since 2025/06/27
 */
public class MergeWithLocalPriorityResolver implements ConfigConflictResolver {
    @Override
    public ConfigEntity resolve(String key, ConfigEntity local, ConfigEntity temp) {
        return local != null ? local : temp;
    }
}
