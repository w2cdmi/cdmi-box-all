package com.huawei.sharedrive.uam.authapp.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.authapp.dao.AuthAppDao;
import com.huawei.sharedrive.uam.authapp.domain.AuthAppExtend;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.core.utils.CacheParameterUtils;
import pw.cdmi.core.utils.EDToolsEnhance;
import pw.cdmi.uam.domain.AuthApp;

@Service
@SuppressWarnings({"unchecked", "deprecation"})
public class AuthAppDaoImpl extends CacheableSqlMapClientDAO implements AuthAppDao
{
    private static Logger logger = LoggerFactory.getLogger(AuthAppDaoImpl.class);
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.huawei.sharedrive.isystem.authapp.dao.AuthAppDao#getByAuthAppID(java.lang.String
     * )
     */
    @Override
    public AuthApp getByAuthAppID(String authAppId)
    {
        AuthApp authApp = null;
        if (isCacheSupported())
        {
            String key = CacheParameterUtils.AUTHAPP_CACHE_ID + authAppId;
            authApp = (AuthApp) getCacheClient().getCache(key);
            if (authApp != null)
            {
                return authApp;
            }
            authApp = (AuthApp) sqlMapClientTemplate.queryForObject("AuthApp.getByAuthAppID", authAppId);
            if (authApp == null)
            {
                return null;
            }
            if (authApp.getUfmSecretKey() != null)
            {
                authApp.setUfmSecretKey(EDToolsEnhance.decode(authApp.getUfmSecretKey(),
                    authApp.getUfmSecretKeyEncodeKey()));
                authApp.setUfmSecretKeyEncodeKey(null);
            }
            getCacheClient().setCache(key,
                authApp,
                System.currentTimeMillis() + CacheParameterUtils.AUTHAPP_CACHE_TIME_OUT);
            return authApp;
        }
        authApp = (AuthApp) sqlMapClientTemplate.queryForObject("AuthApp.getByAuthAppID", authAppId);
        if (authApp != null && authApp.getUfmSecretKey() != null)
        {
            authApp.setUfmSecretKey(EDToolsEnhance.decode(authApp.getUfmSecretKey(),
                authApp.getUfmSecretKeyEncodeKey()));
            authApp.setUfmSecretKeyEncodeKey(null);
        }
        return authApp;
    }
    
    @Override
    public AuthApp getDefaultWebApp()
    {
        AuthApp authApp = (AuthApp) sqlMapClientTemplate.queryForObject("AuthApp.getDefaultWebApp");
        if (authApp != null)
        {
            authApp.setUfmSecretKey(EDToolsEnhance.decode(authApp.getUfmSecretKey(),
                authApp.getUfmSecretKeyEncodeKey()));
            authApp.setUfmSecretKeyEncodeKey(null);
        }
        return authApp;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.huawei.sharedrive.isystem.authapp.dao.AuthAppDao#getFilterd(com.huawei.sharedrive
     * .isystem.authapp.domain.AuthApp, com.huawei.sharedrive.isystem.core.domain.Order,
     * com.huawei.sharedrive.isystem.core.domain.Limit)
     */
    @Override
    public List<AuthApp> getFilterd(AuthAppExtend filter, Order order, Limit limit)
    {
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("filter", filter);
        map.put("order", order);
        map.put("limit", limit);
        List<AuthApp> appLists = sqlMapClientTemplate.queryForList("AuthApp.getFilterd", map);
        for (AuthApp app : appLists)
        {
            app.setUfmSecretKey(EDToolsEnhance.decode(app.getUfmSecretKey(), app.getUfmSecretKeyEncodeKey()));
            app.setUfmSecretKeyEncodeKey(null);
        }
        return appLists;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.huawei.sharedrive.isystem.authapp.dao.AuthAppDao#getFilterdCount(com.huawei
     * .sharedrive.isystem.authapp.domain.AuthApp)
     */
    @Override
    public int getFilterdCount(AuthAppExtend filter)
    {
        Map<String, Object> map = new HashMap<String, Object>(1);
        map.put("filter", filter);
        return (Integer) sqlMapClientTemplate.queryForObject("AuthApp.getFilterdCount", map);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.huawei.sharedrive.isystem.authapp.dao.AuthAppDao#delete(java.lang.String)
     */
    @Override
    public void delete(String authAppId)
    {
        sqlMapClientTemplate.delete("AuthApp.delete", authAppId);
        String key = CacheParameterUtils.AUTHAPP_CACHE_ID + authAppId;
        deleteCacheAfterCommit(key);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.huawei.sharedrive.isystem.authapp.dao.AuthAppDao#create(com.huawei.sharedrive
     * .isystem.authapp.domain.AuthApp)
     */
    @Override
    public void create(AuthApp authApp)
    {
        authApp.setCreatedAt(new Date());
        String ufmSecretKey = authApp.getUfmSecretKey();
        if (!StringUtils.isBlank(ufmSecretKey))
        {
            Map<String, String> map = EDToolsEnhance.encode(ufmSecretKey);
            authApp.setUfmSecretKey(map.get(EDToolsEnhance.ENCRYPT_CONTENT));
            authApp.setUfmSecretKeyEncodeKey(map.get(EDToolsEnhance.ENCRYPT_KEY));
            logger.info("set crypt in uam.AuthApp");
        }
        sqlMapClientTemplate.insert("AuthApp.insert", authApp);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.huawei.sharedrive.isystem.authapp.dao.AuthAppDao#updateAuthApp(com.huawei.
     * sharedrive.isystem.authapp.domain.AuthApp)
     */
    @Override
    public void updateAuthApp(AuthApp authApp)
    {
        authApp.setModifiedAt(new Date());
        String ufmSecretKey = authApp.getUfmSecretKey();
        if (!StringUtils.isBlank(ufmSecretKey))
        {
            Map<String, String> map = EDToolsEnhance.encode(ufmSecretKey);
            authApp.setUfmSecretKey(map.get(EDToolsEnhance.ENCRYPT_CONTENT));
            authApp.setUfmSecretKeyEncodeKey(map.get(EDToolsEnhance.ENCRYPT_KEY));
            logger.info("change crypt in uam.AuthApp");
        }
        sqlMapClientTemplate.update("AuthApp.updateAuthApp", authApp);
        String key = CacheParameterUtils.AUTHAPP_CACHE_ID + authApp.getAuthAppId();
        deleteCacheAfterCommit(key);
    }
    
    @Override
    public int getCountByAuthentication(long enterpriseId)
    {
        return (int) sqlMapClientTemplate.queryForObject("AuthApp.getCountByAuthentication", enterpriseId);
    }
    
    @Override
    public List<AuthApp> getByAuthentication(long enterpriseId, Limit limit)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("enterpriseId", enterpriseId);
        map.put("limit", limit);
        return sqlMapClientTemplate.queryForList("AuthApp.getByAuthentication", map);
    }
    
    @Override
    public void updateNetworkRegionStatus(AuthApp authApp)
    {
        sqlMapClientTemplate.update("AuthApp.updateNetworkRegionStatus", authApp);
        String key = CacheParameterUtils.AUTHAPP_CACHE_ID + authApp.getAuthAppId();
        deleteCacheAfterCommit(key);
    }

    @Override
    public List<String> getAppId()
    {
        List<String> appIds = sqlMapClientTemplate.queryForList("AuthApp.getAppId");
        return appIds;
    }
    
}
