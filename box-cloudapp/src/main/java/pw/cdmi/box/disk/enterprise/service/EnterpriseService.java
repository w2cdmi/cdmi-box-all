package pw.cdmi.box.disk.enterprise.service;

import java.util.List;

import pw.cdmi.common.domain.enterprise.Enterprise;

public interface EnterpriseService
{
    List<Enterprise> listEnterprise();
    
    Enterprise getByDomainName(String domainName);
    Enterprise getById(long enterpriseId);
    Enterprise getByName(String name);
}
