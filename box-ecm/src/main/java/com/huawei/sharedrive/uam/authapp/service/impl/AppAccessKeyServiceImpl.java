package com.huawei.sharedrive.uam.authapp.service.impl;

import java.util.Date;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.authapp.dao.AppAccessKeyDAO;
import com.huawei.sharedrive.uam.authapp.dao.AuthAppDao;
import com.huawei.sharedrive.uam.authapp.service.AppAccessKeyService;
import com.huawei.sharedrive.uam.util.RandomKeyGUID;

import pw.cdmi.common.domain.AppAccessKey;
import pw.cdmi.core.utils.UUIDUtils;
import pw.cdmi.uam.domain.AuthApp;

@Service
public class AppAccessKeyServiceImpl implements AppAccessKeyService
{
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
        return appAccessKeyDAO.delete(id);
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
        key.setId(UUIDUtils.getValueAfterMD5());
        key.setSecretKey(RandomKeyGUID.getSecureRandomGUID());
        key.setCreatedAt(new Date());
        appAccessKeyDAO.create(key);
        return key;
    }
    
    @Override
    public int deleteByAppId(String appId)
    {
        return appAccessKeyDAO.deleteByAppId(appId);
    }
}
