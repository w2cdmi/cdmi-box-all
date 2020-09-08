package com.huawei.sharedrive.uam.user.service;

import com.huawei.sharedrive.uam.exception.LoginAuthFailedException;
import com.huawei.sharedrive.uam.oauth2.domain.UserToken;

import pw.cdmi.common.domain.LdapDomainConfig;

public interface LdapService
{
    /**
     * 
     * @param user
     * @param loginName
     * @param password
     * @return
     * @throws LoginAuthFailedException
     */
    UserToken authenticate(String loginName, String password) throws LoginAuthFailedException;
    
    boolean checkLdapConfig(LdapDomainConfig ldapConfig);
    
    /**
     * 
     * @param ldapConfig
     * @return
     */
    boolean checkNtlmServer(LdapDomainConfig ldapConfig);
    
    /**
     * 
     * @param loginName
     * @return
     */
    UserToken getLdapUserByLoginName(String loginName);
}
