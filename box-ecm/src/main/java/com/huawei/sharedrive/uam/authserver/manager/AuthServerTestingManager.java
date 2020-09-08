package com.huawei.sharedrive.uam.authserver.manager;

import pw.cdmi.common.domain.enterprise.ldap.LdapDomainConfig;

public interface AuthServerTestingManager
{
    boolean checkAuthConfig(LdapDomainConfig ldapConfig);
    
    boolean checkFiledMapping(LdapDomainConfig ldapConfig, long authServerId);
    
    boolean checkNtlmServer(LdapDomainConfig ldapConfig);
}
