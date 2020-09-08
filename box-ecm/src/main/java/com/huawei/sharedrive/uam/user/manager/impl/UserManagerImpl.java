package com.huawei.sharedrive.uam.user.manager.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.user.manager.UserManager;
import com.huawei.sharedrive.uam.user.service.UserService;

import pw.cdmi.box.http.request.RestRegionInfo;

@Component
public class UserManagerImpl implements UserManager
{
    
    @Autowired
    private UserService userService;
    
    @Override
    public List<RestRegionInfo> getRegionInfo(String appId)
    {
        
        return userService.getRegionInfo(appId);
    }
    
}
