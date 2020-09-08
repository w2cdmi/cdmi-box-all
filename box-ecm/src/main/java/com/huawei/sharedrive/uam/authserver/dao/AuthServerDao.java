package com.huawei.sharedrive.uam.authserver.dao;

import java.util.List;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.common.domain.AuthServer;

public interface AuthServerDao
{
    int getCountByEnterpriseId(long enterpriseId);
    
    List<AuthServer> getByEnterpriseId(long enterpriseId, Limit limit);
    
    int getCountByNoStatus(long enterpriseId);
    
    List<AuthServer> getByNoStatus(long enterpriseId, Limit limit);
    
    AuthServer get(Long id);
    
    AuthServer getAuthServerNoStatus(Long id);
    
    AuthServer getByEnterpriseIdType(long enterpriseId, String type);
    
    List<AuthServer> getFilterd(AuthServer filter, Order order, Limit limit);
    
    int getFilterdCount(AuthServer filter);
    
    void create(AuthServer authServer);
    
    void updateAuthServerConfig(AuthServer authServer);
    
    void updateLocalAuth(AuthServer authServer);
    
    AuthServer getDefaultAuthServer();
    
    long getNextAvailableId();
    
    void delete(Long id);
    
    List<AuthServer> getListByAccountId(long enterpriseId, long accountId);
    
    void updateStatus(AuthServer authServer);
    
}
