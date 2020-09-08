package pw.cdmi.box.uam.authserver.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pw.cdmi.box.uam.authserver.dao.AuthServerDao;
import pw.cdmi.box.uam.authserver.service.AuthServerService;
import pw.cdmi.common.domain.AuthServer;

@Service
public class AuthServerServiceImpl implements AuthServerService
{
    @Autowired
    private AuthServerDao authServerDao;
    
    @Override
    public AuthServer getByEnterpriseIdType(long enterpriseId, String type)
    {
        return authServerDao.getByEnterpriseIdType(enterpriseId, type);
    }
    
    @Override
    public void createAuthServer(AuthServer authServer)
    {
        authServerDao.create(authServer);
    }
    
    @Override
    public void updateLocalAuth(AuthServer authServer)
    {
        authServerDao.updateLocalAuth(authServer);
    }
    
}
