package com.huawei.sharedrive.uam.enterprisecontrol;

import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.logininfo.domain.LoginInfo;

import pw.cdmi.common.domain.AuthServer;

public interface EnterpriseAuthControlManager
{
    LoginInfo getLoginInfo(String loginName, String domainName, String appId);
    
    AuthServer getNtlmAuthServer(String appId);
    
    EnterpriseUser authenticate(String name, String password, LoginInfo loginInfo, String requestIp);
    
    LoginInfo getUserLoginInfo(String loginName, String domainName, String appId);
}
