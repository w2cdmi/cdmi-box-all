/**
 *
 */
package com.huawei.sharedrive.app.system.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.huawei.sharedrive.app.system.dao.SystemConfigDAO;
import com.huawei.sharedrive.app.utils.Constants;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.common.domain.SystemConfig;

/**
 * @author q90003805
 *
 */
@Service("systemConfigDAO")
@SuppressWarnings("deprecation")
public class SystemConfigDAOImpl extends AbstractDAOImpl implements SystemConfigDAO
{

    private static Logger logger = LoggerFactory.getLogger(SystemConfigDAOImpl.class);

    private LoadingCache<String, SystemConfig> localCache;

    public SystemConfigDAOImpl()
    {
        localCache = CacheBuilder.newBuilder()
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .build(new CacheLoader<String, SystemConfig>()
                {
                    @Override
                    public SystemConfig load(String id)
                    {
                        Map<String, Object> map = new HashMap<String, Object>(3);
                        map.put("id", id);
                        map.put("appId", Constants.UFM_DEFAULT_APP_ID);
                        return (SystemConfig) sqlMapClientTemplate.queryForObject("SystemConfig.get", map);
                    }
                });
    }

    @Override
    public void create(SystemConfig systemConfig)
    {
        systemConfig.setAppId(Constants.UFM_DEFAULT_APP_ID);
        sqlMapClientTemplate.insert("SystemConfig.insert", systemConfig);
        localCache.put(systemConfig.getId(), systemConfig);
    }

    @Override
    public void delete(String id)
    {
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("id", id);
        map.put("appId", Constants.UFM_DEFAULT_APP_ID);
        sqlMapClientTemplate.delete("SystemConfig.delete", map);
        localCache.invalidate(id);
    }

    @Override
    public SystemConfig get(String id)
    {
        try
        {
            SystemConfig sysconfig = localCache.get(id);
            logger.info(sysconfig.getId() + "" + sysconfig.getValue());
            return sysconfig;
        }
        catch (RuntimeException e)
        {
            logger.warn("Fail in get cache");
            return null;
        }
        catch (Exception e)
        {
            logger.warn("Fail in get cache");
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SystemConfig> getByPrefix(Limit limit, String prefix)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("prefix", prefix);
        map.put("appId", Constants.UFM_DEFAULT_APP_ID);
        map.put("limit", limit);
        List<SystemConfig> list = sqlMapClientTemplate.queryForList("SystemConfig.getByPrefix", map);
        for (SystemConfig systemConfig : list)
        {
            updateCache(systemConfig);
        }
        return list;
    }

    @Override
    public int getByPrefixCount(String prefix)
    {
        Map<String, Object> map = new HashMap<String, Object>(1);
        map.put("prefix", prefix);
        map.put("appId", Constants.UFM_DEFAULT_APP_ID);
        return (Integer) sqlMapClientTemplate.queryForObject("SystemConfig.getByPrefixCount", map);
    }

    @Override
    public void invalidateCache(Object key)
    {
        localCache.invalidate(key);
    }

    @Override
    public void update(SystemConfig systemConfig)
    {
        sqlMapClientTemplate.update("SystemConfig.update", systemConfig);
        localCache.put(systemConfig.getId(), systemConfig);
    }

    private void updateCache(SystemConfig systemConfig)
    {
        localCache.put(systemConfig.getId(), systemConfig);
    }

}
