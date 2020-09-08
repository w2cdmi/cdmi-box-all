
/*
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2018 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2018 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package com.huawei.sharedrive.uam.weixin.service;

import com.huawei.sharedrive.uam.organization.domain.Department;
import com.huawei.sharedrive.uam.weixin.domain.WxDepartment;
import com.huawei.sharedrive.uam.weixin.event.SuiteCreatePartyEvent;

/************************************************************
 * @Description:
 * <pre>管理企业微信部门与系统内账户</pre>
 * @author Rox
 * @version 3.0.1
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2018/1/27
 ************************************************************/
public interface WxWorkDepartmentManager {
    /**
     * 为企业微信部门开通账号
     */
    Department openAccount(Department dept);

    /**
     * 更新企业微信部门信息
     */
    void updateAccount(Department dept);

    /**
     * 删除企业微信部门的账号
     */
    void closeAccount(Department wxDept);
}
