package com.huawei.sharedrive.uam.security.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.security.dao.UserSpecialDAO;
import com.huawei.sharedrive.uam.security.domain.UserSpecial;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

@Service("userSpecialDAO")
@SuppressWarnings("deprecation")
public class UserSpecialDAOImpl extends AbstractDAOImpl implements UserSpecialDAO
{
    
    @Override
    public void insert(UserSpecial userSpecial)
    {
        sqlMapClientTemplate.insert("UserSpecial.insert", userSpecial);
    }
    
    @Override
    public void delete(UserSpecial userSpecial)
    {
        sqlMapClientTemplate.delete("UserSpecial.delete", userSpecial);
    }
    
    @Override
    public void update(UserSpecial userSpecial, UserSpecial oldUserSpecial)
    {
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("filter", userSpecial);
        map.put("oldUserName", oldUserSpecial.getUserName());
        map.put("oldSpecialType", oldUserSpecial.getSpecialType().getType());
        sqlMapClientTemplate.update("UserSpecial.update", map);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<UserSpecial> getAll(UserSpecial userSpecial, Order order, Limit limit)
    {
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("filter", userSpecial);
        map.put("order", order);
        map.put("limit", limit);
        return (List<UserSpecial>) sqlMapClientTemplate.queryForList("UserSpecial.getFilterd", map);
    }
    
    @Override
    public Integer getFilterdCount(UserSpecial userSpecial)
    {
        return (Integer) sqlMapClientTemplate.queryForObject("UserSpecial.getFilterdCount", userSpecial);
    }
    
    @Override
    public Integer isExist(UserSpecial userSpecial)
    {
        return (Integer) sqlMapClientTemplate.queryForObject("UserSpecial.isExist", userSpecial);
    }
    
    @Override
    public UserSpecial getUserSpecial(UserSpecial userSpecial)
    {
        return (UserSpecial) sqlMapClientTemplate.queryForObject("UserSpecial.getUserSpecial", userSpecial);
    }
    
    @Override
    public UserSpecial getUserByNameAndType(String userName, Integer specialType)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("userName", userName);
        map.put("specialType", specialType);
        return (UserSpecial) sqlMapClientTemplate.queryForObject("UserSpecial.getUserSpecialByNameAndType",
            map);
    }
}
