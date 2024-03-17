package com.maple.config.core.boot;

import com.maple.config.core.control.WebOperationControlPanel;
import com.maple.config.core.exp.SmartConfigApplicationException;
import com.maple.config.core.loader.ConfigLoader;
import com.maple.config.core.model.ConfigEntity;
import com.maple.config.core.repository.ConfigRepository;
import com.maple.config.core.subscription.ConfigSubscription;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author maple
 * @since 2024/3/13 22:28
 * Description:
 */

public abstract class AbsConfigBootstrap implements SmartConfigBootstrap {
    /**
     * 配置描述推断
     */
    protected final boolean descInfer;

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
    }


    /**
     * 通过spi加载实现
     * spring or java spi
     */
    public abstract void loaderSpiImpl();

    public void init() {
        loaderSpiImpl();
        loaderConfigToRepository();
    }

    @Override
    public void refreshConfig() {
        configRepository.refresh();

        if (!started) {
            startWebUi();
            started = true;
        }
    }

    /**
     * 加载配置到存储库
     */
    public void loaderConfigToRepository() {
        for (ConfigLoader configLoader : configLoaderList) {
            Collection<ConfigEntity> configEntityList = configLoader.loaderConfig(localConfigPath);
            configRepository.loader(configEntityList);
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
            } catch (Exception e) {
                throw new SmartConfigApplicationException("Smart-config:启动webUi失败", e);
            }
        }, "smartConfig-web").start();
    }
}
