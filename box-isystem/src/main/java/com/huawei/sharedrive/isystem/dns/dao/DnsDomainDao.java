package com.huawei.sharedrive.isystem.dns.dao;

import java.util.List;

import com.huawei.sharedrive.isystem.dns.domain.DnsDomain;
import com.huawei.sharedrive.isystem.dns.domain.DssDomain;

import pw.cdmi.box.dao.BaseDAO;

public interface DnsDomainDao extends BaseDAO<DnsDomain, String>
{
    List<DssDomain> getAllByDnsServerID(int id);
    
    void deleteDnsDomainAndDssDomainByKey(String domainName);
    
    void createDssDomain(DssDomain t);
    
    List<DssDomain> getAllByDataCenterID(int id);
    
    DssDomain getDssDomainbyID(String id);
}
