package pw.cdmi.box.disk.enterprise.dao;

import java.util.List;

import pw.cdmi.common.domain.enterprise.Enterprise;

public interface EnterpriseDao
{
    
    List<Enterprise> listEnterprise();
    
    Enterprise getByDomainName(String domainName);

	Enterprise getById(long enterpriseId);

    Enterprise getByName(String name);
}
