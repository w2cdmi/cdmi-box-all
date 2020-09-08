
/*
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2018 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2018 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package com.huawei.sharedrive.app.user.service;

import java.util.List;

/************************************************************
 * @Description:
 * <pre>查询用户归属的审批人接口</pre>
 * @author Rox
 * @version 3.0.1
 * @Project Alpha CDMI Service Platform, storbox-ufm Component. 2018/3/1
 ************************************************************/
public interface PrivilegeService {
    //查询用户所属的部门负责人账号信息(cloudUserId)
    List<Long> getDeptManagerOfUser(long enterpriseId, long userId, long accountId);

    //查询用户所属的知识管理员账号信息(cloudUserId)
    List<Long> getArchiveOwnerOfUser(long enterpriseId, long userId, long accountId);
}
