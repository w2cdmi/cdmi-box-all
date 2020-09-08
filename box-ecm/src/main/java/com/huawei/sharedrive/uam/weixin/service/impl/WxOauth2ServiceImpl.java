package com.huawei.sharedrive.uam.weixin.service.impl;
/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pw.cdmi.common.cache.CacheClient;

import com.huawei.sharedrive.uam.core.web.JsonMapper;
import com.huawei.sharedrive.uam.weixin.rest.WxMpSessionKey;
import com.huawei.sharedrive.uam.weixin.rest.WxMpUserInfo;
import com.huawei.sharedrive.uam.weixin.rest.WxUserAccessToken;
import com.huawei.sharedrive.uam.weixin.rest.WxUserInfo;
import com.huawei.sharedrive.uam.weixin.rest.proxy.WxMpOauth2Proxy;
import com.huawei.sharedrive.uam.weixin.rest.proxy.WxWebOauth2Proxy;
import com.huawei.sharedrive.uam.weixin.service.WxOauth2Service;
import com.qq.weixin.mp.aes.AesException;
import com.qq.weixin.mp.aes.WXBizDataCrypt;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>套件产品中微信OAuth2相关接口</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/27
 ************************************************************/
@Service
public class WxOauth2ServiceImpl implements WxOauth2Service {
    private static final Logger logger = LoggerFactory.getLogger(WxOauth2ServiceImpl.class);

    @Autowired
    CacheClient cacheClient;

    @Autowired
    private WxMpOauth2Proxy wxMpOauth2Proxy;

    @Autowired
    private WxWebOauth2Proxy wxWebOauth2Proxy;

    /**
     * 使用登录凭证 code 获取登录微信用户的session_key 和 openid。
     * @param code 登录凭证码
     * @return session信息
     */
    private WxMpSessionKey getSessionKeyByCode(String mpId, String code) {
        return wxMpOauth2Proxy.getSessionKeyByCode(mpId, code);
    }

    /**
     * 获取微信用户访问凭证
     */
    public WxUserAccessToken getWxUserAccessToken(String code) {
        String tokenInfoKey = "WxUser.AccessCodeInfo_" + code;
        //1. 先检查缓存
        WxUserAccessToken accessTokeInfo = (WxUserAccessToken)cacheClient.getCache(tokenInfoKey);
        if(accessTokeInfo == null) {
            //2. 可能是缓存已经失效，检查是否有refreshToken
            String refreshKey = "WxUser.RefreshToken_" + code;
            String refreshToken = (String)cacheClient.getCache(refreshKey);

            //没有refreshToken或已失效（30天）
            if(refreshToken == null) {
                accessTokeInfo = wxWebOauth2Proxy.getWxUserAccessCode(code);
            } else {
                accessTokeInfo = wxWebOauth2Proxy.refreshWxUserAccessCode(refreshToken);
            }

            if(!accessTokeInfo.hasError()) {
                cacheClient.setCache(tokenInfoKey, accessTokeInfo, accessTokeInfo.getExpiresIn() * 1000);
                cacheClient.setCache(refreshKey, accessTokeInfo.getRefreshToken(), 2592000000L); //30天
            } else {
                logger.error("Can't get UserAccessToken: code={}: errcode={}, errmsg={}", code, accessTokeInfo.getErrcode(), accessTokeInfo.getErrmsg());
            }
        }

        return accessTokeInfo;
    }

    /**
     * 获取微信用户访问凭证
     */
    @Override
    public WxUserInfo getWxUserInfo(String code) {
        WxUserAccessToken accessCode = getWxUserAccessToken(code);
        if(accessCode == null) {
            logger.error("Can't get WxUserInfo, the access toke info is null: code={}", code);
            return null;
        }

        return wxWebOauth2Proxy.getWxUserInfo(accessCode.getAccessToken(), accessCode.getOpenid());
    }

    /**
     * 获取微信小程序中的用户信息
     */
    public WxMpUserInfo getWxMpUserInfo(String mpId, String code, String iv, String encryptedData) {
        String json = decryptData(mpId, code, iv, encryptedData);
        if(StringUtils.isNotBlank(json)) {
            return JsonMapper.nonEmptyMapper().fromJson(json, WxMpUserInfo.class);
        }

        return null;
    }

    private String decryptData(String mpId, String code, String iv, String encryptedData) {
        WxMpSessionKey sessionInfo = getSessionKeyByCode(mpId, code);
        if(sessionInfo == null) {
            logger.error("Failed to get WxMpUserInfo: session key is null.");
            return null;
        }

        if(sessionInfo.hasError()) {
            logger.error("Failed to get WxMpUserInfo, error occurred while getting session key: code={}, error={}, message={}", code, sessionInfo.getErrcode(), sessionInfo.getErrmsg());
            return null;
        }

        //获取用户信息
        String json;
        try {
            json = new WXBizDataCrypt(sessionInfo.getSessionKey(), iv).DecryptData(encryptedData);
        } catch (AesException e) {
            logger.error("Failed to get WxMpUserInfo, error occurred while decrypting data: code={}, exception={}", code, e.getMessage());
            logger.error("Failed to get WxMpUserInfo", e);
            return null;
        }

        return json;
    }

    /**
     * 获取微信小程序中的手机信息
     */
    public String getWxMpUserPhone(String mpId, String code, String iv, String encryptedData) {
        return decryptData(mpId, code, iv, encryptedData);
    }
}
