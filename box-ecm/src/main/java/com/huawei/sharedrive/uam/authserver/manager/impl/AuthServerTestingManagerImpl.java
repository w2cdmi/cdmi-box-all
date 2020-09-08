package com.huawei.sharedrive.uam.authserver.manager.impl;

import java.io.IOException;
import java.security.SecureRandom;

import javax.naming.CommunicationException;
import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.common.ntlmv2.liferay.NetlogonConnection;
import com.huawei.common.ntlmv2.liferay.NtlmManager;
import com.huawei.common.ntlmv2.liferay.NtlmServiceAccount;
import com.huawei.sharedrive.uam.authserver.manager.AuthServerTestingManager;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.exception.InternalServerErrorException;
import com.huawei.sharedrive.uam.ldapauth.service.LdapAuthService;
import com.huawei.sharedrive.uam.ldapauth.service.impl.LdapAuthServiceImpl;
import com.huawei.sharedrive.uam.util.PropertiesUtils;

import pw.cdmi.common.domain.enterprise.ldap.LdapDomainConfig;

@Component
public class AuthServerTestingManagerImpl implements AuthServerTestingManager
{
    private final static Logger LOGGER = LoggerFactory.getLogger(AuthServerTestingManagerImpl.class);
    
    @Autowired
    private LdapAuthService ldapAuthService;
    
    @Override
    public boolean checkAuthConfig(LdapDomainConfig ldapConfig)
    {
        String domainControlServer = ldapConfig.getLdapBasicConfig().getDomainControlServer();
        LdapContext ldapContext = null;
        boolean isAuth = true;
        try
        {
            if (StringUtils.isBlank(domainControlServer))
            {
                return false;
            }
            String ldapSplit = PropertiesUtils.getProperty("ldap.split");
            if (StringUtils.isBlank(ldapSplit))
            {
                ldapSplit = LdapAuthServiceImpl.LDAP_SPLIT;
            }
            String[] domainControlServerStr = domainControlServer.split(ldapSplit);
            for (int i = 0; i < domainControlServerStr.length; i++)
            {
                ldapContext = ldapAuthService.getLdapContext(ldapConfig, domainControlServerStr[i]);
                ldapContext.close();
            }
        }
        catch (CommunicationException e)
        {
            LOGGER.error("test ldap basic config failed ", e);
            isAuth = false;
        }
        catch (NamingException e)
        {
            isAuth = false;
            LOGGER.error("test ldap basic config failed ", e);
        }
        finally
        {
            closeLdapContext(ldapContext);
        }
        return isAuth;
    }
    
    @Override
    public boolean checkFiledMapping(LdapDomainConfig ldapConfig, long authServerId)
    {
        LdapContext ldapContext = null;
        EnterpriseUser enterpriseUser = null;
        boolean isMapping = false;
        String userName = null;
        if (ldapConfig == null)
        {
            throw new InternalServerErrorException("ldapConfig is null");
        }
        userName = ldapConfig.getLdapFiledMapping().getTestUserName();
        try
        {
            String ldapSplit = PropertiesUtils.getProperty("ldap.split");
            if (StringUtils.isBlank(ldapSplit))
            {
                ldapSplit = LdapAuthServiceImpl.LDAP_SPLIT;
            }
            String[] domainControlServerStr = ldapConfig.getLdapBasicConfig()
                .getDomainControlServer()
                .split(ldapSplit);
            int serverNumber = new SecureRandom().nextInt(domainControlServerStr.length);
            String domainControlServer = domainControlServerStr[serverNumber];
            
            ldapContext = ldapAuthService.getLdapContext(ldapConfig, domainControlServer);
            
            enterpriseUser = ldapAuthService.getLdapUserByLoginName(ldapConfig,
                ldapContext,
                userName,
                authServerId);
            if (null != enterpriseUser)
            {
                isMapping = true;
            }
        }
        catch (CommunicationException e)
        {
            LOGGER.error("check filed mapping failed name:" + userName + " authServerId:" + authServerId, e);
        }
        catch (NamingException e)
        {
            LOGGER.error("check filed mapping failed name:" + userName + " authServerId:" + authServerId, e);
        }
        catch (Exception e)
        {
            LOGGER.error("check filed mapping failed name:" + userName + " authServerId:" + authServerId, e);
        }
        finally
        {
            closeLdapContext(ldapContext);
        }
        return isMapping;
    }
    
    @Override
    public boolean checkNtlmServer(LdapDomainConfig ldapConfig)
    {
        String domainControlServer = ldapConfig.getLdapBasicConfig().getDomainControlServer();
        String ldapDns = ldapConfig.getLdapBasicConfig().getLdapDns();
        boolean isAuth = false;
        if (StringUtils.isBlank(domainControlServer) || StringUtils.isBlank(ldapDns))
        {
            return false;
        }
        String ldapSplit = PropertiesUtils.getProperty("ldap.split");
        if (StringUtils.isBlank(ldapSplit))
        {
            ldapSplit = LdapAuthServiceImpl.LDAP_SPLIT;
        }
        String[] domainControlServerStr = domainControlServer.split(ldapSplit);
        String[] ldapDnsStr = ldapDns.split(ldapSplit);
        if (domainControlServerStr.length != ldapDnsStr.length)
        {
            return false;
        }
        for (int i = 0; i < domainControlServerStr.length; i++)
        {
            isAuth = checkNtlmServerByNoCache(null, ldapConfig, domainControlServerStr[i], ldapDnsStr[i]);
            if (!isAuth)
            {
                return isAuth;
            }
        }
        return isAuth;
    }
    
    @SuppressWarnings("unused")
    private boolean checkNtlmServerByNoCache(NetlogonConnection connection, LdapDomainConfig ldapConfig,
        String domainControlServer, String ldapDns)
    {
        try
        {
            SecureRandom random = new SecureRandom();
            String ntlmPcAccount = ldapConfig.getLdapBasicConfig().getNtlmPcAccount();
            new NtlmManager(ldapConfig.getLdapBasicConfig().getNetBiosDomainName(), domainControlServer,
                ldapDns, ntlmPcAccount, ldapConfig.getLdapBasicConfig().getNtlmPcAccountPasswd());
            connection = new NetlogonConnection();
            connection.connect(domainControlServer, ldapDns, new NtlmServiceAccount(ntlmPcAccount,
                ldapConfig.getLdapBasicConfig().getNtlmPcAccountPasswd()), random);
            
        }
        catch (Exception e)
        {
            LOGGER.error("connect domain control fail", e);
            return false;
        }
        finally
        {
            if (connection != null)
            {
                try
                {
                    connection.disconnect();
                    connection = null;
                }
                catch (IOException e)
                {
                    LOGGER.error("close domain control conn fail", e);
                }
            }
        }
        return true;
    }
    
    private void closeLdapContext(LdapContext ldapContext)
    {
        try
        {
            if (ldapContext != null)
            {
                ldapContext.close();
            }
        }
        catch (NamingException e)
        {
            LOGGER.error("close ldapContext failed ", e);
        }
    }
}
