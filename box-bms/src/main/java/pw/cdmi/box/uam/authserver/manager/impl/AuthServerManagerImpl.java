package pw.cdmi.box.uam.authserver.manager.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pw.cdmi.box.uam.authserver.manager.AuthServerManager;
import pw.cdmi.box.uam.authserver.service.AuthServerService;
import pw.cdmi.common.domain.AuthServer;

@Component
public class AuthServerManagerImpl implements AuthServerManager
{
    @Autowired
    private AuthServerService authServerService;
    
    @Override
    public AuthServer getByEnterpriseIdType(long enterpriseId, String type)
    {
        return authServerService.getByEnterpriseIdType(enterpriseId, type);
    }
    
    @Override
    public void createAuthServer(AuthServer authServer)
    {
        authServerService.createAuthServer(authServer);
    }
    
    @Override
    public void updateLocalAuth(AuthServer authServer)
    {
        authServerService.updateLocalAuth(authServer);
    }
    
}
