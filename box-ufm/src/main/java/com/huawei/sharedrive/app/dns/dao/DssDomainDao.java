package com.huawei.sharedrive.app.dns.dao;

import java.util.List;

import com.huawei.sharedrive.app.dns.domain.DssDomain;

public interface DssDomainDao
{
    
    List<DssDomain> getAll();
    
    List<DssDomain> getByDssId(int dssId);
    
}