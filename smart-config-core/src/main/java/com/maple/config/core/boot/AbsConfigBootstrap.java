package com.maple.config.core.boot;

import com.maple.config.core.control.WebOperationControlPanel;
import com.maple.config.core.loader.ConfigLoader;
import com.maple.config.core.model.ConfigEntity;
import com.maple.config.core.repository.ConfigRepository;
import com.maple.config.core.subscription.ConfigSubscription;
import com.maple.config.core.subscription.LocalConfigSubscription;
import com.maple.config.core.utils.ClassScanner;
import lombok.Getter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author maple
 * Created Date: 2024/3/13 22:28
 * Description:
 */

public abstract class AbsConfigBootstrap implements SmartConfigBootstrap {
    /**
     * 配置描述推断
     */
    private final boolean descInfer;

    /**
     * WebUI端口
     */
    private int webUiPort;

    /**
     * 本地文件地址
     */
    private final String localConfigPath;

    /**
     * 类扫描路径
     */
    private final List<String> packagePathList;

    public AbsConfigBootstrap(boolean descInfer, int webUiPort, String localConfigPath, List<String> packagePathList) {
        this.descInfer = descInfer;
        this.webUiPort = webUiPort;
        this.localConfigPath = localConfigPath;
        this.packagePathList = packagePathList;
    }

    @Getter
    protected ConfigRepository configRepository;

    protected final List<ConfigLoader> configLoaderList = new ArrayList<>();

    @Getter
    protected ConfigSubscription configSubscription = new LocalConfigSubscription();

    public void init() {
        loaderSpiImpl();
        loaderConfigToRepository();
    }

    public void start() {
        // 扫描类并添加订阅
        List<Class<?>> scanClass = scanClass();
        for (Class<?> clazz : scanClass) {
            configSubscription.addSubscription(clazz);
        }


        configRepository.refresh();
    }

    public abstract void loaderSpiImpl();

    public void loaderConfigToRepository() {
        for (ConfigLoader configLoader : configLoaderList) {
            Collection<ConfigEntity> configEntityList = configLoader.loaderConfig(localConfigPath);
            configRepository.loader(configEntityList);
        }
    }


    public List<Class<?>> scanClass() {
        if (packagePathList == null || packagePathList.isEmpty()) {
            throw new IllegalArgumentException("请指定包名路径");
        }

        List<Class<?>> scannerResult = new ArrayList<>();
        for (String packagePath : packagePathList) {
            try {
                List<Class<?>> classes = ClassScanner.getClasses(packagePath);
                scannerResult.addAll(classes);
            } catch (ClassNotFoundException | IOException e) {
                throw new RuntimeException(e);
            }
        }

        return scannerResult;
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
                throw new RuntimeException("Smart-config:启动webUi失败", e);
            }
        }).start();
    }
}
