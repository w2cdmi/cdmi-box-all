/*
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */

package com.huawei.sharedrive.uam.weixin.domain;

import java.io.Serializable;
import java.util.Date;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>企业微信中的企业信息</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/26
 ************************************************************/
public class WxEnterprise implements Serializable {
    public static final byte STATE_INITIAL = 0;
    public static final byte STATE_NORMAL = 1;
    public static final byte STATE_CLOSED = -1;

    String id;
    String name;
    String type;
    String squareLogoUrl;
    Integer userMax;
    String fullName;
    Integer subjectType;
    Date verifiedEndTime;
    String wxqrCode;
    String email;
    String mobile;
    String userId;
    String permanentCode;
    Long boxEnterpriseId;
    Byte state;
    Date createdAt;
    Date modifiedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSquareLogoUrl() {
        return squareLogoUrl;
    }

    public void setSquareLogoUrl(String squareLogoUrl) {
        this.squareLogoUrl = squareLogoUrl;
    }

    public Integer getUserMax() {
        return userMax;
    }

    public void setUserMax(Integer userMax) {
        this.userMax = userMax;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Integer getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(Integer subjectType) {
        this.subjectType = subjectType;
    }

    public Date getVerifiedEndTime() {
        return verifiedEndTime;
    }

    public void setVerifiedEndTime(Date verifiedEndTime) {
        this.verifiedEndTime = verifiedEndTime;
    }

    public String getWxqrCode() {
        return wxqrCode;
    }

    public void setWxqrCode(String wxqrCode) {
        this.wxqrCode = wxqrCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPermanentCode() {
        return permanentCode;
    }

    public void setPermanentCode(String permanentCode) {
        this.permanentCode = permanentCode;
    }

    public Long getBoxEnterpriseId() {
        return boxEnterpriseId;
    }

    public void setBoxEnterpriseId(Long boxEnterpriseId) {
        this.boxEnterpriseId = boxEnterpriseId;
    }

    public Byte getState() {
        return state;
    }

    public void setState(Byte state) {
        this.state = state;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }
}
