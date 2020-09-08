package com.huawei.sharedrive.isystem.authapp.dao;

import java.util.List;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.uam.domain.AuthApp;

public interface AuthAppDao
{
    
    AuthApp getByAuthAppID(String authAppId);
    
    List<AuthApp> getFilterd(AuthApp filter, Order order, Limit limit);
    
    int getFilterdCount(AuthApp filter);
    
    void delete(String authAppId);
    
    void create(AuthApp authApp);
    
    void updateAuthApp(AuthApp authApp);
    
    void updateStatus(String authAppId, int status);
    
    void updateAuthAppCreateby(long createby,String authAppId);
    
}
