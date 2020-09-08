package com.huawei.sharedrive.isystem.dns.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.isystem.dns.dao.DnsServerDao;
import com.huawei.sharedrive.isystem.dns.domain.DnsServer;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Service("dnsServerDao")
@SuppressWarnings("deprecation")
public class DnsServerDaoImpl extends AbstractDAOImpl implements DnsServerDao
{
    
    @Override
    public DnsServer get(Integer id)
    {
        return (DnsServer) sqlMapClientTemplate.queryForObject("DnsServer.select", id);
    }
    
    @Override
    public void create(DnsServer t)
    {
        sqlMapClientTemplate.insert("DnsServer.insert", t);
        
    }
    
    @Override
    public void update(DnsServer t)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void delete(Integer id)
    {
        sqlMapClientTemplate.insert("DnsServer.delete", id);
        
    }
    
    @SuppressWarnings("unchecked")
    public List<DnsServer> getAllDnsServer()
    {
        
        return sqlMapClientTemplate.queryForList("DnsServer.getAll");
    }
    
    public int getNextId()
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("param", "dnsServerId");
        sqlMapClientTemplate.queryForObject("getNextId", map);
        Long id = (Long) map.get("returnid");
        return id.intValue();
    }
    
    @SuppressWarnings("unchecked")
    public List<DnsServer> getDnsServerByIPandPort(DnsServer dnsServer)
    {
        return sqlMapClientTemplate.queryForList("DnsServer.getByIPandPort", dnsServer);
    }
}
