package com.huawei.sharedrive.uam.security.dao.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.security.dao.ProxyNetworkDAO;
import com.huawei.sharedrive.uam.security.domain.ProxyNetwork;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Service
@SuppressWarnings("deprecation")
public class ProxyNetworkDAOImpl extends AbstractDAOImpl implements ProxyNetworkDAO
{
    
    @Override
    public ProxyNetwork get(String id)
    {
        return null;
    }
    
    @Override
    public void create(ProxyNetwork accessNetwork)
    {
        accessNetwork.setId(this.getNextAvailableId());
        sqlMapClientTemplate.insert("ProxyNetwork.insert", accessNetwork);
    }
    
    @Override
    public void update(ProxyNetwork t)
    {
        
    }
    
    @Override
    public void delete(String id)
    {
        
    }
    
    @Override
    public void delete(Integer networkType)
    {
        sqlMapClientTemplate.delete("ProxyNetwork.delete", networkType);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<ProxyNetwork> getTypeList(Integer networkType)
    {
        return (List<ProxyNetwork>) sqlMapClientTemplate.queryForList("ProxyNetwork.getList", networkType);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<ProxyNetwork> getAll()
    {
        return (List<ProxyNetwork>) sqlMapClientTemplate.queryForList("ProxyNetwork.getAll");
    }
    
    @Override
    public long getNextAvailableId()
    {
        Object nextId = sqlMapClientTemplate.queryForObject("ProxyNetwork.getNextId");
        return nextId == null ? 0L : (long) nextId;
    }
}
