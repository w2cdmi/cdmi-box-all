package com.huawei.sharedrive.uam.authapp.service;

import java.util.List;

import com.huawei.sharedrive.uam.authapp.domain.AuthAppExtend;
import com.huawei.sharedrive.uam.exception.AuthFailedException;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.uam.domain.AuthApp;

public interface AuthAppService
{
    AuthApp getByAuthAppID(String authAppId);
    
    AuthApp getDefaultWebApp();
    
    List<AuthApp> getAuthAppList(AuthAppExtend filter, Order order, Limit limit);
    
    void delete(String authAppId);
    
    void create(AuthApp authApp);
    
    void updateAuthApp(AuthApp authApp);
    
    String checkAuthApp(String authorization, String date) throws AuthFailedException;
    
    int getCountByAuthentication(long enterpriseId);
    
    List<AuthApp> getByAuthentication(long enterpriseId, Limit limit);
    
    void updateNetworkRegionStatus(AuthApp authApp);
    
    List<String> getAppId();
}
