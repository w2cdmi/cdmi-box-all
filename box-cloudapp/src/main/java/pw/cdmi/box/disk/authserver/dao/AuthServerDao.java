package pw.cdmi.box.disk.authserver.dao;

import java.util.List;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.common.domain.AuthServer;

public interface AuthServerDao
{
    List<AuthServer> getByEnterpriseId(long enterpriseId, Limit limit);
    
    AuthServer get(Long id);
}
