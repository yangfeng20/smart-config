package com.maple.smart.config.core.boot;

import com.maple.smart.config.core.listener.ConfigListener;
import com.maple.smart.config.core.loader.ConfigLoader;
import com.maple.smart.config.core.repository.ConfigRepository;
import com.maple.smart.config.core.subscription.ConfigSubscription;
import org.springframework.core.io.support.SpringFactoriesLoader;

import java.util.List;

/**
 * @author maple
 * @since 2024/3/13 22:28
 * Description:
 */

public class SpringConfigBootstrap extends AbsConfigBootstrap {

    public SpringConfigBootstrap(boolean descInfer, boolean defaultValEcho, int webUiPort, String localConfigPath, List<String> packagePathList) {
        super(descInfer, webUiPort, localConfigPath, packagePathList, defaultValEcho);
    }

    @Override
    public void loaderSpiImpl() {

        // 配置加载器
        SpringFactoriesLoader.loadFactories(ConfigLoader.class, LocalConfigBootstrap.class.getClassLoader())
                .forEach(configLoader -> {
                    configLoader.setConfigInferDesc(descInfer);
                    configLoaderList.add(configLoader);
                });

        // 配置仓库
        SpringFactoriesLoader.loadFactories(ConfigRepository.class, LocalConfigBootstrap.class.getClassLoader())
                .forEach(configRepository -> this.configRepository = configRepository);

        // 配置订阅者
        SpringFactoriesLoader.loadFactories(ConfigSubscription.class, LocalConfigBootstrap.class.getClassLoader()).forEach(configSubscription -> {
            this.configSubscription = configSubscription;
            this.configSubscription.setConfigRepository(this.configRepository);
            this.configSubscription.setDefaultValEcho(defaultValEcho);
            this.configRepository.setSubscription(configSubscription);
        });

        // 配置监听者
        SpringFactoriesLoader.loadFactories(ConfigListener.class, LocalConfigBootstrap.class.getClassLoader())
                .forEach(configListener -> {
                    configListener.setConfigSubscription(this.configSubscription);
                    configSubscription.addListener(configListener);
                });
    }

    @Override
    public void refreshConfig() {
        // spring应用不需要手动刷新（触发字段赋值）；在beanPostProcessor中处理了
        configSubscription.refresh(configRepository);
        // todo 这里导致无法回显，重复刷新会有问题吗？有问题件，要改造
        if (!started) {
            startWebUi();
            started = true;
        }
    }
}
