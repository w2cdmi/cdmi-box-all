package com.huawei.sharedrive.uam.adminlog.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.adminlog.dao.UserLogConfigDao;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.common.domain.SystemConfig;

@Service("UserLogConfigDao")
@SuppressWarnings("deprecation")
public class UserLogConfigDaoImpl extends CacheableSqlMapClientDAO implements UserLogConfigDao
{
    private static final String CACHE_KEY = "UserLogConfigDaoImpl_language_";
    
    @Override
    public SystemConfig getByPriKey(String appId, String id)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("appId", appId);
        map.put("id", id);
        if (isCacheSupported())
        {   
            String key = CACHE_KEY + appId + id;
            SystemConfig systemConfig = (SystemConfig) getCacheClient().getCache(key);
            if (systemConfig != null)
            {
                return systemConfig;
            }
            systemConfig = (SystemConfig) sqlMapClientTemplate.queryForObject("SystemConfig.get", map);
            if (systemConfig == null)
            {
                return null;
            }
            getCacheClient().setCache(key, systemConfig);
            return systemConfig;
        }
        return (SystemConfig) sqlMapClientTemplate.queryForObject("SystemConfig.get", map);
    }
    
    
}
