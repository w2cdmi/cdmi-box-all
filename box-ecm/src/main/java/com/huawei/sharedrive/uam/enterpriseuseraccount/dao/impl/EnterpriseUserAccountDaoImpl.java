package com.huawei.sharedrive.uam.enterpriseuseraccount.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.core.dao.util.HashTool;
import com.huawei.sharedrive.uam.enterpriseuseraccount.dao.EnterpriseUserAccountDao;
import com.huawei.sharedrive.uam.enterpriseuseraccount.domain.EnterpriseUserAccount;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.box.domain.Limit;

@Service
public class EnterpriseUserAccountDaoImpl extends CacheableSqlMapClientDAO implements
    EnterpriseUserAccountDao
{
    
    private static final int TABLE_COUNT = 100;
    
    @SuppressWarnings({"unchecked", "deprecation"})
    @Override
    public List<EnterpriseUserAccount> getUser(long accountid, long enterpriseId, String filter, Limit limit)
    {
        Map<String, Object> map = new HashMap<String, Object>(4);
        String enterpriseTableSuffix = getTableSuffix(enterpriseId);
        String accountTableSuffix = getTableSuffix(accountid);
        map.put("enterprise_tableSuffix", enterpriseTableSuffix);
        map.put("account_tableSuffix", accountTableSuffix);
        map.put("limit", limit);
        map.put("filter", filter);
        map.put("accountid", accountid);
        return sqlMapClientTemplate.queryForList("EnterpriseUserAccount.getUserFilterd", map);
    }
    
    @SuppressWarnings({"deprecation"})
    @Override
    public int getUserCount(long accountid, long enterpriseId, String filter, Limit limit)
    {
        Map<String, Object> map = new HashMap<String, Object>(4);
        String enterpriseTableSuffix = getTableSuffix(enterpriseId);
        String accountTableSuffix = getTableSuffix(accountid);
        map.put("enterprise_tableSuffix", enterpriseTableSuffix);
        map.put("account_tableSuffix", accountTableSuffix);
        map.put("limit", limit);
        map.put("filter", filter);
        map.put("accountid", accountid);
        return (int) sqlMapClientTemplate.queryForObject("EnterpriseUserAccount.getUserFilterdCount", map);
    }
    
    private String getTableSuffix(long enterpriseId)
    {
        int table = (int) (HashTool.apply(String.valueOf(enterpriseId)) % TABLE_COUNT);
        return "_" + table;
    }
    
}
