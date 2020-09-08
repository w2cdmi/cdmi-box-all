package pw.cdmi.box.uam.authserver.service;

import pw.cdmi.common.domain.AuthServer;

public interface AuthServerService
{
    AuthServer getByEnterpriseIdType(long enterpriseId, String type);
    
    void createAuthServer(AuthServer authServer);
    
    void updateLocalAuth(AuthServer authServer);
}
