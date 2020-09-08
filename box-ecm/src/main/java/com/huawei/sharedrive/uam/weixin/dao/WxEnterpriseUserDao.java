package com.huawei.sharedrive.uam.weixin.dao;
/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */

import com.huawei.sharedrive.uam.weixin.domain.WxEnterpriseUser;

import java.util.List;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>WxEnterpriseUserDao</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/26
 ************************************************************/
public interface WxEnterpriseUserDao {
    void create(WxEnterpriseUser user);
    void update(WxEnterpriseUser user);
    void delete(WxEnterpriseUser user);

    WxEnterpriseUser get(String corpId, String userId);
    List<WxEnterpriseUser> getByCorpId(String corpId);
    List<WxEnterpriseUser> getByEnterpriseIdAndUserId(long enterpriseId, long enterpriseUserId);

    //批量修改某个企业下的所有用户状态
    void changeStatus(WxEnterpriseUser user);

    //批量删除某个企业下特定状态的用户
    void deleteByStatus(String corpId, int status);
}
