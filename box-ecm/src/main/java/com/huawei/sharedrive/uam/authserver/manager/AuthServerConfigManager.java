package com.huawei.sharedrive.uam.authserver.manager;

import pw.cdmi.common.domain.enterprise.ldap.LdapBasicConfig;
import pw.cdmi.common.domain.enterprise.ldap.LdapDomainConfig;
import pw.cdmi.common.domain.enterprise.ldap.LdapFiledMapping;
import pw.cdmi.common.domain.enterprise.ldap.LdapNodeFilterConfig;

public interface AuthServerConfigManager
{
    LdapDomainConfig getAuthServerObject(Long authServerId);
    
    void updateLdapBasciConfig(LdapBasicConfig ldapBasicConfig, Long authServerId);
    
    void updateLdapFiledMapping(LdapFiledMapping ldapFiledMapping, Long authServerId);
    
    void updateLdapNodeFilterConfig(LdapNodeFilterConfig ldapNodeFilterConfig, Long authServerId);
    
    LdapDomainConfig updateFilterNode(LdapBasicConfig ldapBasicConfig, Long authServerId);
    
    void updateSearchRule(LdapBasicConfig ldapBasicConfig, Long authServerId);
    
    boolean isSyncAndDisplayNode(LdapDomainConfig ldapDomainConfig, Long authServerId, String dn);
}
