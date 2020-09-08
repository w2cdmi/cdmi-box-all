package com.huawei.sharedrive.uam.ldapauth.service;

import javax.naming.CommunicationException;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.ldap.LdapContext;

import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;

import pw.cdmi.common.domain.enterprise.ldap.LdapDomainConfig;

public interface LdapAuthService
{
    LdapContext getLdapContext(LdapDomainConfig config, String domainControlServer)
        throws CommunicationException, NamingException;
    
    void authenticate(LdapContext ldapContext, String userDN, String password);
    
    EnterpriseUser getLdapUserByEmail(LdapDomainConfig config, LdapContext ldapContext, String email,
        Long authServerId);
    
    EnterpriseUser getLdapUserByLoginName(LdapDomainConfig config, LdapContext ldapContext, String userName,
        Long authServerId);
    
    String getDomainControlServerCache(String domainControlServer, Long authServerId);
    
    EnterpriseUser getUserByAttri(LdapDomainConfig config, Attributes attributes)
        throws NamingException;
}
