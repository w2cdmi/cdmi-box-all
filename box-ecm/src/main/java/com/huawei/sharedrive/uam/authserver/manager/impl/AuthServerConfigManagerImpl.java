package com.huawei.sharedrive.uam.authserver.manager.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.authserver.manager.AuthServerConfigManager;
import com.huawei.sharedrive.uam.authserver.service.AuthServerConverService;
import com.huawei.sharedrive.uam.authserver.service.AuthServerService;
import com.huawei.sharedrive.uam.authserver.service.impl.AuthServerConverServiceImpl;
import com.huawei.sharedrive.uam.exception.NoSuchAuthServerException;
import com.huawei.sharedrive.uam.ldapauth.manager.impl.LdapAuthServiceManagerImpl;

import pw.cdmi.common.cache.CacheClient;
import pw.cdmi.common.domain.AuthServer;
import pw.cdmi.common.domain.enterprise.ldap.LdapBasicConfig;
import pw.cdmi.common.domain.enterprise.ldap.LdapDomainConfig;
import pw.cdmi.common.domain.enterprise.ldap.LdapFiledMapping;
import pw.cdmi.common.domain.enterprise.ldap.LdapNodeFilterConfig;
import pw.cdmi.core.utils.CacheParameterUtils;

@Component
public class AuthServerConfigManagerImpl implements AuthServerConfigManager
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthServerConfigManagerImpl.class);
    
    @Autowired
    private AuthServerService authServerService;
    
    @Autowired
    private AuthServerConverService authServerConverService;
    
    @Resource(name = "cacheClient")
    private CacheClient cacheClient;
    
    @Override
    public LdapDomainConfig getAuthServerObject(Long authServerId)
    {
        AuthServer authServer = authServerService.getAuthServer(authServerId);
        if (null == authServer)
        {
            LOGGER.error("[authServer] authServer is null" + authServerId);
            throw new NoSuchAuthServerException("[authServer] authServer is null" + authServerId);
        }
        LdapDomainConfig ldapDomainConfig = (LdapDomainConfig) cacheClient.getCache(CacheParameterUtils.AUTHSERVER_LDAP_DOMAIN_CONFIG
            + authServerId);
        // set cache
        if (null == ldapDomainConfig)
        {
            ldapDomainConfig = authServerConverService.convertedToObject(authServer);
            cacheClient.setCache(CacheParameterUtils.AUTHSERVER_LDAP_DOMAIN_CONFIG + authServerId,
                ldapDomainConfig,
                CacheParameterUtils.AUTHSERVER_LDAP_DOMAIN_CONFIG_TIME_OUT);
        }
        return ldapDomainConfig;
    }
    
    @Override
    public void updateLdapBasciConfig(LdapBasicConfig ldapBasicConfig, Long authServerId)
    {
        AuthServer authServer = authServerService.getAuthServer(authServerId);
        if (null == authServer)
        {
            LOGGER.error("[authServer] authServer is null" + authServerId);
            throw new NoSuchAuthServerException("[authServer] authServer is null" + authServerId);
        }
        LdapDomainConfig selLdapDomainConfig = authServerConverService.convertedToObject(authServer);
        LdapBasicConfig transLdapBasicConfig = authServerConverService.transLdapObject(ldapBasicConfig,
            selLdapDomainConfig,
            AuthServerConverServiceImpl.TRANS_BASIC);
        LdapDomainConfig ldapDomainConfig = new LdapDomainConfig();
        ldapDomainConfig.setLdapBasicConfig(transLdapBasicConfig);
        String ldapBasicConfigXml = authServerConverService.convertedToAuthConfig(ldapDomainConfig);
        authServer.setLdapBasicXml(ldapBasicConfigXml);
        authServerService.updateAuthServer(authServer);
        cacheClient.deleteCache(CacheParameterUtils.AUTHSERVER_LDAP_DOMAIN_CONFIG + authServerId);
        cacheClient.deleteCache(CacheParameterUtils.AUTHSERVER_DOMAIN_CONTROL_SERVER + authServerId);
    }
    
    @Override
    public void updateLdapFiledMapping(LdapFiledMapping ldapFiledMapping, Long authServerId)
    {
        AuthServer authServer = authServerService.getAuthServer(authServerId);
        if (null == authServer)
        {
            LOGGER.error("[authServer] authServer is null" + authServerId);
            throw new NoSuchAuthServerException("[authServer] authServer is null" + authServerId);
        }
        LdapDomainConfig ldapDomainConfig = new LdapDomainConfig();
        ldapDomainConfig.setLdapFiledMapping(ldapFiledMapping);
        String ldapFiledMappingXml = authServerConverService.convertedToAuthConfig(ldapDomainConfig);
        authServer.setFiledMappingXml(ldapFiledMappingXml);
        authServerService.updateAuthServer(authServer);
        cacheClient.deleteCache(CacheParameterUtils.AUTHSERVER_LDAP_DOMAIN_CONFIG + authServerId);
    }
    
    @Override
    public void updateLdapNodeFilterConfig(LdapNodeFilterConfig ldapNodeFilterConfig, Long authServerId)
    {
        AuthServer authServer = authServerService.getAuthServer(authServerId);
        if (null == authServer)
        {
            LOGGER.error("[authServer] authServer is null" + authServerId);
            throw new NoSuchAuthServerException("[authServer] authServer is null" + authServerId);
        }
        LdapDomainConfig ldapDomainConfig = new LdapDomainConfig();
        ldapDomainConfig.setLdapNodeFilterConfig(ldapNodeFilterConfig);
        String ldapNodesFilterXml = authServerConverService.convertedToAuthConfig(ldapDomainConfig);
        authServer.setLdapNodesFilterXml(ldapNodesFilterXml);
        authServerService.updateAuthServer(authServer);
        cacheClient.deleteCache(CacheParameterUtils.AUTHSERVER_LDAP_DOMAIN_CONFIG + authServerId);
    }
    
    @Override
    public LdapDomainConfig updateFilterNode(LdapBasicConfig ldapBasicConfig, Long authServerId)
    {
        AuthServer authServer = authServerService.getAuthServer(authServerId);
        if (null == authServer)
        {
            LOGGER.error("[authServer] authServer is null" + authServerId);
            throw new NoSuchAuthServerException("[authServer] authServer is null" + authServerId);
        }
        LdapDomainConfig selLdapDomainConfig = authServerConverService.convertedToObject(authServer);
        LdapBasicConfig transLdapBasicConfig = authServerConverService.transLdapObject(ldapBasicConfig,
            selLdapDomainConfig,
            AuthServerConverServiceImpl.TRANS_NODE);
        LdapDomainConfig ldapDomainConfig = new LdapDomainConfig();
        ldapDomainConfig.setLdapBasicConfig(transLdapBasicConfig);
        String ldapBasicConfigXml = authServerConverService.convertedToAuthConfig(ldapDomainConfig);
        authServer.setLdapBasicXml(ldapBasicConfigXml);
        authServerService.updateAuthServer(authServer);
        selLdapDomainConfig.setLdapBasicConfig(transLdapBasicConfig);
        cacheClient.deleteCache(CacheParameterUtils.AUTHSERVER_LDAP_DOMAIN_CONFIG + authServerId);
        return selLdapDomainConfig;
    }
    
    @Override
    public void updateSearchRule(LdapBasicConfig ldapBasicConfig, Long authServerId)
    {
        AuthServer authServer = authServerService.getAuthServer(authServerId);
        if (null == authServer)
        {
            LOGGER.error("[authServer] authServer is null" + authServerId);
            throw new NoSuchAuthServerException("[authServer] authServer is null" + authServerId);
        }
        LdapDomainConfig selLdapDomainConfig = authServerConverService.convertedToObject(authServer);
        LdapBasicConfig transLdapBasicConfig = authServerConverService.transLdapObject(ldapBasicConfig,
            selLdapDomainConfig,
            AuthServerConverServiceImpl.TRANS_SEARCH);
        LdapDomainConfig ldapDomainConfig = new LdapDomainConfig();
        ldapDomainConfig.setLdapBasicConfig(transLdapBasicConfig);
        String ldapBasicConfigXml = authServerConverService.convertedToAuthConfig(ldapDomainConfig);
        authServer.setLdapBasicXml(ldapBasicConfigXml);
        authServerService.updateAuthServer(authServer);
        cacheClient.deleteCache(CacheParameterUtils.AUTHSERVER_LDAP_DOMAIN_CONFIG + authServerId);
    }
    
    @Override
    public boolean isSyncAndDisplayNode(LdapDomainConfig ldapDomainConfig, Long authServerId, String dn)
    {
        
        if (ldapDomainConfig == null)
        {
            AuthServer authServer = authServerService.getAuthServer(authServerId);
            if (null == authServer)
            {
                LOGGER.error("[authServer] authServer is null" + authServerId);
                throw new NoSuchAuthServerException("[authServer] authServer is null" + authServerId);
            }
            ldapDomainConfig = authServerConverService.convertedToObject(authServer);
        }
        LdapNodeFilterConfig ldapNodeFilterConfig = ldapDomainConfig.getLdapNodeFilterConfig();
        dn = dn.toLowerCase(Locale.ENGLISH);
        if (ldapNodeFilterConfig == null)
        {
            return false;
        }
        String syncNode = ldapNodeFilterConfig.getSyncNode();
        if (StringUtils.isNotBlank(syncNode))
        {
            List<String> syncList = new ArrayList<String>(
                Arrays.asList(syncNode.split(LdapAuthServiceManagerImpl.LDAP_SPLIT)));
            if (syncList.contains(dn))
            {
                return true;
            }
        }
        String displayNode = ldapNodeFilterConfig.getDisplayNode();
        if (StringUtils.isNotBlank(displayNode))
        {
            List<String> displayList = new ArrayList<String>(
                Arrays.asList(displayNode.split(LdapAuthServiceManagerImpl.LDAP_SPLIT)));
            if (displayList.contains(dn))
            {
                return true;
            }
        }
        return false;
    }
    
}
