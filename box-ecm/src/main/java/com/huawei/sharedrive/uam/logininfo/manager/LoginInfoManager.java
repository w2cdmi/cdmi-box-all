package com.huawei.sharedrive.uam.logininfo.manager;

import java.util.List;

import com.huawei.sharedrive.uam.logininfo.domain.LoginInfo;

public interface LoginInfoManager
{
    
    void create(LoginInfo loginInfo);
    
    List<LoginInfo> getByLoginName(String loginName);
    
    void updateByNameType(String orgLoginName, LoginInfo logininfo);
    
    LoginInfo getByDomain(String loginName, String domainName);
    
}
