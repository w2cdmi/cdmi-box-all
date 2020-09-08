package com.huawei.sharedrive.uam.enterprise.manager;

import java.util.List;

import pw.cdmi.common.domain.enterprise.EnterpriseAccount;

public interface EnterpriseAccountManager
{
    
    List<EnterpriseAccount> getByEnterpriseId(long enterpriseId);
    
    EnterpriseAccount getByEnterpriseApp(long enterpriseId, String authAppId);
    
    EnterpriseAccount getByAccessKeyId(String accessKeyId);
    
    EnterpriseAccount getByAccountId(long accountId);
    
    void bindAppCheck(String authAppId);
    
    void bindAppCheck();
    
}
