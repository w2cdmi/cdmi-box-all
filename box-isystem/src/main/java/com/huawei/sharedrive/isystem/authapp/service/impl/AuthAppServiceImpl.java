/**
 * 
 */
package com.huawei.sharedrive.isystem.authapp.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.isystem.authapp.dao.AuthAppDao;
import com.huawei.sharedrive.isystem.authapp.service.AppAccessKeyService;
import com.huawei.sharedrive.isystem.authapp.service.AuthAppService;
import com.huawei.sharedrive.isystem.plugin.manager.impl.PluginManagerImpl;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.common.config.service.ConfigManager;
import pw.cdmi.common.domain.AppAccessKey;
import pw.cdmi.uam.domain.AuthApp;

/**
 * @author d00199602
 * 
 */
@Service
public class AuthAppServiceImpl implements AuthAppService
{
    public static final String ZK_APP_UPDATE = "zk_app_change";
    
    private static Logger logger = LoggerFactory.getLogger(AuthAppServiceImpl.class);
    
    @Autowired
    private AppAccessKeyService appAccessKeyService;
    
    @Autowired
    private AuthAppDao authAppDao;
    
    @Autowired
    private ConfigManager configManager;
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.huawei.sharedrive.isystem.authapp.service.AuthAppService#create(com.huawei.
     * sharedrive.isystem.authapp.domain.AuthApp)
     */
    @Override
    public AppAccessKey create(AuthApp authApp)
    {
        AppAccessKey accessKey = createAuthApp(authApp);
        noticeUfmUpdate(authApp.getAuthAppId());
        return accessKey;
    }
    
    /**
     * @param authApp
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AppAccessKey createAuthApp(AuthApp authApp)
    {
        authAppDao.create(authApp);
        AppAccessKey accessKey = appAccessKeyService.createAppAccessKeyForApp(authApp.getAuthAppId());
        return accessKey;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.huawei.sharedrive.isystem.authapp.service.AuthAppService#delete(java.lang.String
     * )
     */
    @Override
    public void delete(String authAppId)
    {
        deleteAuthApp(authAppId);
        noticeUfmUpdate(authAppId);
    }
    
    /**
     * @param authAppId
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteAuthApp(String authAppId)
    {
        authAppDao.delete(authAppId);
        appAccessKeyService.deleteByAppId(authAppId);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.huawei.sharedrive.isystem.authapp.service.AuthAppService#getAuthAppList(com
     * .huawei.sharedrive.isystem.authapp.domain.AuthApp,
     * com.huawei.sharedrive.isystem.core.domain.Order,
     * com.huawei.sharedrive.isystem.core.domain.Limit)
     */
    @Override
    public List<AuthApp> getAuthAppList(AuthApp filter, Order order, Limit limit)
    {
        List<AuthApp> apps = authAppDao.getFilterd(filter, order, limit);
        List<AuthApp> list = new ArrayList<AuthApp>(10);
        for (AuthApp app : apps)
        {
            addKIAAppToList(list, app);
            
        }
        return list;
    }
    
    private void addKIAAppToList(List<AuthApp> list, AuthApp app)
    {
        try
        {
            PluginManagerImpl.isAppKIA(app.getAuthAppId());
        }
        catch (Exception e)
        {
            logger.info(e.getMessage());
            list.add(app);
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.huawei.sharedrive.isystem.authapp.service.AuthAppService#getByAuthAppID(java
     * .lang.String)
     */
    @Override
    public AuthApp getByAuthAppID(String authAppId)
    {
        return authAppDao.getByAuthAppID(authAppId);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.huawei.sharedrive.isystem.authapp.service.AuthAppService#updateAuthApp(com.
     * huawei.sharedrive.isystem.authapp.domain.AuthApp)
     */
    @Override
    @Transactional(propagation = Propagation.NEVER)
    public void updateAuthApp(AuthApp authApp)
    {
        authAppDao.updateAuthApp(authApp);
        noticeUfmUpdate(authApp.getAuthAppId());
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.huawei.sharedrive.isystem.authapp.service.AuthAppService#updateStatus(java.
     * lang.String, int)
     */
    @Override
    @Transactional(propagation = Propagation.NEVER)
    public void updateStatus(String authAppId, int status)
    {
        authAppDao.updateStatus(authAppId, status);
        noticeUfmUpdate(authAppId);
    }
    
    @Override
    public void updateCreate(String authAppId,long createBy){
    	authAppDao.updateAuthAppCreateby(createBy, authAppId);
    	noticeUfmUpdate(authAppId);
    }
    
    private void noticeUfmUpdate(String appId)
    {
        logger.info("[appLog] update app zkdata; " + appId);
        configManager.setConfig(ZK_APP_UPDATE, appId);
    }
    
}
