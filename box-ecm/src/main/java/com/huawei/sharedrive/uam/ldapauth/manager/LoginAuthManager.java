package com.huawei.sharedrive.uam.ldapauth.manager;

import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.logininfo.domain.LoginInfo;

public interface LoginAuthManager
{
    EnterpriseUser authenticate(String name, String password, LoginInfo loginInfo, long enterpriseId);
    
    EnterpriseUser authenticateByNetwork(String name, String password, LoginInfo loginInfo,
        long enterpriseId, String realIp);
}
