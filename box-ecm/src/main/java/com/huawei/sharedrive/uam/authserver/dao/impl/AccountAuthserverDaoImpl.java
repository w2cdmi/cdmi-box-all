package com.huawei.sharedrive.uam.authserver.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.authserver.dao.AccountAuthserverDao;
import com.huawei.sharedrive.uam.authserver.domain.AccountAuthserver;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;

@SuppressWarnings("deprecation")
@Service
public class AccountAuthserverDaoImpl extends CacheableSqlMapClientDAO implements AccountAuthserverDao
{
    
    @Override
    public void create(AccountAuthserver accountAuthserver)
    {
        sqlMapClientTemplate.insert("AccountAuthserver.insert", accountAuthserver);
    }
    
    @Override
    public void update(AccountAuthserver accountAuthserver)
    {
        sqlMapClientTemplate.update("AccountAuthserver.update", accountAuthserver);
    }
    
    @Override
    public int delete(Long accountId, Long authServerId)
    {
        Map<String, Long> map = new HashMap<String, Long>(2);
        map.put("accountId", accountId);
        map.put("authServerId", authServerId);
        return sqlMapClientTemplate.delete("AccountAuthserver.delete", map);
    }
    
    @Override
    public void deleteByAuthServerId(Long authServerId)
    {
        sqlMapClientTemplate.delete("AccountAuthserver.deleteByAuthServerId", authServerId);
    }
    
    @Override
    public AccountAuthserver getByAccountAuthId(Long accountId, Long authServerId)
    {
        Map<String, Long> map = new HashMap<String, Long>(2);
        map.put("accountId", accountId);
        map.put("authServerId", authServerId);
        return (AccountAuthserver) sqlMapClientTemplate.queryForObject("AccountAuthserver.getByAccountAuthId",
            map);
    }
    
    @Override
    public int getAccountId(long authServerId, long enterpriseId, String authAppId)
    {
        
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("authServerId", authServerId);
        map.put("enterpriseId", enterpriseId);
        map.put("authAppId", authAppId);
        Object accountId = sqlMapClientTemplate.queryForObject("AccountAuthserver.getAccountId", map);
        if (accountId == null)
        {
            return AccountAuthserver.UNDEFINED_OPEN_ACCOUNT;
        }
        return (Integer) accountId;
    }
    
}
