
/*
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2018 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2018 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package com.huawei.sharedrive.app.user.dao.impl;

import com.huawei.sharedrive.app.user.dao.PrivilegeDao;
import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.core.utils.HashTool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/************************************************************
 * @Description:
 * <pre>查询审批权限相关的信息</pre>
 * @author Rox
 * @version 3.0.1
 * @Project Alpha CDMI Service Platform, storbox-ufm Component. 2018/3/1
 ************************************************************/
public class PrivilegeDaoImpl extends CacheableSqlMapClientDAO implements PrivilegeDao {
    private static final int TABLE_USER_ACCOUNT_COUNT = 100;

    @Override
    public List<Long> getDeptManagerOfUser(long enterpriseId, long enterpriseUserId, long accountId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("tableSuffix", getTableSuffix(accountId, TABLE_USER_ACCOUNT_COUNT));
        map.put("enterpriseId", enterpriseId);
        map.put("enterpriseUserId", enterpriseUserId);
        map.put("accountId", accountId);

        return sqlMapClientTemplate.queryForList("Privilege.getDeptManagerOfUser", map);
    }

    @Override
    public List<Long> getArchiveOwnerOfUser(long enterpriseId, long enterpriseUserId, long accountId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("tableSuffix", getTableSuffix(accountId, TABLE_USER_ACCOUNT_COUNT));
        map.put("enterpriseId", enterpriseId);
        map.put("enterpriseUserId", enterpriseUserId);
        map.put("accountId", accountId);

        return sqlMapClientTemplate.queryForList("Privilege.getArchiveOwnerOfUser", map);
    }

    public static String getTableSuffix(long id, int tableCount) {
        String tableSuffix = null;
        int table = (int) (HashTool.apply(String.valueOf(id)) % tableCount);
        tableSuffix = "_" + table;
        return tableSuffix;
    }
}
