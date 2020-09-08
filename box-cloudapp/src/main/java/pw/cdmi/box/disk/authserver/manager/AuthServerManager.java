package pw.cdmi.box.disk.authserver.manager;

import java.util.List;

import pw.cdmi.common.domain.AuthServer;

public interface AuthServerManager
{
    List<AuthServer> getByEnterpriseId(long enterpriseId);
}
