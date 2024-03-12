package com.maple.config.core.loader;

import com.maple.config.core.model.ConfigEntity;

import java.util.Collection;

/**
 * @author maple
 * Created Date: 2024/3/7 21:28
 * Description:
 */

public interface ConfigLoader {

    Collection<ConfigEntity> loaderConfig(String path);
}
