package com.huawei.sharedrive.isystem.user.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.isystem.user.dao.UserDAO;
import com.huawei.sharedrive.isystem.user.domain.User;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

@Service("UserDAO")
@SuppressWarnings("deprecation")
public class UserDAOImpl extends AbstractDAOImpl implements UserDAO
{
    
    @Override
    public User get(Long id)
    {
        return (User) sqlMapClientTemplate.queryForObject("User.get", id);
    }
    
    @Override
    public User getUserByLoginName(String loginName)
    {
        return (User) sqlMapClientTemplate.queryForObject("User.getUserByLoginName", loginName);
    }
    
    @Override
    public User getUserByObjectSid(String objectSid)
    {
        return (User) sqlMapClientTemplate.queryForObject("User.getUserByObjectSid", objectSid);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<User> getFilterd(User filter, Order order, Limit limit)
    {
        if(order == null)
        {
            order = new Order();
            order.setDesc(true);
            order.setField("createdAt");
        }
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("filter", filter);
        map.put("order", order);
        map.put("limit", limit);
        return sqlMapClientTemplate.queryForList("User.getFilterd", map);
    }
    
    @Override
    public int getFilterdCount(User filter)
    {
        Map<String, Object> map = new HashMap<String, Object>(1);
        map.put("filter", filter);
        return (Integer) sqlMapClientTemplate.queryForObject("User.getFilterdCount", map);
    }
    
    @Override
    public void delete(Long id)
    {
        sqlMapClientTemplate.delete("User.delete", id);
    }
    
    @Override
    public void create(User user)
    {
        sqlMapClientTemplate.insert("User.insert", user);
    }
    
    @Override
    public void update(User user)
    {
        sqlMapClientTemplate.update("User.update", user);
    }
    
    @Override
    public long getNextAvailableUserId()
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("param", "userId");
        sqlMapClientTemplate.queryForObject("getNextId", map);
        long id = (Long) map.get("returnid");
        return id;
    }
    
    @Override
    public void updateStatus(long id, String status)
    {
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        user.setModifiedAt(new Date());
        sqlMapClientTemplate.update("User.updateStatus", user);
    }
    
    @Override
    public void updateRegionID(long id, int regionID)
    {
        User user = new User();
        user.setId(id);
        user.setRegionId(regionID);
        user.setModifiedAt(new Date());
        sqlMapClientTemplate.update("User.updateRegion", user);
    }
    
    @Override
    public void sacleUser(long id, long spaceQuota)
    {
        User user = new User();
        user.setId(id);
        user.setSpaceQuota(spaceQuota);
        user.setModifiedAt(new Date());
        sqlMapClientTemplate.update("User.sacle", user);
    }
    
    @Override
    public void updatePassword(long id, String newPsw)
    {
        User user = new User();
        user.setId(id);
        user.setPassword(newPsw);
        user.setModifiedAt(new Date());
        sqlMapClientTemplate.update("User.updatePassword", user);
    }
}
