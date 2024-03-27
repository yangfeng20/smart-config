package com.maple.smart.config.core.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author yangfeng
 * @since : 2023/12/4 16:32
 * desc:
 */

@Data
@NoArgsConstructor
public class ConfigEntity {

    private String key;

    private String value;

    private String desc;

    private Integer status;

    private boolean durable;

    private Date createDate;
    private Date updateDate;

    public ConfigEntity(String key) {
        this.key = key;
    }

    public ConfigEntity(String key, String value, Integer releaseStatus) {
        this.key = key;
        this.value = value;
        this.status = releaseStatus;
    }
}
