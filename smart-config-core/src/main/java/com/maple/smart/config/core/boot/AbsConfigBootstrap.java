package com.maple.smart.config.core.boot;

import com.maple.smart.config.core.control.WebOperationControlPanel;
import com.maple.smart.config.core.exp.SmartConfigApplicationException;
import com.maple.smart.config.core.loader.ConfigLoader;
import com.maple.smart.config.core.model.ConfigEntity;
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

    public AbsConfigBootstrap(boolean descInfer, int webUiPort, String localConfigPath, List<String> packagePathList) {
        this.descInfer = descInfer;
        this.webUiPort = webUiPort;
        this.localConfigPath = localConfigPath;
        this.packagePathList = packagePathList;
        this.defaultValEcho = false;
    }

    public AbsConfigBootstrap(boolean descInfer, int webUiPort, String localConfigPath, List<String> packagePathList, boolean defaultValEcho) {
        this.descInfer = descInfer;
        this.webUiPort = webUiPort;
        this.localConfigPath = localConfigPath;
        this.packagePathList = packagePathList;
        this.defaultValEcho = defaultValEcho;
    }


    /**
     * 通过spi加载实现
     * spring or java spi
     */
    public abstract void loaderSpiImpl();

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
            startWebUi();
            started = true;
        }
    }

    /**
     * 加载配置到存储库
     */
    public void loaderConfigToRepository() {
        for (String path : localConfigPath.split(";")) {
            for (ConfigLoader configLoader : configLoaderList) {
                Collection<ConfigEntity> configEntityList = configLoader.loaderConfig(path);
                configRepository.loader(configEntityList);
            }
        }

        String username = configRepository.getConfig("smart.username");
        String password = configRepository.getConfig("smart.password");
        if (username == null || password == null) {
            throw new SmartConfigApplicationException("Smart-config: 【smart.username】and【smart.password】cannot be empty in profile [" + localConfigPath + "]");
        }
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
}
