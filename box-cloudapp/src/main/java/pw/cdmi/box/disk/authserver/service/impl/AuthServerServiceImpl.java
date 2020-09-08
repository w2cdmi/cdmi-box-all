package pw.cdmi.box.disk.authserver.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pw.cdmi.box.disk.authserver.dao.AuthServerDao;
import pw.cdmi.box.disk.authserver.service.AuthServerService;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.common.domain.AuthServer;

@Service
public class AuthServerServiceImpl implements AuthServerService
{
    
    @Autowired
    private AuthServerDao authServerDao;
    
    @Override
    public List<AuthServer> getByEnterpriseId(long enterpriseId, Limit limit)
    {
        return authServerDao.getByEnterpriseId(enterpriseId, limit);
    }
    
    @Override
    public AuthServer getAuthServer(Long id)
    {
        return authServerDao.get(id);
    }
}
