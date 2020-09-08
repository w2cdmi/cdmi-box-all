package com.huawei.sharedrive.app.authapp.service.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.authapp.dao.AppAccessKeyDAO;
import com.huawei.sharedrive.app.authapp.service.AppAccessKeyService;

import pw.cdmi.common.domain.AppAccessKey;

@Service
public class AppAccessKeyServiceImpl implements AppAccessKeyService
{
    @Autowired
    private AppAccessKeyDAO appAccessKeyDAO;
    
    @Override
    public AppAccessKey getById(String id)
    {
        return appAccessKeyDAO.getById(id);
    }
    
    @Override
    public AppAccessKey getByAppId(String appId)
    {
        List<AppAccessKey> list = appAccessKeyDAO.listByAppId(appId);
        if (CollectionUtils.isEmpty(list))
        {
            return null;
        }
        return list.get(0);
    }
    
    @Override
    public void deleteCache(String id)
    {
        appAccessKeyDAO.deleteCache(id);
    }
    
    @Override
    public void deleteCacheByAppId(String appId)
    {
        appAccessKeyDAO.deleteCacheByAppId(appId);
    }
    
}
