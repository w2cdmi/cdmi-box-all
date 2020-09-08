package com.huawei.sharedrive.uam.authserver.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.authserver.dao.AuthServerDao;
import com.huawei.sharedrive.uam.authserver.service.AuthServerService;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.common.domain.AuthServer;

@Service
public class AuthServerServiceImpl implements AuthServerService
{
    private static Logger logger = LoggerFactory.getLogger(AuthServerServiceImpl.class);
    
    @Autowired
    private AuthServerDao authServerDao;
    
    @Override
    public int getCountByEnterpriseId(long enterpriseId)
    {
        return authServerDao.getCountByEnterpriseId(enterpriseId);
    }
    
    @Override
    public List<AuthServer> getByEnterpriseId(long enterpriseId, Limit limit)
    {
        return authServerDao.getByEnterpriseId(enterpriseId, limit);
    }
    
    @Override
    public int getCountByNoStatus(long enterpriseId)
    {
        return authServerDao.getCountByNoStatus(enterpriseId);
    }
    
    @Override
    public List<AuthServer> getByNoStatus(long enterpriseId, Limit limit)
    {
        return authServerDao.getByNoStatus(enterpriseId, limit);
    }
    
    @Override
    public void deleteAuthServer(Long id)
    {
        authServerDao.delete(id);
    }
    
    @Override
    public void createAuthServer(AuthServer authServer)
    {
        authServerDao.create(authServer);
    }
    
    @Override
    public AuthServer getAuthServer(Long id)
    {
        return authServerDao.get(id);
    }
    
    @Override
    public AuthServer getAuthServerNoStatus(Long id)
    {
        return authServerDao.getAuthServerNoStatus(id);
    }
    
    @Override
    public List<AuthServer> getAllAuthServer()
    {
        return authServerDao.getFilterd(null, null, null);
    }
    
    @Override
    public AuthServer getDefaultAuthServer()
    {
        return authServerDao.getDefaultAuthServer();
    }
    
    @Override
    public void updateAuthServer(AuthServer authServer)
    {
        authServerDao.updateAuthServerConfig(authServer);
    }
    
    @Override
    public void updateLocalAuth(AuthServer authServer)
    {
        authServerDao.updateLocalAuth(authServer);
    }
    
    /**
     * 
     * @param appId
     * @param appType
     */
    @Override
    public AuthServer enterpriseTypeCheck(long enterpriseId, String type)
    {
        AuthServer authServer = authServerDao.getByEnterpriseIdType(enterpriseId, type);
        if (null == authServer)
        {
            logger.error("authServer is null");
            throw new InvalidParamterException();
        }
        return authServer;
    }
    
    @Override
    public AuthServer getByEnterpriseIdType(long enterpriseId, String type)
    {
        
        return authServerDao.getByEnterpriseIdType(enterpriseId, type);
    }
    
    @Override
    public List<AuthServer> getListByAccountId(long enterpriseId, long accountId)
    {
        return authServerDao.getListByAccountId(enterpriseId, accountId);
    }
    
    @Override
    public void updateStatus(AuthServer authServer)
    {
        
        authServerDao.updateStatus(authServer);
    }
    
}
