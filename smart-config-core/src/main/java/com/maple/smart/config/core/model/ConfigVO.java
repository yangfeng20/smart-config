package com.maple.smart.config.core.model;

import lombok.Data;

import java.text.SimpleDateFormat;

/**
 * @author yangfeng
 * @since : 2023/12/6 12:07
 * desc:
 */

@Data
public class ConfigVO {

    private String key;

    private String value;

    private String desc;

    private String status;

    private String durable;

    private String createDate;

    private String updateDate;

    public static ConfigVO build(ConfigEntity configEntity){
        if (configEntity == null){
            return null;
        }
        ConfigVO configVO = new ConfigVO();
        configVO.setKey(configEntity.getKey());
        configVO.setValue(configEntity.getValue());
        configVO.setDesc(configEntity.getDesc());
        configVO.setStatus(ReleaseStatusEnum.getByCode(configEntity.getStatus()).getDesc());
        configVO.setDurable(String.valueOf(configEntity.isDurable()));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (configEntity.getCreateDate()!=null){
            configVO.setCreateDate(sdf.format(configEntity.getCreateDate()));
        }
        if (configEntity.getUpdateDate()!=null){
            configVO.setUpdateDate(sdf.format(configEntity.getUpdateDate()));
        }
        return configVO;
    }
}
