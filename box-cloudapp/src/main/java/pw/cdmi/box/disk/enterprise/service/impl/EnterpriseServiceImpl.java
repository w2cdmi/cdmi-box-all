package pw.cdmi.box.disk.enterprise.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pw.cdmi.box.disk.enterprise.dao.EnterpriseDao;
import pw.cdmi.box.disk.enterprise.service.EnterpriseService;
import pw.cdmi.common.domain.enterprise.Enterprise;

@Component
public class EnterpriseServiceImpl implements EnterpriseService
{
    
    @Autowired
    private EnterpriseDao enterpriseDao;
    
    @Override
    public Enterprise getByDomainName(String domainName)
    {
        return enterpriseDao.getByDomainName(domainName);
    }
    
    @Override
    public List<Enterprise> listEnterprise()
    {
        List<Enterprise> list = enterpriseDao.listEnterprise();
        return list;
        
    }

	@Override
	public Enterprise getById(long enterpriseId) {
		return enterpriseDao.getById(enterpriseId);
	}

    @Override
    public Enterprise getByName(String name) {
        return enterpriseDao.getByName(name);
    }
}
