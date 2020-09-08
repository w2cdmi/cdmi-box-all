package com.huawei.sharedrive.uam.weixin.event;
/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>微信后台推送事件基类</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/27
 ************************************************************/
public class WxEvent {
    String signature;

    Long nonce;

    Long timeStamp;

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public Long getNonce() {
        return nonce;
    }

    public void setNonce(Long nonce) {
        this.nonce = nonce;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
