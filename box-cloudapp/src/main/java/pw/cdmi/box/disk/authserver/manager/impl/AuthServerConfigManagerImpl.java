package pw.cdmi.box.disk.authserver.manager.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pw.cdmi.box.disk.authserver.manager.AuthServerConfigManager;
import pw.cdmi.box.disk.authserver.service.AuthServerConverService;
import pw.cdmi.box.disk.authserver.service.AuthServerService;
import pw.cdmi.common.cache.CacheClient;
import pw.cdmi.common.domain.AuthServer;
import pw.cdmi.common.domain.enterprise.ldap.LdapDomainConfig;
import pw.cdmi.core.exception.InternalServerErrorException;
import pw.cdmi.core.utils.CacheParameterUtils;

@Component
public class AuthServerConfigManagerImpl implements AuthServerConfigManager
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthServerConfigManagerImpl.class);
    
    @Autowired
    private AuthServerService authServerService;
    
    @Autowired
    private AuthServerConverService authServerConverService;
    
    @Resource(name = "uamCacheClient")
    private CacheClient cacheClient;
    
    @Override
    public LdapDomainConfig getAuthServerObject(Long authServerId)
    {
        LdapDomainConfig ldapDomainConfig = (LdapDomainConfig) cacheClient.getCache(CacheParameterUtils.AUTHSERVER_LDAP_DOMAIN_CONFIG
            + authServerId);
        // set cache
        if (null == ldapDomainConfig)
        {
            AuthServer authServer = authServerService.getAuthServer(authServerId);
            if (null == authServer)
            {
                LOGGER.error("[authServer] authServer is null" + authServerId);
                throw new InternalServerErrorException("[authServer] authServer is null" + authServerId);
            }
            
            ldapDomainConfig = authServerConverService.convertedToObject(authServer);
            cacheClient.setCache(CacheParameterUtils.AUTHSERVER_LDAP_DOMAIN_CONFIG + authServerId,
                ldapDomainConfig,
                CacheParameterUtils.AUTHSERVER_LDAP_DOMAIN_CONFIG_TIME_OUT);
        }
        return ldapDomainConfig;
    }
}
