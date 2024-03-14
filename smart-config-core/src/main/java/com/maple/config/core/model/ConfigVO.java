package com.maple.config.core.model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author yangfeng
 * @date : 2023/12/6 12:07
 * desc:
 */

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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDurable() {
        return durable;
    }

    public void setDurable(String durable) {
        this.durable = durable;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }
}
