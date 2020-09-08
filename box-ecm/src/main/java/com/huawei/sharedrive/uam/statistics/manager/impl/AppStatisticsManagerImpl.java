package com.huawei.sharedrive.uam.statistics.manager.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.core.RankRequest;
import com.huawei.sharedrive.uam.statistics.manager.AppStatisticsManager;
import com.huawei.sharedrive.uam.user.domain.User;
import com.huawei.sharedrive.uam.user.service.UserService;

import pw.cdmi.box.domain.Pager;
import pw.cdmi.box.http.request.RestRegionInfo;

@Component
public class AppStatisticsManagerImpl implements AppStatisticsManager
{
    
    @Autowired
    private UserService userService;
    
    @Override
    public List<User> listStatisticsByAppId(String appId)
    {
        
        return userService.listStatisticsByAppId(appId);
    }
    
    @Override
    public Pager<User> getRankedUser(Pager<User> userPage, User user, RankRequest request)
    {
        
        return userService.getRankedUser(userPage, user, request);
    }
    
    @Override
    public List<RestRegionInfo> getRegionInfo(String appId)
    {
        
        return userService.getRegionInfo(appId);
    }
    
}
