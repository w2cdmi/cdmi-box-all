package com.huawei.sharedrive.app.user.service;

import java.util.List;

import com.huawei.sharedrive.app.user.domain.GroupCache;

public interface UserCacheService
{
    List<GroupCache>getCacheGroupList(long userId);
}
