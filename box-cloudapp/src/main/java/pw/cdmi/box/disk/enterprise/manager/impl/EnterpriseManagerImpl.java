package pw.cdmi.box.disk.enterprise.manager.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pw.cdmi.box.disk.enterprise.manager.EnterpriseManager;
import pw.cdmi.box.disk.enterprise.service.EnterpriseService;
import pw.cdmi.common.domain.enterprise.Enterprise;

@Component
public class EnterpriseManagerImpl implements EnterpriseManager
{
    
    @Autowired
    private EnterpriseService enterpriseService;
    
    @Override
    public List<Enterprise> listEnterprise()
    {
        return enterpriseService.listEnterprise();
    }
    
    @Override
    public Enterprise getByDomainName(String domainName)
    {
        return enterpriseService.getByDomainName(domainName);
    }

	@Override
	public Enterprise getById(long enterpriseId) {
		// TODO Auto-generated method stub
		return enterpriseService.getById(enterpriseId);
	}
}
