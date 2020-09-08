package com.huawei.sharedrive.app.group.dao.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.group.dao.GroupCachedDAO;
import com.huawei.sharedrive.app.group.domain.GroupConstants;
import com.huawei.sharedrive.app.openapi.domain.group.MemcachedGroup;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;

@Service
public class GroupCachedDAOImpl extends CacheableSqlMapClientDAO implements GroupCachedDAO
{
    
    @SuppressWarnings("unchecked")
    @Override
    public List<MemcachedGroup> getGroupMemcached(Long userId)
    {
        if (isCacheSupported())
        {
            String key = GroupConstants.CACHE_MEMBER_GROUP + userId;
            List<MemcachedGroup> memcachedGroupes = (List<MemcachedGroup>) getCacheClient().getCache(key);
            if (memcachedGroupes != null)
            {
                return memcachedGroupes;
            }
        }
        return null;
    }
    
    @Override
    public void setGroupMemcached(Long userId, List<MemcachedGroup> memcachedGroupes)
    {
        if (isCacheSupported())
        {
            String key = GroupConstants.CACHE_MEMBER_GROUP + userId;
            getCacheClient().setCache(key, memcachedGroupes);
        }
    }
    
    @Override
    public void deleteCached(long userId)
    {
        if (isCacheSupported())
        {
            String key = GroupConstants.CACHE_MEMBER_GROUP + userId;
            @SuppressWarnings("unchecked")
            List<MemcachedGroup> memcachedGroup = (List<MemcachedGroup>) getCacheClient().getCache(key);
            if (memcachedGroup != null)
            {
                getCacheClient().deleteCache(key);
            }
        }
    }
    
}
