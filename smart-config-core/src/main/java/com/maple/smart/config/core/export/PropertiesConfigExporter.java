package com.maple.smart.config.core.export;

import com.maple.smart.config.core.model.ConfigEntity;
import java.util.Collection;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Properties格式配置导出实现
 *
 * @author maple
 * @since 2025/06/30
 */
public class PropertiesConfigExporter implements ConfigExporter {
    @Override
    public String export(Collection<ConfigEntity> configList) {
        Properties props = new Properties();
        for (ConfigEntity entity : configList) {
            props.setProperty(entity.getKey(), entity.getValue() == null ? "" : entity.getValue());
        }
        return props.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("\n"));
    }
}
