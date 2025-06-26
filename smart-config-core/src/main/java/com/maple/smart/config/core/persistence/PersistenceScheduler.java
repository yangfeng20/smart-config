package com.maple.smart.config.core.persistence;

import com.maple.smart.config.core.model.ConfigEntity;
import com.maple.smart.config.core.repository.ConfigRepository;

import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 定时持久化调度器
 */
public class PersistenceScheduler {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final ConfigPersistenceManager persistenceManager;
    private final ConfigRepository configRepository;
    private final long intervalMs;

    public PersistenceScheduler(ConfigPersistenceManager persistenceManager, ConfigRepository configRepository, long intervalMs) {
        this.persistenceManager = persistenceManager;
        this.configRepository = configRepository;
        this.intervalMs = intervalMs;
    }

    public void start() {
        scheduler.scheduleAtFixedRate(() -> {
            Collection<ConfigEntity> configs = configRepository.configList();
            persistenceManager.persist(configs);
        }, 10, intervalMs, TimeUnit.SECONDS);
    }

    public void stop() {
        scheduler.shutdownNow();
    }
}
