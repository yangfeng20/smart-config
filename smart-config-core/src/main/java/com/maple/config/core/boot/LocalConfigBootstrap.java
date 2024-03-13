package com.maple.config.core.boot;

import com.maple.config.core.control.WebOperationControlPanel;
import com.maple.config.core.listener.ConfigListener;
import com.maple.config.core.loader.ConfigLoader;
import com.maple.config.core.model.ConfigEntity;
import com.maple.config.core.repository.ConfigRepository;
import com.maple.config.core.subscription.ConfigSubscription;
import com.maple.config.core.subscription.LocalConfigSubscription;
import com.maple.config.core.utils.ClassScanner;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;

/**
 * @author maple
 * Created Date: 2024/3/7 22:11
 * Description:
 */

public class LocalConfigBootstrap {

    /**
     * 配置描述推断
     */
    @Setter
    private boolean descInfer;

    /**
     * WebUI端口
     */
    private int webUiPort;

    /**
     * 本地文件地址
     */
    @Setter
    private String localConfigPath;

    /**
     * 类扫描路径
     */
    @Setter
    private List<String> packagePathList;


    private ConfigRepository configRepository;

    private final List<ConfigLoader> configLoaderList = new ArrayList<>();

    private ConfigSubscription configSubscription = new LocalConfigSubscription();

    public void init() {

        loaderSpiImpl();

        loaderConfigToRepository();

        // 扫描类并添加订阅
        List<Class<?>> scanClass = scanClass();
        for (Class<?> clazz : scanClass) {
            configSubscription.addSubscription(clazz);
        }


        configRepository.refresh();
    }

    private void loaderSpiImpl() {

        // 配置加载器
        List<ConfigLoader> sipImplConfigLoaderList = SpringFactoriesLoader.loadFactories(ConfigLoader.class, LocalConfigBootstrap.class.getClassLoader());
        configLoaderList.addAll(sipImplConfigLoaderList);
        if (CollectionUtils.isEmpty(configLoaderList)) {
            ServiceLoader.load(ConfigLoader.class).forEach(configLoaderList::add);
        }

        // 配置仓库
        configRepository = SpringFactoriesLoader.loadFactories(ConfigRepository.class, LocalConfigBootstrap.class.getClassLoader()).get(0);
        if (configRepository == null) {
            ServiceLoader.load(ConfigRepository.class).forEach(configRepository -> {
                this.configRepository = configRepository;
            });
        }

        // 配置订阅者
        configSubscription = SpringFactoriesLoader.loadFactories(ConfigSubscription.class, LocalConfigBootstrap.class.getClassLoader()).get(0);
        this.configRepository.setSubscription(configSubscription);
        if (configSubscription == null) {
            ServiceLoader.load(ConfigSubscription.class).forEach(configSubscription -> {
                this.configSubscription = configSubscription;
                this.configRepository.setSubscription(configSubscription);
            });
        }

        // 配置监听者
        SpringFactoriesLoader.loadFactories(ConfigListener.class, LocalConfigBootstrap.class.getClassLoader())
                .forEach(configListener -> configSubscription.addListener(configListener));
        ServiceLoader.load(ConfigListener.class).forEach(configListener -> configSubscription.addListener(configListener));
    }

    private void loaderConfigToRepository() {
        for (ConfigLoader configLoader : configLoaderList) {
            Collection<ConfigEntity> configEntityList = configLoader.loaderConfig(localConfigPath);
            configRepository.loader(configEntityList);
        }
    }


    private List<Class<?>> scanClass() {
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

    private void startWebUi() {
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
