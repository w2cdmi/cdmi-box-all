package com.huawei.sharedrive.uam.openapi.manager;

import java.io.IOException;

import com.huawei.sharedrive.uam.enterpriseuseraccount.domain.EnterpriseUserAccount;
import com.huawei.sharedrive.uam.openapi.domain.BasicUserUpdateRequest;
import com.huawei.sharedrive.uam.openapi.domain.RestUserCreateRequest;

public interface TokenMeApiManager
{
    EnterpriseUserAccount createLdapUser(String loginName, String appId, long enterpriseId, long accountId)
        throws IOException;
    
    EnterpriseUserAccount updateEnterpriseUser(String authorization, long userId, long enterpriseId,
        long accountId, BasicUserUpdateRequest ruser);
    
    EnterpriseUserAccount getUserInfo(Long userId, long accountId, long enterpriseId);

    EnterpriseUserAccount getUserInfoByImAccount(String imAccount, long accountId, long enterpriseId);

	RestUserCreateRequest getPersonUserInfo(Long cloudUserId, String token);
}
