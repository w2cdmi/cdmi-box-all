package com.huawei.sharedrive.uam.weixin.service.impl;
/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */

import com.huawei.sharedrive.uam.weixin.rest.proxy.WxWorkOauth2SuiteProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.weixin.rest.*;
import com.huawei.sharedrive.uam.weixin.service.WxProviderService;

import pw.cdmi.common.cache.CacheClient;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>套件产品中微信OAuth2相关接口</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/27
 ************************************************************/
@Service
public class WxProviderServiceImpl implements WxProviderService {
    private static final Logger logger = LoggerFactory.getLogger(WxProviderServiceImpl.class);

    @Autowired
    private CacheClient cacheClient;

    @Autowired
    private WxWorkOauth2SuiteProxy wxOauth2SuiteProxy;

    /**
     * 获取服务商的凭证。以corpid、provider_secret换取provider_access_token
     * @return 服务商凭证
     */
    public String getProviderAccessToken() {
        String key = "Provider_accessToken";
        String token = (String)cacheClient.getCache(key);

        if(token == null) {
            ProviderAccessTokenInfo tokenInfo = wxOauth2SuiteProxy.getProviderAccessToken();
            if(tokenInfo != null) {
                if(tokenInfo.getErrcode() == null || tokenInfo.getErrcode() == 0) {
                    token = tokenInfo.getProviderAccessToken();

                    cacheClient.setCache(key, token, tokenInfo.getExpiresIn() * 1000);
                } else {
                    logger.error("Failed to query suite token: errcode={}, errmsg={}", tokenInfo.getErrcode(), tokenInfo.getErrmsg());
                }
            }
        }
        return token;
    }

    /**
     * 用于根据注册模板生成注册码
     * @return 服务商凭证
     */
    public String getRegisterCode(String templateId) {
        String token = getProviderAccessToken();

        RegisterCodeInfo info = wxOauth2SuiteProxy.getRegisterCodeInfo(token, templateId);
        if(info.getErrcode() == null || info.getErrcode() == 0) {
            return info.getRegisterCode();
        } else {
            logger.error("Failed to query register code: errcode={}, errmsg={}", info.getErrcode(), info.getErrmsg());
        }

        return null;
    }


    /**
     * 该API用于获取预授权码。预授权码用于企业授权时的第三方服务商安全验证。
     * @return 预授权码
     */
    public String getPreAuthCode() {
        String key = "pre_auth_code";

        String code = (String)cacheClient.getCache(key);
        if(code == null) {
            String suiteToken = wxOauth2SuiteProxy.getSuiteToken();
            PreAuthCodeInfo codeInfo = wxOauth2SuiteProxy.getPreAuthCode(suiteToken);
            if(codeInfo.getErrcode() == null || codeInfo.getErrcode() == 0) {
                code = codeInfo.getPreAuthCode();

                cacheClient.setCache(key, code, codeInfo.getExpiresIn() * 1000);
            } else {
                logger.error("Failed to query pre-auth-code: errcode={}, errmsg={}", codeInfo.getErrcode(), codeInfo.getErrmsg());
            }
        }

        return code;
    }
}
