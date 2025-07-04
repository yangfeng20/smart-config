package com.maple.smart.config.core.boot;

import com.maple.smart.config.core.conflict.ConfigConflictResolver;
import com.maple.smart.config.core.conflict.ConflictResolutionManager;
import com.maple.smart.config.core.control.WebOperationControlPanel;
import com.maple.smart.config.core.exp.SmartConfigApplicationException;
import com.maple.smart.config.core.loader.ConfigLoader;
import com.maple.smart.config.core.model.ConfigEntity;
import com.maple.smart.config.core.persistence.ConfigPersistenceManager;
import com.maple.smart.config.core.persistence.PersistenceScheduler;
import com.maple.smart.config.core.persistence.TempDirectoryPersistenceManager;
import com.maple.smart.config.core.repository.ConfigRepository;
import com.maple.smart.config.core.subscription.ConfigSubscription;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author maple
 * @since 2024/3/13 22:28
 * Description:
 */

@Slf4j
public abstract class AbsConfigBootstrap implements SmartConfigBootstrap {
    /**
     * 配置描述推断
     */
    protected final boolean descInfer;

    /**
     * 配置描述推断
     */
    protected final boolean defaultValEcho;

    /**
     * WebUI端口
     */
    protected int webUiPort;

    /**
     * 本地文件地址
     */
    protected final String localConfigPath;

    /**
     * 类扫描路径
     */
    protected final List<String> packagePathList;

    /**
     *
     */
    protected boolean started;


    @Getter
    protected ConfigRepository configRepository;

    @Getter
    protected ConfigSubscription configSubscription;

    protected final List<ConfigLoader> configLoaderList = new ArrayList<>();

    protected final String conflictStrategy;

    protected final ConflictResolutionManager conflictResolutionManager = new ConflictResolutionManager();

    protected ConfigPersistenceManager configPersistenceManager;
    protected PersistenceScheduler persistenceScheduler;

    // 默认5分钟
    protected long persistenceIntervalMinutes = 60;

    protected ConfigConflictResolver customResolver;

    protected final String projectName;

    public AbsConfigBootstrap(boolean descInfer, int webUiPort, String localConfigPath, List<String> packagePathList, String conflictStrategy) {
        this(descInfer, webUiPort, localConfigPath, packagePathList, false, conflictStrategy, null, resolveProjectName());
    }

    public AbsConfigBootstrap(boolean descInfer, int webUiPort, String localConfigPath, List<String> packagePathList, boolean defaultValEcho, String conflictStrategy) {
        this(descInfer, webUiPort, localConfigPath, packagePathList, defaultValEcho, conflictStrategy, null, resolveProjectName());
    }

    public AbsConfigBootstrap(boolean descInfer, int webUiPort, String localConfigPath, List<String> packagePathList, boolean defaultValEcho, String conflictStrategy, ConfigConflictResolver customResolver) {
        this(descInfer, webUiPort, localConfigPath, packagePathList, defaultValEcho, conflictStrategy, customResolver, resolveProjectName());
    }

    public AbsConfigBootstrap(boolean descInfer, int webUiPort, String localConfigPath, List<String> packagePathList, boolean defaultValEcho, String conflictStrategy, ConfigConflictResolver customResolver, String projectName) {
        this.descInfer = descInfer;
        this.webUiPort = webUiPort;
        this.localConfigPath = localConfigPath;
        this.packagePathList = packagePathList;
        this.defaultValEcho = defaultValEcho;
        this.conflictStrategy = conflictStrategy;
        this.conflictResolutionManager.setCurrentStrategy(conflictStrategy);
        this.customResolver = customResolver;
        if (customResolver != null) {
            this.conflictResolutionManager.registerCustomResolver(customResolver);
        }
        this.projectName = projectName;
    }


    /**
     * 通过spi加载实现
     * spring or java spi
     */
    public abstract void loaderSpiImpl();

    /**
     * 初始化流程，加载临时目录配置并合并，启动定时持久化
     */
    @Override
    public void init() {
        log.info("Smart-Config init begin");
        loaderSpiImpl();
        log.debug("Smart-Config 加载spi实现完成");

        loaderConfigToRepository();
        log.debug("Smart-Config 加载配置到本地仓库完成");

    }

    @Override
    public void refreshConfig() {
        configRepository.refresh();
        log.debug("Smart-Config 本地配置刷新");

        if (!started) {
            doStart();
        }
    }

    /**
     * 加载配置到存储库
     */
    public void loaderConfigToRepository() {
        // 1. 加载本地配置
        Collection<ConfigEntity> localConfigs = new java.util.ArrayList<>();
        for (String path : localConfigPath.split(";")) {
            for (ConfigLoader configLoader : configLoaderList) {
                localConfigs.addAll(configLoader.loaderConfig(path));
            }
        }

        // 2. 加载临时目录配置
        configPersistenceManager = new TempDirectoryPersistenceManager(projectName);
        Collection<ConfigEntity> tempConfigs = configPersistenceManager.load();

        // 3. 合并配置
        Collection<ConfigEntity> merged = mergeConfig(localConfigs, tempConfigs);
        configRepository.loader(merged);

        String username = configRepository.getConfig("smart.username");
        String password = configRepository.getConfig("smart.password");
        if (username == null || password == null) {
            throw new SmartConfigApplicationException("Smart-config: 【smart.username】and【smart.password】cannot be empty in profile [" + localConfigPath + "]");
        }
    }

    public void doStart() {
        startWebUi();

        // 启动配置定时持久化
        persistenceScheduler = new PersistenceScheduler(configPersistenceManager, configRepository, persistenceIntervalMinutes);
        persistenceScheduler.start();

        started = true;

    }

    public void startWebUi() {
        if (webUiPort == 0) {
            webUiPort = 6767;
        }
        WebOperationControlPanel webOperationControlPanel = new WebOperationControlPanel(configRepository, webUiPort);
        new Thread(() -> {
            try {
                webOperationControlPanel.start();
                log.info("Smart-Config WebUi start success with port(s): " + webUiPort);
            } catch (Exception e) {
                throw new SmartConfigApplicationException("Smart-config: start webui fail", e);
            }
        }, "SmartConfig-web").start();
    }

    /**
     * 合并本地配置与临时目录配置，统一走冲突策略
     */
    protected Collection<ConfigEntity> mergeConfig(Collection<ConfigEntity> local, Collection<ConfigEntity> temp) {
        java.util.Map<String, ConfigEntity> result = new java.util.HashMap<>();
        if (local != null) {
            for (ConfigEntity entity : local) {
                result.put(entity.getKey(), entity);
            }
        }
        if (temp != null) {
            for (ConfigEntity tempEntity : temp) {
                String key = tempEntity.getKey();
                ConfigEntity localEntity = result.get(key);
                if (localEntity != null) {
                    ConfigEntity resolved = conflictResolutionManager.resolve(key, localEntity, tempEntity);
                    result.put(key, resolved);
                } else {
                    result.put(key, tempEntity);
                }
            }
        }
        return result.values();
    }

    /**
     * 获取项目名，优先级：
     * 1. Spring环境 application.name
     * 2. 系统属性 smart.config.project.name
     * 3. 环境变量 SMART_CONFIG_PROJECT_NAME
     * 4. 当前工作目录名
     */
    public static String resolveProjectName(org.springframework.core.env.Environment env) {
        String name = null;
        if (env != null) {
            name = env.getProperty("spring.application.name");
        }
        if (name == null || name.isEmpty()) {
            name = System.getProperty("smart.config.project.name");
        }
        if (name == null || name.isEmpty()) {
            name = System.getenv("SMART_CONFIG_PROJECT_NAME");
        }
        if (name == null || name.isEmpty()) {
            try {
                name = new java.io.File(".").getCanonicalFile().getName();
            } catch (Exception ignore) {}
        }
        return name == null ? "default" : name;
    }

    /**
     * 获取项目名，非Spring环境优先系统属性/环境变量/工作目录名
     */
    public static String resolveProjectName() {
        return resolveProjectName(null);
    }
}
