package com.huawei.sharedrive.uam.weixin.service;
/*
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-${year} 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-${year} www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description:
 * <pre>获取企业微信授权访使用的参数</pre>
 * @Project Alpha CDMI Service Platform, ${project_name} Component. ${date}
 ************************************************************/
public interface WxProviderService {
    /**
     * 获取服务商的凭证。以corpid、provider_secret换取provider_access_token
     * @return 服务商凭证
     */
    String getProviderAccessToken();

    /**
     * 用于根据注册模板生成注册码
     * @return 服务商凭证
     */
    String getRegisterCode(String templateId);

    /**
     * 该API用于获取预授权码。预授权码用于企业授权时的第三方服务商安全验证。
     * @return 预授权码
     */
    String getPreAuthCode();
}
