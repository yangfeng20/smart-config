package com.maple.smart.config.core.conflict;

import com.maple.smart.config.core.model.ConfigEntity;
import java.util.HashMap;
import java.util.Map;

/**
 * 策略管理器，统一注册和调度所有冲突解决策略
 *
 * @author maple
 * @since 2025/06/30
 */
public class ConflictResolutionManager {
    private final Map<ConflictStrategyEnum, ConfigConflictResolver> resolverMap = new HashMap<>();
    private ConfigConflictResolver currentResolver;
    private ConfigConflictResolver customResolver;

    public ConflictResolutionManager() {
        // 注册默认策略
        resolverMap.put(ConflictStrategyEnum.MERGE_WITH_LOCAL_PRIORITY, new MergeWithLocalPriorityResolver());
        resolverMap.put(ConflictStrategyEnum.MERGE_WITH_TEMP_PRIORITY, new MergeWithTempPriorityResolver());
        resolverMap.put(ConflictStrategyEnum.OVERRIDE_LOCAL, new OverrideLocalResolver());
        resolverMap.put(ConflictStrategyEnum.KEEP_LOCAL, new KeepLocalResolver());
        // 默认策略
        setCurrentStrategy(ConflictStrategyEnum.MERGE_WITH_LOCAL_PRIORITY);
    }

    public void registerCustomResolver(ConfigConflictResolver resolver) {
        this.customResolver = resolver;
    }

    public void setCurrentStrategy(ConflictStrategyEnum strategy) {
        this.currentResolver = resolverMap.getOrDefault(strategy, resolverMap.get(ConflictStrategyEnum.MERGE_WITH_LOCAL_PRIORITY));
    }

    public void setCurrentStrategy(String strategyName) {
        try {
            setCurrentStrategy(ConflictStrategyEnum.valueOf(strategyName));
        } catch (Exception e) {
            setCurrentStrategy(ConflictStrategyEnum.MERGE_WITH_LOCAL_PRIORITY);
        }
    }

    public ConfigConflictResolver getCurrentResolver() {
        return customResolver != null ? customResolver : currentResolver;
    }

    public ConfigEntity resolve(String key, ConfigEntity local, ConfigEntity temp) {
        return getCurrentResolver().resolve(key, local, temp);
    }
}
