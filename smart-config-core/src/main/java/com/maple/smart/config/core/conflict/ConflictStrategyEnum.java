package com.maple.smart.config.core.conflict;

/**
 * 冲突策略枚举
 *
 * @author maple
 * @since 2025/06/27
 */
public enum ConflictStrategyEnum {
    /**
     * 合并配置，本地配置优先
     */
    MERGE_WITH_LOCAL_PRIORITY,
    /**
     * 合并配置，临时配置优先
     */
    MERGE_WITH_TEMP_PRIORITY,
    /**
     * 临时目录配置覆盖本地配置
     */
    OVERRIDE_LOCAL,
    /**
     * 保留本地配置，忽略临时目录配置
     */
    KEEP_LOCAL
}
