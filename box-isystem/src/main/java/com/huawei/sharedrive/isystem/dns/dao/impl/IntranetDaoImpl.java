package com.huawei.sharedrive.isystem.dns.dao.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.isystem.dns.dao.IntranetDao;
import com.huawei.sharedrive.isystem.dns.domain.Intranet;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Service("intranetDao")
@SuppressWarnings("deprecation")
public class IntranetDaoImpl extends AbstractDAOImpl implements IntranetDao
{
    
  
    @Override
    public Intranet get(String id)
    {
        return (Intranet) sqlMapClientTemplate.queryForObject("Intranet.select", id);
    }
    
    @Override
    public void create(Intranet t)
    {
        sqlMapClientTemplate.insert("Intranet.insert", t);
    }
    
    @Override
    public void update(Intranet t)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void delete(String netAddress)
    {
        sqlMapClientTemplate.delete("Intranet.delete", netAddress);
        
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<Intranet> getAllList()
    {
        return sqlMapClientTemplate.queryForList("Intranet.selectAll");
        
    }
    
}
