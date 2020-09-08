package com.huawei.sharedrive.uam.weixin.service;
/*
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-${year} 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-${year} www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */

import com.huawei.sharedrive.uam.weixin.rest.CorpAuthInfo;
import com.huawei.sharedrive.uam.weixin.rest.LoginInfo;
import com.huawei.sharedrive.uam.weixin.rest.WxMpSessionKey;
import com.huawei.sharedrive.uam.weixin.rest.WxWorkUserInfo;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description:
 * <pre>获取企业微信授权访使用的参数</pre>
 * @Project Alpha CDMI Service Platform, ${project_name} Component. ${date}
 ************************************************************/
public interface WxWorkOauth2Service {
    /**
     * 获取企业的访问令牌。如果缓存未过期，直接使用；否则通过微信后台接口重新获取，然后缓存，直到过期。
     * @param corpId 授权方corpid
     * @return 授权方（企业）access_token
     */
    String getCorpToken(String corpId);

    /**
     * 获取企业的授权信息。
     * @param corpId 授权方corpid
     * @return 授权方（企业）授权信息
     */
    CorpAuthInfo getCorpAuthInfo(String corpId, String code);

    /**
     * 根据code获取成员信息
     * @param corpId 授权方corpid
     * @param code 通过成员授权获取到的code，最大为512字节。每次成员授权带上的code将不一样，code只能使用一次，5分钟未被使用自动过期。
     */
    WxWorkUserInfo getUserInfoByCode(String corpId, String code);

    /**
     * 获取企业JS-SDK的访问票据。如果缓存未过期，直接使用；否则通过微信后台接口重新获取，然后缓存，直到过期。
     * @param corpId 授权方corpid
     * @return 授权方（企业）JS-SDK ticket
     */
    String getCorpJsApiTicket(String corpId);

    /**
     * 第三方可通过如下接口，获取企业微信登录用户的信息。
     * @param authCode oauth2.0授权企业微信管理员登录产生的code，最长为512字节。只能使用一次，5分钟未被使用自动过期
     */
    public LoginInfo getLoginInfo(String authCode);
}
