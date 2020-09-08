package com.huawei.sharedrive.app.user.dao;

import java.util.List;

import com.huawei.sharedrive.app.user.domain.UserConfig;

public interface UserConfigDAO
{
    void create(UserConfig userConfig);
    
    UserConfig get(long userId, String name);
    
    List<UserConfig> list(long userId);
    
    int update(long userId, String name, String value);
}
