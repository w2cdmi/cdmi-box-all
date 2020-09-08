package com.huawei.sharedrive.app.group.service;

import java.util.List;

import com.huawei.sharedrive.app.openapi.domain.group.MemcachedGroup;

public interface GroupCachedService
{
    List<MemcachedGroup> setAndGetUserListToCached(Long userId);
    
    void deleteCached(long userId);
}
