package pw.cdmi.box.disk.authserver.service;

import java.util.List;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.common.domain.AuthServer;

public interface AuthServerService
{
    
    String AUTH_TYPE_LOCAL = "LocalAuth";
    
    List<AuthServer> getByEnterpriseId(long enterpriseId, Limit limit);
    
    AuthServer getAuthServer(Long id);
}
