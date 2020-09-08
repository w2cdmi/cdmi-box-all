
/*
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2018 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2018 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package com.huawei.sharedrive.app.user.dao;

import java.util.List;

/************************************************************
 * @Description:
 * <pre>查询审批权限相关的信息</pre>
 * @author Rox
 * @version 3.0.1
 * @Project Alpha CDMI Service Platform, storbox-ufm Component. 2018/3/1
 ************************************************************/
public interface PrivilegeDao {
    List<Long> getDeptManagerOfUser(long enterpriseId, long userId, long accountId);

    List<Long> getArchiveOwnerOfUser(long enterpriseId, long userId, long accountId);
}
