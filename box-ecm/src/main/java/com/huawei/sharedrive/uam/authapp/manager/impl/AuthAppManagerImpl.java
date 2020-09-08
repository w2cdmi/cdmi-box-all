package com.huawei.sharedrive.uam.authapp.manager.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.authapp.manager.AuthAppManager;
import com.huawei.sharedrive.uam.authapp.service.AuthAppService;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.uam.domain.AuthApp;

@Component
public class AuthAppManagerImpl implements AuthAppManager
{
    
    @Autowired
    private AuthAppService authAppService;
    
    @Override
    public List<String> getAppId()
    {
        return authAppService.getAppId();
    }
    
    @Override
    public Page<AuthApp> getByAuthentication(long enterpriseId, PageRequest pageRequest)
    {
        int total = authAppService.getCountByAuthentication(enterpriseId);
        List<AuthApp> content = authAppService.getByAuthentication(enterpriseId, pageRequest.getLimit());
        Page<AuthApp> page = new PageImpl<AuthApp>(content, pageRequest, total);
        return page;
    }
    
    @Override
    public AuthApp getByAuthAppID(String authAppId)
    {
        return authAppService.getByAuthAppID(authAppId);
    }
    
}
