package pw.cdmi.box.uam.authserver.manager;

import pw.cdmi.common.domain.AuthServer;

public interface AuthServerManager
{
    AuthServer getByEnterpriseIdType(long enterpriseId, String type);
    
    void createAuthServer(AuthServer authServer);
    
    void updateLocalAuth(AuthServer authServer);
}
