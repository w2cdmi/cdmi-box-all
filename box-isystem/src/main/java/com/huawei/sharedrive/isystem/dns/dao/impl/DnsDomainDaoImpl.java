package com.huawei.sharedrive.isystem.dns.dao.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.isystem.dns.dao.DnsDomainDao;
import com.huawei.sharedrive.isystem.dns.domain.DnsDomain;
import com.huawei.sharedrive.isystem.dns.domain.DssDomain;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Service("dnsDomainDao")
@SuppressWarnings("deprecation")
public class DnsDomainDaoImpl extends AbstractDAOImpl implements DnsDomainDao
{
    
    @Override
    public void create(DnsDomain t)
    {
        sqlMapClientTemplate.insert("DnsDomain.insert", t);
    }
    
    @Override
    public void update(DnsDomain t)
    {
        // TODO Auto-generated method stub
        
    }
    
    @SuppressWarnings("unchecked")
    public List<DssDomain> getAllByDnsServerID(int id)
    {
        return sqlMapClientTemplate.queryForList("DssDomain.getAllByDnsServerID", id);
    }
    
    @SuppressWarnings("unchecked")
    public List<DssDomain> getAllByDataCenterID(int id)
    {
        return sqlMapClientTemplate.queryForList("DssDomain.getAllByDataCenterID", id);
    }
    
    public void deleteDnsDomainAndDssDomainByKey(String domainName)
    {
        sqlMapClientTemplate.delete("DnsDomain.delete", domainName);
        sqlMapClientTemplate.delete("DssDomain.delete", domainName);
    }
    
    public void createDssDomain(DssDomain t)
    {
        sqlMapClientTemplate.insert("DssDomain.insert", t);
    }
    
    @Override
    public DnsDomain get(String id)
    {
        return (DnsDomain) sqlMapClientTemplate.queryForObject("DnsDomain.select", id);
    }
    
    public DssDomain getDssDomainbyID(String id)
    {
        return (DssDomain) sqlMapClientTemplate.queryForObject("DssDomain.select", id);
    }
    
    @Override
    public void delete(String id)
    {
        // TODO Auto-generated method stub
        
    }
}
