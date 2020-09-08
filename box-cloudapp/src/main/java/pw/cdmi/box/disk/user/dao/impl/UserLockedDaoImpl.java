package pw.cdmi.box.disk.user.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.disk.user.dao.UserLockedDao;
import pw.cdmi.box.disk.user.domain.UserLocked;

@SuppressWarnings("deprecation")
@Service("userLockedDao")
public class UserLockedDaoImpl extends AbstractDAOImpl implements UserLockedDao
{
    
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
        return (UserLocked) obj;
    }
    
    @Override
    public void insert(UserLocked userLocked)
    {
        sqlMapClientTemplate.insert("UserLocked.insert", userLocked);
    }
    
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
    public UserLocked getByLoginName(String selName)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("loginName", selName);
        Object obj = sqlMapClientTemplate.queryForObject("UserLocked.getByLoginNameForUpdate", map);
        if (null == obj)
        {
            return null;
        }
        return (UserLocked) obj;
    }
    
}
