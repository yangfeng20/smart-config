package com.maple.smart.config.core.listener;

import com.maple.smart.config.core.infrastructure.json.JSONFacade;
import com.maple.smart.config.core.model.ConfigEntity;
import com.maple.smart.config.core.subscription.ConfigSubscription;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

/**
 * @author maple
 * @since 2024/3/17 10:40
 * Description:
 */

@Slf4j
public class DefaultConfigListener implements ConfigListener {

    protected ConfigSubscription configSubscription;

    @Override
    public void onChange(Collection<ConfigEntity> changeConfigEntityList) {
        log.info("Repository release; changeList:{}", JSONFacade.toJsonStr(changeConfigEntityList));
    }

    @Override
    public void setConfigSubscription(ConfigSubscription configSubscription) {
        this.configSubscription = configSubscription;
    }
}
