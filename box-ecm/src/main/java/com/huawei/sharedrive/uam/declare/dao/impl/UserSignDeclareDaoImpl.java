package com.huawei.sharedrive.uam.declare.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.core.dao.util.HashTool;
import com.huawei.sharedrive.uam.declare.dao.UserSignDeclareDao;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.common.domain.UserSignDeclare;

@Service
@SuppressWarnings("deprecation")
public class UserSignDeclareDaoImpl extends CacheableSqlMapClientDAO implements UserSignDeclareDao
{
    private static final int TABLE_COUNT = 100;
    
    @Override
    public void create(UserSignDeclare declare)
    {
        declare.setTableSuffix(getTableSuffix(declare.getAccountId()));
        sqlMapClientTemplate.insert("UserSignDeclare.insert", declare);
    }
    
    @Override
    public UserSignDeclare getUserSignDeclare(UserSignDeclare declare)
    {
        declare.setTableSuffix(getTableSuffix(declare.getAccountId()));
        return (UserSignDeclare) sqlMapClientTemplate.queryForObject("UserSignDeclare.get", declare);
    }
    
    @Override
    public void delete(UserSignDeclare declare)
    {
        declare.setTableSuffix(getTableSuffix(declare.getAccountId()));
        Map<String, Object> map = new HashMap<String, Object>(4);
        map.put("tableSuffix", declare.getTableSuffix());
        map.put("accountId", declare.getAccountId());
        map.put("cloudUserId", declare.getCloudUserId());
        map.put("clientType", declare.getClientType());
        sqlMapClientTemplate.delete("UserSignDeclare.delete", map);
    }
    
    public static String getTableSuffix(long accountId)
    {
        String tableSuffix = null;
        int table = (int) (HashTool.apply(String.valueOf(accountId)) % TABLE_COUNT);
        tableSuffix = "_" + table;
        return tableSuffix;
    }
    
    @Override
    public UserSignDeclare getUserSignByClientType(UserSignDeclare declare)
    {
        declare.setTableSuffix(getTableSuffix(declare.getAccountId()));
        Map<String, Object> map = new HashMap<String, Object>(4);
        map.put("accountId", declare.getAccountId());
        map.put("cloudUserId", declare.getCloudUserId());
        map.put("clientType", declare.getClientType());
        map.put("tableSuffix", declare.getTableSuffix());
        return (UserSignDeclare) sqlMapClientTemplate.queryForObject("UserSignDeclare.getUserSign", map);
    }
    
}
