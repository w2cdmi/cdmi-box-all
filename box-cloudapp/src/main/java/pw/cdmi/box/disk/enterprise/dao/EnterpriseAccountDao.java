package pw.cdmi.box.disk.enterprise.dao;

import pw.cdmi.common.domain.enterprise.EnterpriseAccount;

public interface EnterpriseAccountDao
{
    EnterpriseAccount getByEnterpriseApp(long enterpriseId, String authAppId);
    
    EnterpriseAccount getByAccountId(long accountId);
}
