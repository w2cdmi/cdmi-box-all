package com.huawei.sharedrive.uam.weixin.rest.proxy;
/*
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-${year} 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-${year} www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */

import com.huawei.sharedrive.uam.weixin.rest.CorpAccessTokenInfo;
import com.huawei.sharedrive.uam.weixin.rest.JsApiTicketInfo;
import com.huawei.sharedrive.uam.weixin.rest.WxWorkUserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.core.web.JsonMapper;
import com.huawei.sharedrive.uam.util.HttpClientUtils;

import pw.cdmi.common.cache.CacheClient;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description:
 * <pre>企业微信中，作为企业应用提供者（应用只为本公司服务），与企业微信后台相关的功能接口</pre>
 * @Project Alpha CDMI Service Platform, ${project_name} Component. ${date}
 ************************************************************/
@Component
public class WxWorkOauth2AppProxy {
    private static final Logger logger = LoggerFactory.getLogger(WxWorkOauth2AppProxy.class);
    private String corpId = "wwc7342fa63c523b9a";
    private String corpSecret = "eg3UNml2ULRuKnA06HziGhvbvdj3lRf8FfXodCvL3zA";

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
        String key = "CorpApp_accessToken";

        String token = (String)cacheClient.getCache(key);
        if(token == null) {
            CorpAccessTokenInfo tokenInfo = _getCorpToken();
            if(tokenInfo.getErrcode() == null || tokenInfo.getErrcode() == 0) {
                token = tokenInfo.getAccessToken();
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
        return JsonMapper.nonEmptyCamelMapper().fromJson(json, CorpAccessTokenInfo.class);
    }

    /**
     * 根据code获取成员信息
     * @param code 通过成员授权获取到的code，最大为512字节。每次成员授权带上的code将不一样，code只能使用一次，5分钟未被使用自动过期。
     */
    public WxWorkUserInfo getUserInfoByCode(String corpId, String code)  {
        String token = getCorpToken(corpId);
        String json = HttpClientUtils.httpGet("https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo?access_token=" + token + "&code=" + code);
        return JsonMapper.nonEmptyCamelMapper().fromJson(json, WxWorkUserInfo.class);
    }

    /**
     * 获取企业JS-SDK的访问票据。如果缓存未过期，直接使用；否则通过微信后台接口重新获取，然后缓存，直到过期。
     * @param corpId 授权方corpid
     * @return 授权方（企业）JS-SDK ticket
     */
    public String getCorpJsApiTicket(String corpId)  {
        String CORP_JSSDK_TICKET_KEY = "CorpApp_jsSdkTicket";
        String key = CORP_JSSDK_TICKET_KEY + "." + corpId;

        String ticket = (String)cacheClient.getCache(key);
        if(ticket == null) {
            String token = getCorpToken(corpId);
            JsApiTicketInfo ticketInfo = getJsApiTicket(token);
            if(ticketInfo.getErrcode() == null || ticketInfo.getErrcode() == 0) {
                ticket = ticketInfo.getTicket();
                cacheClient.setCache(key, ticket, ticketInfo.getExpiresIn() * 1000);
            } else {
                logger.error("Failed to query corp token: errcode={}, errmsg={}", ticketInfo.getErrcode(), ticketInfo.getErrmsg());
            }
        }

        return ticket;
    }

    /**
     * 通过access_token来获取jsapi_ticket，jsapi_ticket是H5应用调用企业微信JS接口的临时票据。
     * @param token 企业调用接口凭证
     */
    protected JsApiTicketInfo getJsApiTicket(String token) {
        String json = HttpClientUtils.httpGet("https://qyapi.weixin.qq.com/cgi-bin/get_jsapi_ticket?access_token=" + token);
        return JsonMapper.nonEmptyCamelMapper().fromJson(json, JsApiTicketInfo.class);
    }
}