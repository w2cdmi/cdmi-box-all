package com.huawei.sharedrive.uam.weixin.rest.proxy;
/*
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-${year} 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-${year} www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */

import com.huawei.sharedrive.uam.weixin.rest.WxUserAccessToken;
import com.huawei.sharedrive.uam.weixin.rest.WxUserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.core.web.JsonMapper;
import com.huawei.sharedrive.uam.util.HttpClientUtils;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description:
 * <pre>微信开放平台中，网站应用，与微信平台相关的功能接口, 主要用于获取用户鉴权信息</pre>
 * @Project Alpha CDMI Service Platform, ${project_name} Component. ${date}
 ************************************************************/
@Component
public class WxWebOauth2Proxy {
    private static final Logger logger = LoggerFactory.getLogger(WxWebOauth2Proxy.class);

    //网站应用AppID
    private String appId = "wxf54677c64020f6f1";

    //网站应用AppSecret
    private String appSecret = "9e20f1d7ed1c7d00d390cdf41ebb1f02";

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    /**
     * 使用登录凭证 code 获取登录微信用户的session_key 和 openid。
     * @param code 登录凭证码
     * @return session信息
     */
    public WxUserAccessToken getWxUserAccessCode(String code) {
        String json = HttpClientUtils.httpPostWithJsonBody("https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + appId + "&secret=" + appSecret + "&code=" + code + "&grant_type=authorization_code", null);

        return JsonMapper.nonEmptyCamelMapper().fromJson(json, WxUserAccessToken.class);
    }

    /**
     * 刷新登录凭证。
     * @param refreshCode 刷新凭证码
     * @return session信息
     */
    public WxUserAccessToken refreshWxUserAccessCode(String refreshCode) {
        String json = HttpClientUtils.httpPostWithJsonBody("https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=" + appId + "&grant_type=refresh_token&refresh_token=" + refreshCode, null);

        return JsonMapper.nonEmptyCamelMapper().fromJson(json, WxUserAccessToken.class);
    }

    /**
     * 使用access code和openid获取用户信息。
     * @param openId 用户Id
     * @return session信息
     */
    public WxUserInfo getWxUserInfo(String accessCode, String openId) {
        String json = HttpClientUtils.httpPostWithJsonBody("https://api.weixin.qq.com/sns/userinfo?access_token=" + accessCode + "&openid=" + openId, null);

        return JsonMapper.nonEmptyCamelMapper().fromJson(json, WxUserInfo.class);
    }
}