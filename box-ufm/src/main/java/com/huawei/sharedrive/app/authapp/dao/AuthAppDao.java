package com.huawei.sharedrive.app.authapp.dao;

import java.util.List;

import com.huawei.sharedrive.app.core.domain.OrderV1;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.uam.domain.AuthApp;

public interface AuthAppDao
{
    
    AuthApp getByAuthAppID(String authAppId);
    
    List<AuthApp> getFilterd(AuthApp filter, OrderV1 order, Limit limit);
    
    int getFilterdCount(AuthApp filter);
    
}
