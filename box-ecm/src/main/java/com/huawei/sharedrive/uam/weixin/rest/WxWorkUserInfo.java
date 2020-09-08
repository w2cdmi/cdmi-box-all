/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package com.huawei.sharedrive.uam.weixin.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>企业微信用户类</pre>
 * @Project Alpha CDMI Service Platform, box-weixin Component. 2017/8/15
 ************************************************************/
public class WxWorkUserInfo extends WxApiResponse implements Serializable {
    @JsonProperty("UserId")
    String userId;

    @JsonProperty("DeviceId")
    String deviceId;

    @JsonProperty("user_ticket")
    String userTicket;

    @JsonProperty("expires_in")
    Integer expiresIn;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getUserTicket() {
        return userTicket;
    }

    public void setUserTicket(String userTicket) {
        this.userTicket = userTicket;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }
}
