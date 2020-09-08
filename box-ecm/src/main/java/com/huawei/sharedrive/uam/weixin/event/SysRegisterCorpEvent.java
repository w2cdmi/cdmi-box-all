/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package com.huawei.sharedrive.uam.weixin.event;

import com.huawei.sharedrive.uam.weixin.rest.AuthUserInfo;
import com.huawei.sharedrive.uam.weixin.rest.ContactSync;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>企业注册事件，用户在服务商网站扫码一键安装企业微信和应用，当用户注册企业微信时，微信后台会发送此事件通知服务商</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/9/26
 ************************************************************/
public class SysRegisterCorpEvent extends WxSysEvent {
    private String registerCode;
    private String authCorpId;
    private ContactSync contactSync;
    private AuthUserInfo authUserInfo;

    public String getRegisterCode() {
        return registerCode;
    }

    public void setRegisterCode(String registerCode) {
        this.registerCode = registerCode;
    }

    public String getAuthCorpId() {
        return authCorpId;
    }

    public void setAuthCorpId(String authCorpId) {
        this.authCorpId = authCorpId;
    }

    public ContactSync getContactSync() {
        return contactSync;
    }

    public void setContactSync(ContactSync contactSync) {
        this.contactSync = contactSync;
    }

    public AuthUserInfo getAuthUserInfo() {
        return authUserInfo;
    }

    public void setAuthUserInfo(AuthUserInfo authUserInfo) {
        this.authUserInfo = authUserInfo;
    }
}
