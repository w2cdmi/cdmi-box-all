package com.huawei.sharedrive.app.user.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.user.dao.UserConfigDAO;
import com.huawei.sharedrive.app.user.domain.UserConfig;
import com.huawei.sharedrive.app.user.service.UserConfigService;

@Service
public class UserConfigServiceImpl implements UserConfigService
{
    @Autowired
    private UserConfigDAO userConfigDAO;
    
    @Override
    public void create(UserConfig userConfig)
    {
        userConfigDAO.create(userConfig);
    }
    
    @Override
    public UserConfig get(long userId, String name)
    {
        return userConfigDAO.get(userId, name);
    }
    
    @Override
    public List<UserConfig> list(long userId)
    {
        return userConfigDAO.list(userId);
    }
    
    @Override
    public void update(long userId, String name, String value)
    {
        userConfigDAO.update(userId, name, value);
    }
    
}
