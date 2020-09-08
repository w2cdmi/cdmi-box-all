package com.huawei.sharedrive.uam.logininfo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.logininfo.dao.LoginInfoDao;
import com.huawei.sharedrive.uam.logininfo.domain.LoginInfo;
import com.huawei.sharedrive.uam.logininfo.service.LoginInfoService;

@Component
public class LoginInfoServiceImpl implements LoginInfoService
{
    
    @Autowired
    private LoginInfoDao loginInfoDao;
    
    @Override
    public void create(LoginInfo loginInfo)
    {
        loginInfoDao.create(loginInfo);
        
    }
    
    @Override
    public List<LoginInfo> getByLoginName(String loginName)
    {
        return loginInfoDao.getByLoginName(loginName);
    }
    
    @Override
    public void delByNameEnterId(String loginName, Long enterpriseId)
    {
        loginInfoDao.delByNameEnterId(loginName, enterpriseId);
    }
    
    @Override
    public LoginInfo getByNameDomain(String loginName, String domain)
    {
        return loginInfoDao.getByNameDomain(loginName, domain);
    }
    
}
