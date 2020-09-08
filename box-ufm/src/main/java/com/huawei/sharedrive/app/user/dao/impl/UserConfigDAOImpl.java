package com.huawei.sharedrive.app.user.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.huawei.sharedrive.app.user.dao.UserConfigDAO;
import com.huawei.sharedrive.app.user.domain.UserConfig;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Repository
@SuppressWarnings("deprecation")
public class UserConfigDAOImpl extends AbstractDAOImpl implements UserConfigDAO
{

    @Override
    public void create(UserConfig userConfig)
    {
        sqlMapClientTemplate.insert("UserConfig.create", userConfig);
    }

    @Override
    public UserConfig get(long userId, String name)
    {
        UserConfig userConfig = new UserConfig();
        userConfig.setUserId(userId);
        userConfig.setName(name);
        return (UserConfig) sqlMapClientTemplate.queryForObject("UserConfig.get", userConfig);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<UserConfig> list(long userId)
    {
        UserConfig userConfig = new UserConfig();
        userConfig.setUserId(userId);
        return sqlMapClientTemplate.queryForList("UserConfig.list", userConfig);
    }

    @Override
    public int update(long userId, String name, String value)
    {
        UserConfig userConfig = new UserConfig(userId, name, value);
        return sqlMapClientTemplate.update("UserConfig.update", userConfig);
    }
    
}
