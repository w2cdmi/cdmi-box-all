package com.huawei.sharedrive.uam.user.service;

import pw.cdmi.common.domain.LdapDomainConfig;

public interface LdapConfigService
{
    LdapDomainConfig getLdapConfigIgnoreCache();
    
    LdapDomainConfig getLdapConfig();
    
    void saveLdapConfig(LdapDomainConfig ldapConfig);
    
    /**
     * 
     * @param domainControlServer
     */
    void delDisableActiveDir(String domainControlServer);
    
    void refreshLdapConfig();
    
}
