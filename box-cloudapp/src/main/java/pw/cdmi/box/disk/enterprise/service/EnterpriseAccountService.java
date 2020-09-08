package pw.cdmi.box.disk.enterprise.service;

import pw.cdmi.common.domain.enterprise.EnterpriseAccount;

public interface EnterpriseAccountService
{
    
    EnterpriseAccount getByEnterpriseApp(long enterpriseId, String authAppId);
    EnterpriseAccount getByAccountId(long accountId);
    
}
