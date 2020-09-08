package com.huawei.sharedrive.uam.enterpriseuseraccount.manager;

import com.huawei.sharedrive.uam.enterpriseuseraccount.domain.EnterpriseUserAccount;
import com.huawei.sharedrive.uam.openapi.domain.user.ResponseSearchUser;

public interface EnterpriseUserAccountManager
{
    @SuppressWarnings("PMD.ExcessiveParameterList")
    ResponseSearchUser listUser(int limit, int offset, String filter, long accountid, long enterpriseId,
        String appId);
    
    EnterpriseUserAccount get(Long userId, long accountId, long enterpriseId);

    EnterpriseUserAccount getByImAccount(String imAccount, long accountId, long enterpriseId);
}
