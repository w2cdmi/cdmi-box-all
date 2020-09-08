/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package com.huawei.sharedrive.uam.weixin.service;

import com.huawei.sharedrive.uam.weixin.domain.WxUser;
import com.huawei.sharedrive.uam.weixin.domain.WxUserEnterprise;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>管理微信账户与系统内账户</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/31
 ************************************************************/
public interface WxUserManager {
    /**
     * 为微信个人帐号
     *
     */
    void openAccount(WxUser wxUser);

    /**
     * 为现有的企业用户绑定微信账号
     * @param wxUser 微信用户信息
     * @param enterpriseId 企业ID
     * @param userId 用户ID
     */
    void bindWxAccount(WxUser wxUser, long enterpriseId, long userId);

    void deleteUser(String unionId);
}
