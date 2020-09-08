package com.huawei.sharedrive.app.authapp.dao.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.authapp.dao.AppAccessKeyDAO;
import com.huawei.sharedrive.app.utils.BusinessConstants;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.common.domain.AppAccessKey;
import pw.cdmi.core.utils.EDToolsEnhance;

@Service
@SuppressWarnings({"deprecation"})
public class AppAccessKeyDAOImpl extends AbstractDAOImpl implements AppAccessKeyDAO
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AppAccessKeyDAOImpl.class);
    
    private static final Map<String, AppAccessKey> ACCESSKEY_CACHE = new ConcurrentHashMap<String, AppAccessKey>(
        BusinessConstants.INITIAL_CAPACITIES);
        
    @Override
    public AppAccessKey getById(String id)
    {
        AppAccessKey appAccessKey = ACCESSKEY_CACHE.get(id);
        if (appAccessKey != null)
        {
            return appAccessKey;
        }
        
        appAccessKey = (AppAccessKey) sqlMapClientTemplate.queryForObject("AppAccessKey.getById", id);
        if (appAccessKey != null)
        {
            appAccessKey.setSecretKey(
                EDToolsEnhance.decode(appAccessKey.getSecretKey(), appAccessKey.getSecretKeyEncodeKey()));
            ACCESSKEY_CACHE.put(id, appAccessKey);
        }
        return appAccessKey;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<AppAccessKey> listByAppId(String appId)
    {
        List<AppAccessKey> list = sqlMapClientTemplate.queryForList("AppAccessKey.getByAppId", appId);
        if (CollectionUtils.isNotEmpty(list))
        {
            for (AppAccessKey accessKey : list)
            {
                accessKey.setSecretKey(
                    EDToolsEnhance.decode(accessKey.getSecretKey(), accessKey.getSecretKeyEncodeKey()));
            }
        }
        return list;
    }
    
    @Override
    public void deleteCache(String id)
    {
        ACCESSKEY_CACHE.remove(id);
        LOGGER.info("Delete access key cache : {}", id);
    }
    
    @Override
    public void deleteCacheByAppId(String appId)
    {
        List<AppAccessKey> list = listByAppId(appId);
        if (CollectionUtils.isEmpty(list))
        {
            return;
        }
        for (AppAccessKey accesskey : list)
        {
            ACCESSKEY_CACHE.remove(accesskey.getId());
            LOGGER.info("Delete access key cache : {}", accesskey.getId());
        }
    }
}
