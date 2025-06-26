package com.maple.smart.config.core.conflict;

import com.maple.smart.config.core.model.ConfigEntity;
import java.util.HashMap;
import java.util.Map;

/**
 * 策略管理器，统一注册和调度所有冲突解决策略
 */
public class ConflictResolutionManager {
    private final Map<String, ConfigConflictResolver> resolverMap = new HashMap<>();
    private ConfigConflictResolver currentResolver;

    public ConflictResolutionManager() {
        // 注册默认策略
        register("MERGE_WITH_LOCAL_PRIORITY", new MergeWithLocalPriorityResolver());
        register("MERGE_WITH_TEMP_PRIORITY", new MergeWithTempPriorityResolver());
        register("OVERRIDE_LOCAL", new OverrideLocalResolver());
        register("KEEP_LOCAL", new KeepLocalResolver());
        // 默认策略
        setCurrentStrategy("MERGE_WITH_LOCAL_PRIORITY");
    }

    public void register(String name, ConfigConflictResolver resolver) {
        resolverMap.put(name, resolver);
    }

    public void setCurrentStrategy(String name) {
        this.currentResolver = resolverMap.getOrDefault(name, resolverMap.get("MERGE_WITH_LOCAL_PRIORITY"));
    }

    public ConfigConflictResolver getCurrentResolver() {
        return currentResolver;
    }

    public ConfigEntity resolve(String key, ConfigEntity local, ConfigEntity temp) {
        return currentResolver.resolve(key, local, temp);
    }
}
