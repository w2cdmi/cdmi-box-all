package com.huawei.sharedrive.uam.authserver.manager.impl;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.uam.authserver.manager.AuthServerManager;
import com.huawei.sharedrive.uam.authserver.service.AccountAuthserverService;
import com.huawei.sharedrive.uam.authserver.service.AuthServerService;
import com.huawei.sharedrive.uam.exception.BusinessException;
import com.huawei.sharedrive.uam.exception.NoSuchAuthServerException;
import com.huawei.sharedrive.uam.ldapauth.manager.AuthServiceManager;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.common.domain.AuthServer;
import pw.cdmi.core.utils.SpringContextUtil;

@Component
public class AuthServerManagerImpl implements AuthServerManager
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthServerManagerImpl.class);
    
    private final static String SERVICE_SUFFIX = "ServiceManager";
    
    @Autowired
    private AuthServerService authServerService;
    
    @Autowired
    private AccountAuthserverService accountAuthserverService;
    
    @Override
    public AuthServer enterpriseTypeCheck(long enterpriseId, String type)
    {
        return authServerService.enterpriseTypeCheck(enterpriseId, type);
    }
    
    @Override
    public void createAuthServer(AuthServer authServer)
    {
        authServerService.createAuthServer(authServer);
    }
    
    @Override
    public Page<AuthServer> getByEnterpriseId(long enterpriseId, PageRequest pageRequest)
    {
        int total = authServerService.getCountByEnterpriseId(enterpriseId);
        List<AuthServer> content = authServerService.getByEnterpriseId(enterpriseId, pageRequest.getLimit());
        Page<AuthServer> page = new PageImpl<AuthServer>(content, pageRequest, total);
        return page;
        
    }
    
    @Override
    public Page<AuthServer> getByNoStatus(long enterpriseId, PageRequest pageRequest)
    {
        int total = authServerService.getCountByNoStatus(enterpriseId);
        List<AuthServer> content = authServerService.getByNoStatus(enterpriseId, pageRequest.getLimit());
        Page<AuthServer> page = new PageImpl<AuthServer>(content, pageRequest, total);
        return page;
        
    }
    
    @Override
    public List<AuthServer> getByEnterpriseId(long enterpriseId)
    {
        return authServerService.getByEnterpriseId(enterpriseId, null);
    }
    
    @Override
    public List<AuthServer> getByNoStatus(long enterpriseId)
    {
        return authServerService.getByNoStatus(enterpriseId, null);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteAuthServer(Long authServerId)
    {
        authServerService.deleteAuthServer(authServerId);
        accountAuthserverService.deleteByAuthServerId(authServerId);
    }
    
    @Override
    public AuthServer getAuthServer(Long id)
    {
        
        AuthServer authServer = authServerService.getAuthServer(id);
        if (null == authServer)
        {
            LOGGER.error("[authServer] authServer is null" + id);
            throw new NoSuchAuthServerException("[authServer] authServer is null" + id);
        }
        return authServer;
    }
    
    @Override
    public AuthServer getAuthServerNoStatus(Long id)
    {
        AuthServer authServer = authServerService.getAuthServerNoStatus(id);
        if (null == authServer)
        {
            LOGGER.error("[authServer] authServer is null" + id);
            throw new NoSuchAuthServerException("[authServer] authServer is null" + id);
        }
        return authServer;
    }
    
    @Override
    public AuthServiceManager getAuthService(long authServerId)
    {
        AuthServer authServer = authServerService.getAuthServer(authServerId);
        if (authServer == null)
        {
            throw new BusinessException("authServer is  null authServerId:" + authServerId);
        }
        String type = AuthServer.AUTH_TYPE_LOCAL;
        if (StringUtils.equals(AuthServer.AUTH_TYPE_AD, authServer.getType())
            || StringUtils.equals(AuthServer.AUTH_TYPE_LDAP, authServer.getType()))
        {
            type = AuthServer.AUTH_TYPE_LDAP;
        }
        String newType = type.substring(0, 1).toLowerCase(Locale.getDefault()) + type.substring(1);
        String serviceName = new StringBuffer().append(newType).append(SERVICE_SUFFIX).toString();
        return (AuthServiceManager) SpringContextUtil.getBean(serviceName);
    }
    
    @Override
    public List<AuthServer> getListByAccountId(long enterpriseId, long accountId)
    {
        return authServerService.getListByAccountId(enterpriseId, accountId);
    }
    
    @Override
    public void updateStatus(long id, byte status)
    {
        AuthServer authServer = new AuthServer();
        authServer.setId(id);
        authServer.setStatus(status);
        authServerService.updateStatus(authServer);
    }
    
}
