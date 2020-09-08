package pw.cdmi.box.disk.enterprise.manager;

import java.util.List;

import pw.cdmi.common.domain.enterprise.Enterprise;

public interface EnterpriseManager
{
    List<Enterprise> listEnterprise();
    
    Enterprise getByDomainName(String domainName);
    Enterprise getById(long enterpriseId);
}
