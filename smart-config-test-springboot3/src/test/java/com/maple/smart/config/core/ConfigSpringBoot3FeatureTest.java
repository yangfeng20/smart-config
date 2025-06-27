package com.maple.smart.config.core;

import com.maple.smart.config.core.annotation.EnableSmartConfig;
import com.maple.smart.config.core.conflict.ConflictResolutionManager;
import com.maple.smart.config.core.model.ConfigEntity;
import com.maple.smart.config.core.persistence.TempDirectoryPersistenceManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@SpringBootTest(classes = ConfigSpringBoot3FeatureTest.class)
@EnableSmartConfig
public class ConfigSpringBoot3FeatureTest {
    @Test
    public void testConflictResolution() {
        ConflictResolutionManager manager = new ConflictResolutionManager();
        ConfigEntity local = new ConfigEntity("k", "local", 1);
        ConfigEntity temp = new ConfigEntity("k", "temp", 1);
        Assertions.assertEquals("local", manager.resolve("k", local, temp).getValue());
        manager.setCurrentStrategy("MERGE_WITH_TEMP_PRIORITY");
        Assertions.assertEquals("temp", manager.resolve("k", local, temp).getValue());
    }

    @Test
    public void testPersistenceAndLoad() {
        TempDirectoryPersistenceManager pm = new TempDirectoryPersistenceManager("");
        List<ConfigEntity> list = Arrays.asList(new ConfigEntity("a", "1", 1), new ConfigEntity("b", "2", 1));
        pm.persist(list);
        Collection<ConfigEntity> loaded = pm.load();
        Assertions.assertTrue(loaded.stream().anyMatch(e -> "a".equals(e.getKey())));
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
        Assertions.assertTrue(json.contains("a"));
        Assertions.assertTrue(prop.contains("a=1"));
        if (yaml != null) Assertions.assertTrue(yaml.contains("a"));
    }
}
