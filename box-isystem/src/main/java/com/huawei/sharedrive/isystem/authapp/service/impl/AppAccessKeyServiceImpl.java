package com.huawei.sharedrive.isystem.authapp.service.impl;

import java.util.Date;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.isystem.authapp.dao.AppAccessKeyDAO;
import com.huawei.sharedrive.isystem.authapp.dao.AuthAppDao;
import com.huawei.sharedrive.isystem.authapp.service.AppAccessKeyService;
import com.huawei.sharedrive.isystem.util.RandomKeyGUID;

import pw.cdmi.common.config.service.ConfigManager;
import pw.cdmi.common.domain.AppAccessKey;
import pw.cdmi.uam.domain.AuthApp;

@Service
public class AppAccessKeyServiceImpl implements AppAccessKeyService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AppAccessKeyServiceImpl.class);
    
    public static final String ZK_ACCESSKEY_DELETE = "zk_accesskey_delete";
    
    public static final String ZK_APP_ACCESSKEY_DELETE = "zk_app_accesskey_delete";
    
    @Autowired
    private ConfigManager configManager;
    
    @Autowired
    private AppAccessKeyDAO appAccessKeyDAO;
    
    @Autowired
    private AuthAppDao authAppDao;
    
    @Override
    public AppAccessKey getById(String id)
    {
        return appAccessKeyDAO.getById(id);
    }
    
    @Override
    public List<AppAccessKey> getByAppId(String appId)
    {
        return appAccessKeyDAO.getByAppId(appId);
    }
    
    @Override
    public int delete(String id)
    {
        int result = appAccessKeyDAO.delete(id);
        LOGGER.info("[zk log] Delete acccesskey: {}", id);
        configManager.setConfig(ZK_ACCESSKEY_DELETE, id);
        return result;
    }
    
    @Override
    public AppAccessKey createAppAccessKeyForApp(String appId)
    {
        AuthApp app = authAppDao.getByAuthAppID(appId);
        if (app == null)
        {
            throw new ConstraintViolationException("can not found app for id " + appId, null);
        }
        List<AppAccessKey> list = getByAppId(appId);
        if (list.size() >= ACCESSKEY_PER_APP_LIMIT)
        {
            return null;
        }
        AppAccessKey key = new AppAccessKey();
        key.setAppId(appId);
        key.setId(RandomKeyGUID.getSecureRandomGUID());
        key.setSecretKey(RandomKeyGUID.getSecureRandomGUID());
        key.setCreatedAt(new Date());
        appAccessKeyDAO.create(key);
        return key;
    }
    
    @Override
    public int deleteByAppId(String appId)
    {
        int result = appAccessKeyDAO.deleteByAppId(appId);
        LOGGER.info("[zk log] Delete acccesskey by appId: {}", appId);
        configManager.setConfig(ZK_APP_ACCESSKEY_DELETE, appId);
        return result;
    }
    
    @Override
    @Transactional(propagation = Propagation.NEVER)
    public void updateFirstScan(AppAccessKey appId)
    {
        appAccessKeyDAO.updateFirstScan(appId);
    }
}
