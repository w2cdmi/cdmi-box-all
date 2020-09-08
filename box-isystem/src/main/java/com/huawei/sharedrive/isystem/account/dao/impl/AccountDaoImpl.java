package com.huawei.sharedrive.isystem.account.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Repository;

import com.huawei.sharedrive.isystem.account.dao.AccountDao;
import com.huawei.sharedrive.isystem.account.domain.Account;
import com.huawei.sharedrive.isystem.account.domain.AccountPageCondition;

@SuppressWarnings("deprecation")
@Repository("accountDao")
public class AccountDaoImpl implements AccountDao
{
    @Autowired
    private SqlMapClientTemplate sqlMapClientTemplate;
    
    @SuppressWarnings("unchecked")
    @Override
    public List<Account> getFilterd(AccountPageCondition filter)
    {
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("filter", filter);
        map.put("order", filter.getPageRequest().getOrder());
        map.put("limit", filter.getPageRequest().getLimit());
        return sqlMapClientTemplate.queryForList("Account.getFilterd", map);
    }
    
    @Override
    public int getFilterdCount(AccountPageCondition filter)
    {
        Map<String, Object> map = new HashMap<String, Object>(1);
        map.put("filter", filter);
        return (Integer) sqlMapClientTemplate.queryForObject("Account.getFilterdCount", map);
    }
}
