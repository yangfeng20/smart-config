package com.maple.config.core.model;

import java.util.Date;

/**
 * @author yangfeng
 * @date : 2023/12/4 16:32
 * desc:
 */

public class ConfigEntity {

    private String key;

    private String value;

    private String desc;

    private Integer status;

    private boolean durable;

    private Date createDate;
    private Date updateDate;

    public ConfigEntity(String key, String value, Integer releaseStatus) {
        this.key = key;
        this.value = value;
        this.status = releaseStatus;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Boolean getDurable() {
        return durable;
    }

    public void setDurable(boolean durable) {
        this.durable = durable;
    }
}
