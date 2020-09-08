package com.huawei.sharedrive.uam.weixin.rest.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pw.cdmi.common.cache.CacheClient;

import com.huawei.cs.json.JSONObject;
import com.huawei.sharedrive.uam.core.web.JsonMapper;
import com.huawei.sharedrive.uam.util.HttpClientUtils;
import com.huawei.sharedrive.uam.weixin.rest.CorpAccessTokenInfo;
import com.huawei.sharedrive.uam.weixin.rest.JsApiTicketInfo;
import com.huawei.sharedrive.uam.weixin.rest.WxWorkUserInfo;
import com.huawei.sharedrive.uam.weixin.rest.WxworkWxMpUserInfo;

/************************************************************
 * @author 7367
 * @version 3.0.1
 * @Description:
 * <pre>企业微信小程序登录平台相关的功能接口, 主要用于获取用户鉴权信息</pre>
 ************************************************************/
@Component
public class WxworkWxMpOauth2Proxy {
	
	private static final Logger logger = LoggerFactory.getLogger(WxworkWxMpOauth2Proxy.class);
    private String corpId = "wwff314b9b8085f16c";
    private String corpSecret = "dHmWF-m-c3MsMHZpU1fGsL8WjzoBbRyaDTmMm-kph7Q";

    @Autowired
    private CacheClient cacheClient;

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getCorpSecret() {
        return corpSecret;
    }

    public void setCorpSecret(String corpSecret) {
        this.corpSecret = corpSecret;
    }

    /**
     * 获取企业的访问令牌。如果缓存未过期，直接使用；否则通过微信后台接口重新获取，然后缓存，直到过期。
     * @param corpId 授权方corpid
     */
    public String getCorpToken(String corpId) {
        String key = "wxworkWxmp_accessToken";

        String token = (String)cacheClient.getCache(key);
        logger.error("wxworkWxmp_accessToken cache: {}", token);
        if(token == null) {
            CorpAccessTokenInfo tokenInfo = _getCorpToken();
            if(tokenInfo.getErrcode() == null || tokenInfo.getErrcode() == 0) {
                token = tokenInfo.getAccessToken();
                logger.error("wxworkWxmp_accessToken create: {}", token);
                cacheClient.setCache(key, token, tokenInfo.getExpiresIn() * 1000);
            } else {
                logger.error("Failed to query corp token: errcode={}, errmsg={}", tokenInfo.getErrcode(), tokenInfo.getErrmsg());
            }
        }

        return token;
    }

    /**
     * 请求方式：GET（HTTPS）
     * 请求地址： https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=ID&corpsecret=SECRECT
     */
    protected CorpAccessTokenInfo _getCorpToken() {
        String json = HttpClientUtils.httpGet("https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=" + corpId + "&corpsecret=" + corpSecret);
        logger.error("accessToken info json: {}", json);
        return JsonMapper.nonEmptyCamelMapper().fromJson(json, CorpAccessTokenInfo.class);
    }

    /**
     * 根据code获取成员信息
     * @param code 通过成员授权获取到的code，最大为512字节。每次成员授权带上的code将不一样，code只能使用一次，5分钟未被使用自动过期。
     */
    public WxworkWxMpUserInfo getUserInfoByCode(String corpId, String code)  {
        String token = getCorpToken(corpId);
        String json = HttpClientUtils.httpGet("https://qyapi.weixin.qq.com/cgi-bin/miniprogram/jscode2session?access_token=" + token + "&js_code=" + code + "&grant_type=authorization_code");
        logger.error("get user info json:" + json  + " token" + token + " code: " + code);
        return JsonMapper.nonEmptyCamelMapper().fromJson(json, WxworkWxMpUserInfo.class);
    }
}
