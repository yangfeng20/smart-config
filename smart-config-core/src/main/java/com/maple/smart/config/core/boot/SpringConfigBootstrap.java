package com.maple.smart.config.core.boot;

import com.maple.smart.config.core.inject.PropertyInject;
import com.maple.smart.config.core.listener.ConfigListener;
import com.maple.smart.config.core.loader.ConfigLoader;
import com.maple.smart.config.core.repository.ConfigRepository;
import com.maple.smart.config.core.spring.PropertySubscriptionInjectBeanPostProcessor;
import com.maple.smart.config.core.subscription.ConfigSubscription;
import org.springframework.beans.PropertyValues;
import org.springframework.core.io.support.SpringFactoriesLoader;

import java.util.List;

/**
 * @author maple
 * @since 2024/3/13 22:28
 * Description:
 */

public class SpringConfigBootstrap extends AbsConfigBootstrap {

    public SpringConfigBootstrap(boolean descInfer, boolean defaultValEcho, int webUiPort, String localConfigPath, List<String> packagePathList) {
        super(descInfer, webUiPort, localConfigPath, packagePathList, defaultValEcho, "MERGE_WITH_LOCAL_PRIORITY");
    }

    public SpringConfigBootstrap(boolean descInfer, boolean defaultValEcho, int webUiPort, String localConfigPath, List<String> packagePathList, String conflictStrategy) {
        super(descInfer, webUiPort, localConfigPath, packagePathList, defaultValEcho, conflictStrategy);
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


    /**
     * 在spring中，最终还是需要调用refresh方法；再次触发字段赋值，确保默认值回显逻辑被执行
     * <pre>
     *{@code
            configSubscription.refresh(configRepository);
     *}<pre>
     *  默认值回显
     *  <pre>
     * {@code
     *          // 默认值回显
     *         if (!defaultValEcho && defaultValEchoConfigList.isEmpty()) {
     *             return;
     *         }
     *         configRepository.loader(defaultValEchoConfigList);
     * }
     *
     * 最好不要移除下面方法的字段赋值逻辑，框架在初始化过程中，就需要或者字段的值，所以最好不要移除，页不能统一放到最后这里执行
     * @see PropertySubscriptionInjectBeanPostProcessor#postProcessProperties(PropertyValues, Object, String)
     */
    @Override
    public void refreshConfig() {
        // spring应用不需要手动刷新（触发字段赋值）；在beanPostProcessor中处理了
        //configSubscription.refresh(configRepository);
        if (defaultValEcho) {
            configSubscription.defaultValEcho();
        }
        if (!started) {
            doStart();
        }
    }
}
