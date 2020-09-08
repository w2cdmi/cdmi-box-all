package com.huawei.sharedrive.uam.logininfo.dao;

import java.util.List;

import com.huawei.sharedrive.uam.logininfo.domain.LoginInfo;

public interface LoginInfoDao
{
    
    void create(LoginInfo loginInfo);
    
    List<LoginInfo> getByLoginName(String loginName);
    
    LoginInfo getByNameDomain(String loginName, String domain);
    
    void delByNameEnterId(String loginName, Long enterpriseId);
    
}
