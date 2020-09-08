package pw.cdmi.box.disk.authserver.manager;

import pw.cdmi.common.domain.enterprise.ldap.LdapDomainConfig;

public interface AuthServerConfigManager
{
    LdapDomainConfig getAuthServerObject(Long authServerId);
}
