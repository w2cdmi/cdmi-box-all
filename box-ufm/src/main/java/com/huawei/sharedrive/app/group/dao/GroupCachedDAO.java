package com.huawei.sharedrive.app.group.dao;

import java.util.List;

import com.huawei.sharedrive.app.openapi.domain.group.MemcachedGroup;

public interface GroupCachedDAO
{
    List<MemcachedGroup> getGroupMemcached(Long userId);
    
    void setGroupMemcached(Long userId, List<MemcachedGroup> memcachedGroupes);
    
    void deleteCached(long userId);
}
