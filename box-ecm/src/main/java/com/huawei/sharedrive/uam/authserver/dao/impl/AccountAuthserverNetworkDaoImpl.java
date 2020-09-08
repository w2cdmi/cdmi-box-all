package com.huawei.sharedrive.uam.authserver.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.authserver.dao.AccountAuthserverNetworkDao;
import com.huawei.sharedrive.uam.authserver.domain.AccountAuthserverNetwork;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.box.domain.Limit;

@SuppressWarnings({"unchecked", "deprecation"})
@Service
public class AccountAuthserverNetworkDaoImpl extends CacheableSqlMapClientDAO implements
    AccountAuthserverNetworkDao
{
    @Override
    public long getMaxId()
    {
        Object maxIdObject = sqlMapClientTemplate.queryForObject("AccountAuthserverNetwork.getMaxId");
        long maxId = maxIdObject == null ? 1L : (long) maxIdObject;
        return maxId;
    }
    
    @Override
    public AccountAuthserverNetwork getById(Long id)
    {
        return (AccountAuthserverNetwork) sqlMapClientTemplate.queryForObject("AccountAuthserverNetwork.getById",
            id);
    }
    
    @Override
    public int getNetworkCount(Long authServerId, Long accountId)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("accountId", accountId);
        map.put("authServerId", authServerId);
        return (Integer) sqlMapClientTemplate.queryForObject("AccountAuthserverNetwork.getNetworkCount", map);
    }
    
    @Override
    public List<AccountAuthserverNetwork> getNetworkList(Long authServerId, Long accountId, Limit limit)
    {
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("accountId", accountId);
        map.put("authServerId", authServerId);
        map.put("limit", limit);
        return sqlMapClientTemplate.queryForList("AccountAuthserverNetwork.getNetworkList", map);
    }
    
    @Override
    public List<AccountAuthserverNetwork> getNetworkListByAccount(Long accountId)
    {
        return sqlMapClientTemplate.queryForList("AccountAuthserverNetwork.getNetworkListByAccount",
            accountId);
    }
    
    @Override
    public void create(AccountAuthserverNetwork accountAuthserverNetwork)
    {
        sqlMapClientTemplate.insert("AccountAuthserverNetwork.insert", accountAuthserverNetwork);
    }
    
    @Override
    public void update(AccountAuthserverNetwork accountAuthserverNetwork)
    {
        sqlMapClientTemplate.update("AccountAuthserverNetwork.update", accountAuthserverNetwork);
    }
    
    @Override
    public void deleteByIds(String ids)
    {
        sqlMapClientTemplate.delete("AccountAuthserverNetwork.deleteByIds", ids);
    }
    
    @Override
    public void deleteAll(Long authServerId, Long accountId)
    {
        Map<String, Long> map = new HashMap<String, Long>(2);
        map.put("accountId", accountId);
        map.put("authServerId", authServerId);
        sqlMapClientTemplate.delete("AccountAuthserverNetwork.deleteAll", map);
    }
}
