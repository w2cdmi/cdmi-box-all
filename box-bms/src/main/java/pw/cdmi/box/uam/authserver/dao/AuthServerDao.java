package pw.cdmi.box.uam.authserver.dao;

import pw.cdmi.common.domain.AuthServer;

public interface AuthServerDao
{
    AuthServer getByEnterpriseIdType(long enterpriseId, String type);
    
    void create(AuthServer authServer);
    
    void updateLocalAuth(AuthServer authServer);
    
    long getNextAvailableId();
}
