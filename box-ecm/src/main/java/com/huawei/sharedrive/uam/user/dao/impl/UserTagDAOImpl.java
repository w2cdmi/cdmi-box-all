package com.huawei.sharedrive.uam.user.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.user.dao.UserTagDAO;
import com.huawei.sharedrive.uam.user.domain.UserExtend;
import com.huawei.sharedrive.uam.user.domain.UserTag;
import com.huawei.sharedrive.uam.user.domain.UserTagExtend;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

@Service("userTagDAO")
@SuppressWarnings("deprecation")
public class UserTagDAOImpl extends AbstractDAOImpl implements UserTagDAO
{
    @Override
    public void update(UserTag userTag)
    {
        sqlMapClientTemplate.update("UserTag.update", userTag);
    }
    
    @Override
    public void insert(UserTag userTag)
    {
        sqlMapClientTemplate.insert("UserTag.insert", userTag);
    }
    
    @Override
    public void delete(String tagId)
    {
        sqlMapClientTemplate.delete("UserTag.delete", tagId);
    }
    
    @Override
    public void deleteByUserId(Long userId)
    {
        sqlMapClientTemplate.delete("UserTag.deleteByUserId", userId);
    }
    
    @Override
    public UserTag selectByUserTag(UserTag userTag)
    {
        return (UserTag) sqlMapClientTemplate.queryForObject("UserTag.selectByUserTag", userTag);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<UserTagExtend> selectUserTagByUserId(Long userId)
    {
        List<UserTagExtend> list = sqlMapClientTemplate.queryForList("UserTag.selectUserTagByUserId", userId);
        return list;
    }
    
    @Override
    public int getFilterdCount(UserExtend filter)
    {
        Map<String, Object> map = new HashMap<String, Object>(1);
        map.put("filter", filter);
        return (Integer) sqlMapClientTemplate.queryForObject("UserTag.getUserTagFilterdCount", map);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<UserExtend> getFilterdOrderList(UserExtend filter, Order order, Limit limit)
    {
        Map<String, Object> map = new HashMap<String, Object>(3);
        if (order == null)
        {
            order = new Order();
            order.setDesc(true);
            order.setField("createdAt");
        }
        map.put("filter", filter);
        map.put("limit", limit);
        map.put("order", order);
        return sqlMapClientTemplate.queryForList("UserTag.getUserTagFilterdOrderList", map);
    }
}
