package com.huawei.sharedrive.app.user.service;

import java.util.List;

import com.huawei.sharedrive.app.user.domain.UserConfig;

public interface UserConfigService
{
    /**
     * 新增用户配置
     * 
     * @param userConfig
     */
    void create(UserConfig userConfig);
    
    /**
     * 查询用户配置项
     * 
     * @param userId
     * @param name
     * @return
     */
    UserConfig get(long userId, String name);
    
    /**
     * 列举用户所有配置项
     * 
     * @param userId
     * @return
     */
    List<UserConfig> list(long userId);
    
    /**
     * 更新用户配置
     * 
     * @param userId
     * @param name
     * @param value
     */
    void update(long userId, String name, String value);
    
}
