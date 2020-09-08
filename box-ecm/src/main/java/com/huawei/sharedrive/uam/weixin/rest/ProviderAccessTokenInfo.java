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
 * @Description: <pre>服务商访问凭证</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/9/26
 ************************************************************/
public class ProviderAccessTokenInfo extends WxApiResponse {
    private String providerAccessToken;
    private Integer expiresIn;

    public String getProviderAccessToken() {
        return providerAccessToken;
    }

    public void setProviderAccessToken(String providerAccessToken) {
        this.providerAccessToken = providerAccessToken;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }
}
