package com.huawei.sharedrive.uam.declare.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.declare.dao.ConcealDeclareDao;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.common.domain.ConcealDeclare;

@Service
@SuppressWarnings({"deprecation"})
public class ConcealDeclareDaoImpl extends CacheableSqlMapClientDAO implements ConcealDeclareDao
{
    
    @Override
    public ConcealDeclare getDeclaration(ConcealDeclare declare)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("clientType", declare.getClientType());
        map.put("appId", declare.getAppId());
        return (ConcealDeclare) sqlMapClientTemplate.queryForObject("ConcealDeclare.getDeclaration", map);
    }
    
    @Override
    public void create(ConcealDeclare declare)
    {
        declare.setId(UUID.randomUUID().toString().replace("-", ""));
        declare.setCreateAt(new Date());
        sqlMapClientTemplate.insert("ConcealDeclare.insert", declare);
    }
    
    @Override
    public void update(ConcealDeclare declare)
    {
        sqlMapClientTemplate.update("ConcealDeclare.update", declare);
    }
    
    @Override
    public void delete(ConcealDeclare declare)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("clientType", declare.getClientType());
        map.put("appId", declare.getAppId());
        
        sqlMapClientTemplate.delete("ConcealDeclare.delete", map);
    }
    
    @Override
    public ConcealDeclare getDeclarationById(String declarationId)
    {
        return (ConcealDeclare) sqlMapClientTemplate.queryForObject("ConcealDeclare.getDeclarationById",
            declarationId);
    }
    
}
