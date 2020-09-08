/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package com.huawei.sharedrive.uam.weixin.rest;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>微信后台返回的套件访问令牌信息</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/28
 ************************************************************/
public class SuiteAccessTokenInfo extends WxApiResponse {
    private String suiteAccessToken;
    private Integer expiresIn;

    public String getSuiteAccessToken() {
        return suiteAccessToken;
    }

    public void setSuiteAccessToken(String suiteAccessToken) {
        this.suiteAccessToken = suiteAccessToken;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }
}
