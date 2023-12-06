package com.maple.config.core.model;


public enum ReleaseStatusEnum {
    /**
     *
     */
    NULL(0, ""),
    NOT_RELEASE(1, "未发布"),

    RELEASE(2, "已发布"),
    ;

    ReleaseStatusEnum() {
    }

    ReleaseStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private Integer code;

    private String desc;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static ReleaseStatusEnum getByDesc(String desc) {
        for (ReleaseStatusEnum fileType : ReleaseStatusEnum.values()) {
            if (fileType.desc != null && fileType.desc.equalsIgnoreCase(desc)) {
                return fileType;
            }
        }

        return ReleaseStatusEnum.NULL;
    }

    public static ReleaseStatusEnum getByCode(Integer code) {
        for (ReleaseStatusEnum fileType : ReleaseStatusEnum.values()) {
            if (fileType.code != null && fileType.code.equals(code)) {
                return fileType;
            }
        }

        return ReleaseStatusEnum.NULL;
    }
}

