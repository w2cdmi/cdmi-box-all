package com.huawei.sharedrive.uam.system.service.impl;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.uam.system.domain.MailServer;

import pw.cdmi.common.domain.enterprise.ldap.LdapDomainConfig;

public final class PwdConfuser
{
    private PwdConfuser()
    {
        
    }
    
    public static final String DEFAULT_SHOW_PWD = "**********";
    
    public static String getSysMailPwd(MailServer mailServer, String pwd)
    {
        if (mailServer == null)
        {
            return pwd;
        }
        if (StringUtils.equals(pwd, DEFAULT_SHOW_PWD))
        {
            Object res = mailServer.getAuthPassword();
            if (null == res)
            {
                return null;
            }
            return (String) res;
        }
        return pwd;
    }
    
    public static String getAppMailPwd(MailServer mailServer, String pwd)
    {
        if (mailServer == null)
        {
            return pwd;
        }
        
        if (StringUtils.equals(pwd, DEFAULT_SHOW_PWD))
        {
            Object res = mailServer.getAuthPassword();
            if (null == res)
            {
                return null;
            }
            return (String) res;
        }
        return pwd;
    }
    
    public static String getAppAdPwd(LdapDomainConfig ldapDomainConfig, String pwd)
    {
        if (ldapDomainConfig.getLdapBasicConfig() == null)
        {
            return pwd;
        }
        if (StringUtils.isBlank(ldapDomainConfig.getLdapBasicConfig().getLdapBindAccountPassword()))
        {
            return pwd;
        }
        if (StringUtils.equals(pwd, DEFAULT_SHOW_PWD))
        {
            Object res = ldapDomainConfig.getLdapBasicConfig().getLdapBindAccountPassword();
            if (null == res)
            {
                return null;
            }
            return (String) res;
        }
        return pwd;
    }
    
    public static String getAppNtlmPwd(LdapDomainConfig ldapDomainConfig, String pwd)
    {
        if (ldapDomainConfig.getLdapBasicConfig() == null)
        {
            return pwd;
        }
        if (StringUtils.isBlank(ldapDomainConfig.getLdapBasicConfig().getNtlmPcAccountPasswd()))
        {
            return pwd;
        }
        if (StringUtils.equals(pwd, DEFAULT_SHOW_PWD))
        {
            Object res = ldapDomainConfig.getLdapBasicConfig().getNtlmPcAccountPasswd();
            if (null == res)
            {
                return null;
            }
            return (String) res;
        }
        return pwd;
    }
    
}
