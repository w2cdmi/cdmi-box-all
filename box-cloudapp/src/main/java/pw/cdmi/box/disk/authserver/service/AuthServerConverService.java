package pw.cdmi.box.disk.authserver.service;

import pw.cdmi.common.domain.AuthServer;
import pw.cdmi.common.domain.enterprise.ldap.LdapBasicConfig;
import pw.cdmi.common.domain.enterprise.ldap.LdapDomainConfig;

public interface AuthServerConverService
{
    LdapDomainConfig convertedToObject(AuthServer authConfig);
    
    String convertedToAuthConfig(LdapDomainConfig ldapDomainConfig);
    
    LdapBasicConfig transLdapObject(LdapBasicConfig ldapBasicConfig,
        LdapDomainConfig ldapDomainConfig, String type);
}
