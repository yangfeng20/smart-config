package com.maple.config.core.boot;

import com.maple.config.core.listener.ConfigListener;
import com.maple.config.core.loader.ConfigLoader;
import com.maple.config.core.repository.ConfigRepository;
import com.maple.config.core.subscription.ConfigSubscription;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.util.CollectionUtils;

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
