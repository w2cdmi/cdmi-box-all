package com.huawei.sharedrive.uam.openapi.manager.impl;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import jcifs.util.Base64;
import pw.cdmi.common.domain.enterprise.ldap.LdapBasicConfig;
import pw.cdmi.common.domain.enterprise.ldap.LdapDomainConfig;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.common.ntlmv2.liferay.NtlmLogonException;
import com.huawei.common.ntlmv2.liferay.NtlmManager;
import com.huawei.common.ntlmv2.liferay.NtlmUserAccount;
import com.huawei.sharedrive.uam.authserver.manager.AuthServerConfigManager;
import com.huawei.sharedrive.uam.exception.BaseRunException;
import com.huawei.sharedrive.uam.exception.InternalServerErrorException;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.exception.LoginAuthFailedException;
import com.huawei.sharedrive.uam.ldapauth.manager.impl.LdapAuthServiceManagerImpl;
import com.huawei.sharedrive.uam.ldapauth.service.LdapAuthService;
import com.huawei.sharedrive.uam.openapi.manager.NtlmApiManager;
import com.huawei.sharedrive.uam.user.domain.NtlmCaches;
import com.huawei.sharedrive.uam.user.service.NtlmCacheService;
import com.huawei.sharedrive.uam.util.PropertiesUtils;

@Component
public class NtlmApiManagerImpl implements NtlmApiManager
{
    private static final Logger LOGGER = LoggerFactory.getLogger(NtlmApiManager.class);
    
    private static final String NTLM_HEARDER = "NTLM";
    
    private static final int NTLM_HEARDER_LENGTH = 5;
    
    private static final int NTLM_MSG_CHALLENGE = 8;
    
    private SecureRandom secureRandom = new SecureRandom();
    
    @Autowired
    private NtlmCacheService ntlmZKService;
    
    @Autowired
    private LdapAuthService ldapAuthService;
    
    @Autowired
    private AuthServerConfigManager authServerConfigManager;
    
    @SuppressWarnings("PMD.PreserveStackTrace")
    @Override
    public String getChallenge(String usernameHash, String key, long authServerId, String ip)
        throws BaseRunException
    {
        byte[] src = Base64.decode(usernameHash.substring(NTLM_HEARDER_LENGTH));
        if (src[NTLM_MSG_CHALLENGE] != 1)
        {
            LOGGER.error("not a t1 message");
            throw new InvalidParameterException();
        }
        Map<String, Object> newMap = getNtlmServer(key, authServerId);
        NtlmManager priNtlmManager = (NtlmManager) newMap.get("priNtlmManager");
        String curServer = newMap.get("currentServer").toString();
        String netBiosName = newMap.get("netBiosName").toString();
        byte[] serverChallenge = new byte[NTLM_MSG_CHALLENGE];
        secureRandom.nextBytes(serverChallenge);
        byte[] challengeMessage;
        try
        {
            challengeMessage = priNtlmManager.negotiate(src, serverChallenge);
        }
        catch (IOException e)
        {
            LOGGER.error("get challenge message failed", e);
            throw new InternalServerErrorException();
        }
        String authorization = Base64.encode(challengeMessage);
        authorization = NTLM_HEARDER + ' ' + authorization;
        ntlmZKService.createNtlmCaches(key, curServer, netBiosName, serverChallenge, ip);
        return authorization;
    }
    
    @Override
    public NtlmUserAccount validateChallenge(String challenge, String key, long authServerId, String ip)
        throws BaseRunException
    {
        byte[] src = Base64.decode(challenge.substring(NTLM_HEARDER_LENGTH));
        NtlmCaches ntlmCaches = ntlmZKService.getNtlmCaches(key);
        if (null == ntlmCaches)
        {
            LOGGER.error("ntlmCaches is null");
            throw new InvalidParamterException();
        }
        checkIpValid(authServerId, ip, ntlmCaches);
        byte[] serverChallenge = ntlmCaches.getServerChallenge();
        if (serverChallenge.length == 0)
        {
            LOGGER.error("challenge is null");
            throw new InvalidParameterException();
        }
        Map<String, Object> newMap = getNtlmServer(key, authServerId);
        ntlmZKService.deleteNtlmCaches(key);
        NtlmManager priNtlmManager = (NtlmManager) newMap.get("priNtlmManager");
        NtlmUserAccount ntlmUserAccount = null;
        try
        {
            ntlmUserAccount = priNtlmManager.authenticate(src, serverChallenge);
        }
        catch (IOException e)
        {
            LOGGER.warn("ntlm login failed", e);
        }
        catch (NoSuchAlgorithmException e)
        {
            LOGGER.warn("ntlm login failed", e);
        }
        catch (NtlmLogonException e)
        {
            LOGGER.warn("ntlm login failed", e);
        }
        if (ntlmUserAccount == null)
        {
            throw new LoginAuthFailedException();
        }
        return ntlmUserAccount;
    }
    
    private void checkIpValid(long authServerId, String ip, NtlmCaches ntlmCaches)
    {
        LdapDomainConfig config = authServerConfigManager.getAuthServerObject(authServerId);
        if (config == null || config.getLdapBasicConfig() == null)
        {
            throw new LoginAuthFailedException();
        }
        LdapBasicConfig ldapBasicConfig = config.getLdapBasicConfig();
        if (ldapBasicConfig != null)
        {
            if (ldapBasicConfig.getIsCheckIp())
            {
                if (StringUtils.isBlank(ip))
                {
                    LOGGER.error("remote ip is blank");
                    throw new InvalidParameterException();
                }
                if (!StringUtils.equals(ip, ntlmCaches.getIp()))
                {
                    LOGGER.error("remote ip is invalid");
                    throw new InvalidParameterException();
                }
            }
        }
    }
    
    private Map<String, Object> getNtlmServer(String key, long authServerId)
        throws InternalServerErrorException
    {
        LdapDomainConfig ldapDomainConfig = authServerConfigManager.getAuthServerObject(authServerId);
        String domainControlServer = "";
        String netBiosName = "";
        NtlmCaches ntlmCaches = ntlmZKService.getNtlmCaches(key);
        if (null == ntlmCaches || StringUtils.isBlank(ntlmCaches.getCurrentServer()))
        {
            domainControlServer = ldapAuthService.getDomainControlServerCache(ldapDomainConfig.getLdapBasicConfig()
                .getDomainControlServer(),
                authServerId);
            netBiosName = getNetBiosName(ldapDomainConfig, domainControlServer);
        }
        else
        {
            domainControlServer = ntlmCaches.getCurrentServer();
            netBiosName = ntlmCaches.getCurrentNetBios();
        }
        String ntlmPcAccount = ldapDomainConfig.getLdapBasicConfig().getNtlmPcAccount();
        String ldapConfigDomainControl = ldapDomainConfig.getLdapBasicConfig().getNetBiosDomainName();
        NtlmManager priNtlmManager = new NtlmManager(ldapConfigDomainControl, domainControlServer,
            netBiosName, ntlmPcAccount, ldapDomainConfig.getLdapBasicConfig().getNtlmPcAccountPasswd());
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("priNtlmManager", priNtlmManager);
        map.put("currentServer", domainControlServer);
        map.put("netBiosName", netBiosName);
        return map;
    }
    
    private String getNetBiosName(LdapDomainConfig ldapDomainConfig, String domainControlServer)
    {
        String netBiosDomainName = "";
        String ldapSplit = PropertiesUtils.getProperty("ldap.split");
        if (StringUtils.isBlank(ldapSplit))
        {
            ldapSplit = LdapAuthServiceManagerImpl.LDAP_SPLIT;
        }
        String[] domainControlServerStr = ldapDomainConfig.getLdapBasicConfig()
            .getDomainControlServer()
            .split(ldapSplit);
        String[] netBiosDomainNameStr = ldapDomainConfig.getLdapBasicConfig().getLdapDns().split(ldapSplit);
        if (netBiosDomainNameStr.length != domainControlServerStr.length)
        {
            netBiosDomainName = netBiosDomainNameStr[0];
            return netBiosDomainName;
        }
        for (int i = 0; i < domainControlServerStr.length; i++)
        {
            if (domainControlServerStr[i].equals(domainControlServer))
            {
                netBiosDomainName = netBiosDomainNameStr[i];
                return netBiosDomainName;
            }
        }
        return netBiosDomainName;
    }
}
