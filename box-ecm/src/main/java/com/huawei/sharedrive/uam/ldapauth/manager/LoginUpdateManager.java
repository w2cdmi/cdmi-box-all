package com.huawei.sharedrive.uam.ldapauth.manager;

import java.io.IOException;

import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuseraccount.domain.EnterpriseUserAccount;

public interface LoginUpdateManager
{
    EnterpriseUserAccount save(long authServerId, String objecrSid, long enterpriseId, String authAppId,
        EnterpriseUser ldapEnterpriseUser) throws IOException;
}
