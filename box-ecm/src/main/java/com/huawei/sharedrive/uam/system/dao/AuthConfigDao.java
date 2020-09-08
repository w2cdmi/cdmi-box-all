package com.huawei.sharedrive.uam.system.dao;

import java.util.List;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.common.domain.AuthServer;

public interface AuthConfigDao
{
    
    AuthServer get(Long id);
    
    List<AuthServer> getFilterd(AuthServer filter, Order order, Limit limit);
    
    int getFilterdCount(AuthServer filter);
    
    void delete(Long id);
    
    void create(AuthServer authServer);
    
    void update(AuthServer authServer);
    
    AuthServer getDefaultAuthServer();
    
    long getNextAvailableId();
    
}
