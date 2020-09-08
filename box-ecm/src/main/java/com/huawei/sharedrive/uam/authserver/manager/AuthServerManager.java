package com.huawei.sharedrive.uam.authserver.manager;

import java.util.List;

import com.huawei.sharedrive.uam.ldapauth.manager.AuthServiceManager;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.common.domain.AuthServer;

public interface AuthServerManager
{
    
    AuthServer enterpriseTypeCheck(long enterpriseId, String type);
    
    void createAuthServer(AuthServer authServer);
    
    Page<AuthServer> getByEnterpriseId(long enterpriseId, PageRequest pageRequest);
    
    Page<AuthServer> getByNoStatus(long enterpriseId, PageRequest pageRequest);
    
    void deleteAuthServer(Long id);
    
    List<AuthServer> getByEnterpriseId(long enterpriseId);
    
    List<AuthServer> getByNoStatus(long enterpriseId);
    
    AuthServer getAuthServer(Long id);
    
    AuthServer getAuthServerNoStatus(Long id);
    
    AuthServiceManager getAuthService(long authServerId);
    
    List<AuthServer> getListByAccountId(long enterpriseId, long accountId);
    
    void updateStatus(long id, byte status);
    
}
