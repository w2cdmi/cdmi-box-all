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
 * @Description: <pre>变更授权通知</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/26
 ************************************************************/
public class SuiteChangeAuthEvent extends WxSuiteEvent {
    private String authCorpId;

    public SuiteChangeAuthEvent() {
    }

    public SuiteChangeAuthEvent(String suiteId, String corpId) {
        this.suiteId = suiteId;
        this.authCorpId = corpId;
    }

    public String getAuthCorpId() {
        return authCorpId;
    }

    public void setAuthCorpId(String authCorpId) {
        this.authCorpId = authCorpId;
    }
}
