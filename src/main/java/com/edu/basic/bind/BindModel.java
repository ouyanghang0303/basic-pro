package com.edu.basic.bind;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class BindModel {

    public static final String STATUS_WAIT_BIND = "0";
    public static final String STATUS_BIND_END = "1";

    private String barCode;
    private String imageId;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;//创建时间
    private String bindStatus; //0未绑定 1 已绑定


    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getBindStatus() {
        return bindStatus;
    }

    public void setBindStatus(String bindStatus) {
        this.bindStatus = bindStatus;
    }
}
