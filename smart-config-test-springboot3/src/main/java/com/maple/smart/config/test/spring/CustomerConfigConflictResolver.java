package com.maple.smart.config.test.spring;

import com.maple.smart.config.core.conflict.MergeWithLocalPriorityResolver;
import com.maple.smart.config.core.model.ConfigEntity;
import com.maple.smart.config.core.model.ReleaseStatusEnum;

/**
 * 客户配置冲突解决程序
 *
 * @author maple
 * @since 2025/06/30
 */
public class CustomerConfigConflictResolver extends MergeWithLocalPriorityResolver {
    @Override
    public ConfigEntity resolve(String key, ConfigEntity local, ConfigEntity temp) {
        if ("prod".equals(key)) {
            return new ConfigEntity(key, "自定义数据", ReleaseStatusEnum.RELEASE.getCode());
        }
        return super.resolve(key, local, temp);
    }
}
