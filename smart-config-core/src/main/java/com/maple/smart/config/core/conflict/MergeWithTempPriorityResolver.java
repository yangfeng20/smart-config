package com.maple.smart.config.core.conflict;

import com.maple.smart.config.core.model.ConfigEntity;

/**
 * 合并，优先临时目录配置
 * @see com.maple.smart.config.core.conflict.ConflictStrategyEnum#MERGE_WITH_TEMP_PRIORITY
 *
 * @author maple
 * @since 2025/06/27
 */
public class MergeWithTempPriorityResolver implements ConfigConflictResolver {
    @Override
    public ConfigEntity resolve(String key, ConfigEntity local, ConfigEntity temp) {
        return temp != null ? temp : local;
    }
}
