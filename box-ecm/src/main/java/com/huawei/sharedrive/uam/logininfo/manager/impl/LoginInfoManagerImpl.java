package com.huawei.sharedrive.uam.logininfo.manager.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.uam.logininfo.domain.LoginInfo;
import com.huawei.sharedrive.uam.logininfo.manager.LoginInfoManager;
import com.huawei.sharedrive.uam.logininfo.service.LoginInfoService;

@Component
public class LoginInfoManagerImpl implements LoginInfoManager
{
    
    @Autowired
    private LoginInfoService loginInfoService;
    
    @Override
    public void create(LoginInfo loginInfo)
    {
        loginInfoService.create(loginInfo);
        
    }
    
    @Override
    public List<LoginInfo> getByLoginName(String loginName)
    {
        return loginInfoService.getByLoginName(loginName);
    }
    
    @Override
    public LoginInfo getByDomain(String loginName, String domainName)
    {
        return loginInfoService.getByNameDomain(loginName, domainName);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateByNameType(String orgLoginName, LoginInfo logininfo)
    {
        loginInfoService.delByNameEnterId(orgLoginName, logininfo.getEnterpriseId());
        loginInfoService.create(logininfo);
    }
}
