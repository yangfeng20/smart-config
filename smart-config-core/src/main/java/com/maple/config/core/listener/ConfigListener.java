package com.maple.config.core.listener;

import com.maple.config.core.model.ConfigEntity;

import java.util.List;

/**
 * @author maple
 * Created Date: 2024/3/7 21:55
 * Description:
 */

public interface ConfigListener {


    void onChange(List<ConfigEntity> chnageConfigEntityList);
}
