package com.huawei.sharedrive.uam.weixin.service.impl;
/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */

import com.huawei.sharedrive.uam.weixin.domain.WxEnterprise;
import com.huawei.sharedrive.uam.weixin.rest.CorpAuthInfo;
import com.huawei.sharedrive.uam.weixin.rest.LoginInfo;
import com.huawei.sharedrive.uam.weixin.rest.WxWorkUserInfo;
import com.huawei.sharedrive.uam.weixin.rest.proxy.WxWorkOauth2SuiteProxy;
import com.huawei.sharedrive.uam.weixin.service.WxEnterpriseService;
import com.huawei.sharedrive.uam.weixin.service.WxWorkOauth2Service;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>套件产品中微信OAuth2相关接口</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/27
 ************************************************************/
@Service
public class WxWorkOauth2SuiteServiceImpl implements WxWorkOauth2Service {
    private static final Logger logger = LoggerFactory.getLogger(WxWorkOauth2SuiteServiceImpl.class);

    @Autowired
    private WxWorkOauth2SuiteProxy wxWorkOauth2SuiteProxy;

    @Autowired
    private WxEnterpriseService wxEnterpriseService;

    /**
     * 获取企业的访问令牌。如果缓存未过期，直接使用；否则通过微信后台接口重新获取，然后缓存，直到过期。
     * @param corpId 授权方corpid
     * @return 授权方（企业）access_token
     */
    public String getCorpToken(String corpId) {
        return wxWorkOauth2SuiteProxy.getCorpToken(corpId);
    }

    public CorpAuthInfo getCorpAuthInfo(String corpId, String code) {
        String suiteToken = wxWorkOauth2SuiteProxy.getSuiteToken();
        if(StringUtils.isBlank(suiteToken)) {
            logger.error("Failed to get CorpAuthInfo: corpId={}, error=Can't get suite access token.");
            return null;
        }

        return  wxWorkOauth2SuiteProxy.getAuthInfo(suiteToken, corpId, code);
    }

    /**
     * 根据code获取成员信息
     * @param corpId 授权方corpid
     * @param code 通过成员授权获取到的code，最大为512字节。每次成员授权带上的code将不一样，code只能使用一次，5分钟未被使用自动过期。
     */
    public WxWorkUserInfo getUserInfoByCode(String corpId, String code)  {
        return wxWorkOauth2SuiteProxy.getUserInfoByCode(corpId, code);
    }

    /**
     * 获取企业JS-SDK的访问票据。如果缓存未过期，直接使用；否则通过微信后台接口重新获取，然后缓存，直到过期。
     * @param corpId 授权方corpid
     * @return 授权方（企业）JS-SDK ticket
     */
    public String getCorpJsApiTicket(String corpId)  {
        return wxWorkOauth2SuiteProxy.getCorpJsApiTicket(corpId);
    }

    /**
     * 第三方可通过如下接口，获取登录用户的信息。
     * @param authCode oauth2.0授权企业微信管理员登录产生的code，最长为512字节。只能使用一次，5分钟未被使用自动过期
     */
    @Override
    public LoginInfo getLoginInfo(String authCode) {
        return wxWorkOauth2SuiteProxy.getLoginInfo(authCode);
    }
}
