package com.huawei.sharedrive.app.user.dao;

import java.util.List;

import com.huawei.sharedrive.app.user.domain.GroupCache;


public interface UserCacheDao
{
    List<GroupCache> getCacheGroup(long userId);
}
