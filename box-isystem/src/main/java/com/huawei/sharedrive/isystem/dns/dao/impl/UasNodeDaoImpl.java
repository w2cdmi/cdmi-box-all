package com.huawei.sharedrive.isystem.dns.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.isystem.dns.dao.UasNodeDao;
import com.huawei.sharedrive.isystem.dns.domain.UasNode;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Service("uasNodeDao")
@SuppressWarnings("deprecation")
public class UasNodeDaoImpl extends AbstractDAOImpl implements UasNodeDao
{
    
    @Override
    public UasNode get(String id)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public void create(UasNode t)
    {
        // TODO Auto-generated method stub
        
    }
    
    
    @Override
    public void update(UasNode t)
    {
        sqlMapClientTemplate.update("UasNode.update", t);
        
    }
    
    @Override
    public void delete(String id)
    {
        // TODO Auto-generated method stub
        
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, UasNode> getUasNodeMap()
    {
        return sqlMapClientTemplate.queryForMap("UasNode.selectAll", null, "serviceAddr");
        
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<UasNode> getUasNodeList()
    {
        return sqlMapClientTemplate.queryForList("UasNode.selectAll");
    }
    
}
