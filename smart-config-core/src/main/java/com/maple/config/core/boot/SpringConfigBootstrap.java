package com.maple.config.core.boot;

import com.maple.config.core.listener.ConfigListener;
import com.maple.config.core.loader.ConfigLoader;
import com.maple.config.core.repository.ConfigRepository;
import com.maple.config.core.subscription.ConfigSubscription;
import org.springframework.core.io.support.SpringFactoriesLoader;

import java.util.List;

/**
 * @author maple
 * Created Date: 2024/3/13 22:28
 * Description:
 */

public class SpringConfigBootstrap extends AbsConfigBootstrap {

    public SpringConfigBootstrap(boolean descInfer, int webUiPort, String localConfigPath, List<String> packagePathList) {
        super(descInfer, webUiPort, localConfigPath, packagePathList);
    }

    @Override
    public void loaderSpiImpl() {

        // 配置加载器
        configLoaderList.addAll(SpringFactoriesLoader.loadFactories(ConfigLoader.class, LocalConfigBootstrap.class.getClassLoader()));

        // 配置仓库
        configRepository = SpringFactoriesLoader.loadFactories(ConfigRepository.class, LocalConfigBootstrap.class.getClassLoader()).get(0);

        // 配置订阅者
        configSubscription = SpringFactoriesLoader.loadFactories(ConfigSubscription.class, LocalConfigBootstrap.class.getClassLoader()).get(0);
        this.configRepository.setSubscription(configSubscription);

        // 配置监听者
        SpringFactoriesLoader.loadFactories(ConfigListener.class, LocalConfigBootstrap.class.getClassLoader())
                .forEach(configListener -> configSubscription.addListener(configListener));
    }
}
