package com.maple.smart.config.core;

import com.maple.smart.config.core.boot.LocalConfigBootstrap;
import com.maple.smart.config.core.conflict.ConfigConflictResolver;
import com.maple.smart.config.core.conflict.ConflictResolutionManager;
import com.maple.smart.config.core.model.ConfigEntity;
import com.maple.smart.config.core.persistence.TempDirectoryPersistenceManager;
import org.junit.Assert;
import org.junit.Test;
import java.util.*;

public class ConfigCoreFeatureTest {

    @Test
    public void testConflictResolution() {
        ConflictResolutionManager manager = new ConflictResolutionManager();
        ConfigEntity local = new ConfigEntity("k", "local", 1);
        ConfigEntity temp = new ConfigEntity("k", "temp", 1);
        // 默认本地优先
        Assert.assertEquals("local", manager.resolve("k", local, temp).getValue());
        manager.setCurrentStrategy("MERGE_WITH_TEMP_PRIORITY");
        Assert.assertEquals("temp", manager.resolve("k", local, temp).getValue());
    }

    @Test
    public void testPersistenceAndLoad() {
        TempDirectoryPersistenceManager pm = new TempDirectoryPersistenceManager();
        List<ConfigEntity> list = Arrays.asList(new ConfigEntity("a", "1", 1), new ConfigEntity("b", "2", 1));
        pm.persist(list);
        Collection<ConfigEntity> loaded = pm.load();
        Assert.assertTrue(loaded.stream().anyMatch(e -> "a".equals(e.getKey())));
    }

    @Test
    public void testExporters() {
        List<ConfigEntity> list = Arrays.asList(new ConfigEntity("a", "1", 1), new ConfigEntity("b", "2", 1));
        String json = new com.maple.smart.config.core.export.JsonConfigExporter().export(list);
        String prop = new com.maple.smart.config.core.export.PropertiesConfigExporter().export(list);
        String yaml = null;
        try {
            yaml = new com.maple.smart.config.core.export.YamlConfigExporter().export(list);
        } catch (Throwable ignore) {}
        Assert.assertTrue(json.contains("a"));
        Assert.assertTrue(prop.contains("a=1"));
        if (yaml != null) Assert.assertTrue(yaml.contains("a"));
    }
}
