package pw.cdmi.box.disk.enterprise.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pw.cdmi.box.disk.enterprise.dao.EnterpriseAccountDao;
import pw.cdmi.box.disk.enterprise.service.EnterpriseAccountService;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;

@Service
public class EnterpriseAccountServiceImpl implements EnterpriseAccountService
{
    
    @Autowired
    private EnterpriseAccountDao enterpriseAccountDao;
    
    @Override
    public EnterpriseAccount getByEnterpriseApp(long enterpriseId, String authAppId)
    {
        return enterpriseAccountDao.getByEnterpriseApp(enterpriseId, authAppId);
    }

	@Override
	public EnterpriseAccount getByAccountId(long accountId) {
		// TODO Auto-generated method stub
		return enterpriseAccountDao.getByAccountId(accountId);
	}
    
}
