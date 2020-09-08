package pw.cdmi.box.disk.authserver.manager.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pw.cdmi.box.disk.authserver.manager.AuthServerManager;
import pw.cdmi.box.disk.authserver.service.AuthServerService;
import pw.cdmi.common.domain.AuthServer;

@Component
public class AuthServerManagerImpl implements AuthServerManager
{
    
    @Autowired
    private AuthServerService authServerService;
    
    @Override
    public List<AuthServer> getByEnterpriseId(long enterpriseId)
    {
        return authServerService.getByEnterpriseId(enterpriseId, null);
    }
}
