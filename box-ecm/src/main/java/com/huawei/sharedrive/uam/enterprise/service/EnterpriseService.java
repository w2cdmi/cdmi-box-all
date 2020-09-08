package com.huawei.sharedrive.uam.enterprise.service;

import java.util.List;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.common.domain.enterprise.Enterprise;

public interface EnterpriseService
{
    
    long create(Enterprise enterprise);
    
    boolean isDuplicateValues(Enterprise enterprise);
    
    Page<Enterprise> getFilterd(String filter, String appId, PageRequest pageRequest);
    
    Enterprise getById(long id);
    
    long getByDomainExclusiveId(Enterprise enterprise);
    
    void updateEnterpriseInfo(Enterprise enterprise);
    
    void updateStatus(Enterprise enterprise);
    
    void deleteById(long id);
    
    Enterprise getByDomainName(String domainName);
    
    void updateNetworkAuthStatus(Byte networkAuthStatus, Long id);
    
    List<Enterprise> listForUpdate();

    Enterprise getByName(String name);
}
