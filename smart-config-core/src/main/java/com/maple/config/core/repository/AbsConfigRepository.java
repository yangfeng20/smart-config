package com.maple.config.core.repository;

import com.maple.config.core.model.ConfigEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author maple
 * Created Date: 2024/3/7 21:31
 * Description:
 */

public abstract class AbsConfigRepository implements ConfigRepository {

    protected LocalDateTime lastReleaseTime;

    protected Map<String, ConfigEntity> configEntityMap;

    protected List<String> waitReleaseKeyList;
}
