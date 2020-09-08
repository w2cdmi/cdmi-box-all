package com.huawei.sharedrive.uam.authserver.service;

import java.util.List;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.common.domain.AuthServer;

public interface AuthServerService
{
    
    AuthServer enterpriseTypeCheck(long enterpriseId, String type);
    
    AuthServer getByEnterpriseIdType(long enterpriseId, String type);
    
    int getCountByEnterpriseId(long enterpriseId);
    
    List<AuthServer> getByEnterpriseId(long enterpriseId, Limit limit);
    
    int getCountByNoStatus(long enterpriseId);
    
    List<AuthServer> getByNoStatus(long enterpriseId, Limit limit);
    
    void deleteAuthServer(Long id);
    
    void updateAuthServer(AuthServer authServer);
    
    void updateLocalAuth(AuthServer authServer);
    
    AuthServer getDefaultAuthServer();
    
    void createAuthServer(AuthServer authServer);
    
    AuthServer getAuthServer(Long id);
    
    AuthServer getAuthServerNoStatus(Long id);
    
    List<AuthServer> getAllAuthServer();
    
    List<AuthServer> getListByAccountId(long enterpriseId, long accountId);
    
    void updateStatus(AuthServer authServer);
}
