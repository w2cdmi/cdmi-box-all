package com.huawei.sharedrive.uam.weixin.service.impl;
/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */

import com.huawei.sharedrive.uam.weixin.rest.proxy.WxWorkOauth2AppProxy;
import com.huawei.sharedrive.uam.weixin.service.WxWorkOauth2Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.weixin.rest.*;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>企业应用中微信OAuth2相关接口</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/27
 ************************************************************/
@Service
public class WxWorkOauth2AppServiceImpl implements WxWorkOauth2Service {
    private static final Logger logger = LoggerFactory.getLogger(WxWorkOauth2AppServiceImpl.class);

    @Autowired
    private WxWorkOauth2AppProxy wxOauth2AppProxy;

    /**
     * 获取企业的访问令牌。
     * @param corpId 企业corpid
     */
    public String getCorpToken(String corpId) {
        return wxOauth2AppProxy.getCorpToken(corpId);
    }

    @Override
    public CorpAuthInfo getCorpAuthInfo(String corpId, String code) {
        return null;
    }

    /**
     * 根据code获取成员信息
     * @param corpId 授权方corpid
     * @param code 通过成员授权获取到的code，最大为512字节。每次成员授权带上的code将不一样，code只能使用一次，5分钟未被使用自动过期。
     */
    public WxWorkUserInfo getUserInfoByCode(String corpId, String code)  {
        return wxOauth2AppProxy.getUserInfoByCode(corpId, code);
    }

    /**
     * 获取企业JS-SDK的访问票据。如果缓存未过期，直接使用；否则通过微信后台接口重新获取，然后缓存，直到过期。
     * @param corpId 授权方corpid
     * @return 授权方（企业）JS-SDK ticket
     */
    public String getCorpJsApiTicket(String corpId)  {
        return wxOauth2AppProxy.getCorpJsApiTicket(corpId);
    }

    @Override
    public LoginInfo getLoginInfo(String authCode) {
        throw new RuntimeException("Not Supported In App Mode!");
    }
}
