package com.huawei.sharedrive.uam.user.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.user.dao.UserLockedDao;
import com.huawei.sharedrive.uam.user.domain.UserLocked;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@SuppressWarnings("deprecation")
@Service("UserLockedDao")
public class UserLockedDaoImpl extends AbstractDAOImpl implements UserLockedDao
{
    @Override
    public void addFailTime(UserLocked userLocked)
    {
        sqlMapClientTemplate.update("UserLocked.updateFailTime", userLocked);
    }
    
    @Override
    public void clearFailTime(UserLocked userLocked)
    {
        if (null != userLocked)
        {
            sqlMapClientTemplate.update("UserLocked.clear", userLocked);
        }
    }
    
    @Override
    public UserLocked get(long userId, String appId)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("userId", userId);
        map.put("appId", appId);
        Object obj = sqlMapClientTemplate.queryForObject("UserLocked.getForUpdate", map);
        if (null == obj)
        {
            return null;
        }
        else
        {
            return (UserLocked) obj;
        }
    }
    
    @Override
    public UserLocked getWithOutLock(long userId, String appId) {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("userId", userId);
        map.put("appId", appId);

        return (UserLocked) sqlMapClientTemplate.queryForObject("UserLocked.getWithoutLock", map);
    }
    
    @Override
    public void insert(UserLocked userLocked)
    {
        sqlMapClientTemplate.insert("UserLocked.insert", userLocked);
    }
    
}
