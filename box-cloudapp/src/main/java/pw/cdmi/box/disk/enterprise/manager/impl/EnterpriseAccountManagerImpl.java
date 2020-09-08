package pw.cdmi.box.disk.enterprise.manager.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pw.cdmi.box.disk.enterprise.manager.EnterpriseAccountManager;
import pw.cdmi.box.disk.enterprise.service.EnterpriseAccountService;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;

@Component
public class EnterpriseAccountManagerImpl implements EnterpriseAccountManager
{
    
    @Autowired
    private EnterpriseAccountService enterpriseAccountService;
    
    @Override
    public EnterpriseAccount getByEnterpriseApp(long enterpriseId, String authAppId)
    {
        return enterpriseAccountService.getByEnterpriseApp(enterpriseId, authAppId);
    }

	@Override
	public EnterpriseAccount getByAccountId(long accountId) {
		// TODO Auto-generated method stub
		return enterpriseAccountService.getByAccountId(accountId);
	}
    
}
