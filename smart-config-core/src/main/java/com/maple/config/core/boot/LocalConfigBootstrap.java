package com.maple.config.core.boot;

import com.maple.config.core.listener.ConfigListener;
import com.maple.config.core.loader.ConfigLoader;
import com.maple.config.core.repository.ConfigRepository;
import com.maple.config.core.subscription.ConfigSubscription;
import com.maple.config.core.utils.ClassUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * @author maple
 * Created Date: 2024/3/7 22:11
 * Description:
 */

public class LocalConfigBootstrap extends AbsConfigBootstrap {


    public LocalConfigBootstrap(boolean descInfer, int webUiPort, String localConfigPath, List<String> packagePathList) {
        super(descInfer, webUiPort, localConfigPath, packagePathList);
    }

    @Override
    public void init() {
        super.init();
        scanClassAndSubscription();

        this.refreshConfig();
    }

    private void scanClassAndSubscription() {
        // 扫描类并添加订阅
        List<Class<?>> scanClass = scanClass();
        for (Class<?> clazz : scanClass) {
            configSubscription.addSubscription(clazz);
        }
    }

    public List<Class<?>> scanClass() {
        if (packagePathList == null || packagePathList.isEmpty()) {
            throw new IllegalArgumentException("请指定包名路径");
        }

        List<Class<?>> scannerResult = new ArrayList<>();
        for (String packagePath : packagePathList) {
            try {
                List<Class<?>> classes = ClassUtils.getClasses(packagePath);
                scannerResult.addAll(classes);
            } catch (ClassNotFoundException | IOException e) {
                throw new RuntimeException(e);
            }
        }

        return scannerResult;
    }

    @Override
    public void loaderSpiImpl() {

        // 配置加载器
        ServiceLoader.load(ConfigLoader.class).forEach(configLoaderList::add);

        // 配置仓库
        ServiceLoader.load(ConfigRepository.class).forEach(configRepository -> this.configRepository = configRepository);

        // 配置订阅者
        ServiceLoader.load(ConfigSubscription.class).forEach(configSubscription -> {
            this.configSubscription = configSubscription;
            this.configRepository.setSubscription(configSubscription);
        });

        // 配置监听者
        ServiceLoader.load(ConfigListener.class).forEach(configListener -> configSubscription.addListener(configListener));
    }
}
