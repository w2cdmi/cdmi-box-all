package com.huawei.sharedrive.uam.cmb.retrieve.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.accountuser.dao.impl.UserAccountDaoImpl;
import com.huawei.sharedrive.uam.cmb.retrieve.dao.RetrieveUserDao;
import com.huawei.sharedrive.uam.cmb.retrieve.domain.RetrieveUser;
import com.huawei.sharedrive.uam.core.dao.impl.CacheableSqlMapClientDAO;
import com.huawei.sharedrive.uam.core.domain.Limit;
import com.huawei.sharedrive.uam.enterpriseuser.dao.impl.EnterpriseUserDaoImpl;

@Service
@SuppressWarnings({"deprecation", "unchecked"})
public class RetrieveUserDaoImpl extends CacheableSqlMapClientDAO implements RetrieveUserDao
{
    @Override
    public int getRetrieveUserCount(String filter, Integer orgId, Long accountId, Long enterpriseId)
    {
        Map<String, Object> map = new HashMap<String, Object>(5);
        map.put("enterpriseTableSuffix", EnterpriseUserDaoImpl.getTableSuffix(enterpriseId));
        map.put("accountTableSuffix", UserAccountDaoImpl.getTableSuffix(accountId));
        map.put("filter", filter);
        map.put("orgId", orgId);
        map.put("accountId", accountId);
        return (int) sqlMapClientTemplate.queryForObject("RetrieveUser.getRetrieveUserCount",map);
    }
    
    @Override
    public List<RetrieveUser> getRetrieveUserList(String filter, Integer orgId, Long accountId,
        Long enterpriseId, Limit limit)
    {
        Map<String, Object> map = new HashMap<String, Object>(6);
        map.put("enterpriseTableSuffix", EnterpriseUserDaoImpl.getTableSuffix(enterpriseId));
        map.put("accountTableSuffix", UserAccountDaoImpl.getTableSuffix(accountId));
        map.put("filter", filter);
        map.put("orgId", orgId);
        map.put("accountId", accountId);
        map.put("limit", limit);
        return sqlMapClientTemplate.queryForList("RetrieveUser.getRetrieveUserList",map);
    }
}
