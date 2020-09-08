package com.huawei.sharedrive.uam.logininfo.service;

import java.util.List;

import com.huawei.sharedrive.uam.logininfo.domain.LoginInfo;

public interface LoginInfoService
{
    
    void create(LoginInfo loginInfo);
    
    List<LoginInfo> getByLoginName(String loginName);
    
    void delByNameEnterId(String loginName, Long enterpriseId);
    
    LoginInfo getByNameDomain(String loginName, String domain);
}
