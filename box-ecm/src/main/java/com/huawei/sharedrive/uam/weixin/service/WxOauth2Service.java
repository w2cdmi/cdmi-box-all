package com.huawei.sharedrive.uam.weixin.service;
/*
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-${year} 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-${year} www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */


import com.huawei.sharedrive.uam.weixin.rest.WxMpUserInfo;
import com.huawei.sharedrive.uam.weixin.rest.WxUserInfo;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description:
 * <pre>微信开放平台相关接口</pre>
 * @Project Alpha CDMI Service Platform, ${project_name} Component. ${date}
 ************************************************************/
public interface WxOauth2Service {
    /**
     * 获取微信用户信息
     */
    public WxUserInfo getWxUserInfo(String code);

    /**
     * 获取微信小程序中的用户信息
     */
    public WxMpUserInfo getWxMpUserInfo(String mpId, String code, String iv, String encryptedData);

    /**
     * 获取用户手机号信息
     */
    public String getWxMpUserPhone(String mpId, String code, String iv, String encryptedData);
}
