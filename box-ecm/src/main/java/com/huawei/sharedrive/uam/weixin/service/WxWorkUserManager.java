/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package com.huawei.sharedrive.uam.weixin.service;

import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.weixin.domain.WxEnterpriseUser;
import com.huawei.sharedrive.uam.weixin.domain.WxUser;
import com.huawei.sharedrive.uam.weixin.domain.WxUserEnterprise;
import com.huawei.sharedrive.uam.weixin.domain.WxWorkCorpApp;
import com.huawei.sharedrive.uam.weixin.event.SuiteCreateUserEvent;
import com.huawei.sharedrive.uam.weixin.rest.User;

import java.util.List;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>管理企业微信账户与系统内账户</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/31
 ************************************************************/
public interface WxWorkUserManager {
    /**
     * 为企业微信用户开通账号
     */
    EnterpriseUser openAccount(long enterpriseId, User wxUser);

    EnterpriseUser openAccount(long enterpriseId, SuiteCreateUserEvent event);

    /**
     * 更新企业微信用户的账号信息，包括加入的团队空间信息等
     * @param wxUser 企业微信用户
     */
    void updateAccount(long enterpriseId, User wxUser);

    /**
     * 删除企业微信用户的账号信息，包括加入的团队空间信息等
     * @param wxUser 企业微信用户
     */
    void closeAccount(EnterpriseUser sysUser);
}
