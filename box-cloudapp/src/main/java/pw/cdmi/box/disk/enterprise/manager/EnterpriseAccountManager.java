package pw.cdmi.box.disk.enterprise.manager;

import pw.cdmi.common.domain.enterprise.EnterpriseAccount;

public interface EnterpriseAccountManager
{
    EnterpriseAccount getByEnterpriseApp(long enterpriseId, String authAppId);
    EnterpriseAccount getByAccountId(long accountId);
}
