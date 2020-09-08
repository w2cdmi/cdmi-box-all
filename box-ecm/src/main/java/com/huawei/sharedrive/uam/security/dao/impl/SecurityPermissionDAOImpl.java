package com.huawei.sharedrive.uam.security.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.security.dao.SecurityPermissionDAO;
import com.huawei.sharedrive.uam.security.domain.SecurityPermission;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

@Service("scurityPermissionDAO")
@SuppressWarnings("deprecation")
public class SecurityPermissionDAOImpl extends AbstractDAOImpl implements SecurityPermissionDAO
{
    
    @Override
    public void insert(SecurityPermission permission)
    {
        sqlMapClientTemplate.insert("SecurityPermission.insert", permission);
    }
    
    @Override
    public void delete(String permissionDesc)
    {
        sqlMapClientTemplate.delete("SecurityPermission.delete", permissionDesc);
    }
    
    @Override
    public void update(SecurityPermission permission, SecurityPermission oldPermission)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("filter", permission);
        map.put("oldPermissionDesc", oldPermission.getPermissionDesc());
        sqlMapClientTemplate.update("SecurityPermission.update", map);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<SecurityPermission> getAll(SecurityPermission permission, Order order, Limit limit)
    {
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("filter", permission);
        map.put("order", order);
        map.put("limit", limit);
        return (List<SecurityPermission>) sqlMapClientTemplate.queryForList("SecurityPermission.getFilterd",
            map);
    }
    
    @Override
    public Integer getFilterdCount(SecurityPermission permission)
    {
        return (Integer) sqlMapClientTemplate.queryForObject("SecurityPermission.getFilterdCount", permission);
    }
    
    @Override
    public Integer isExist(SecurityPermission permission)
    {
        return (Integer) sqlMapClientTemplate.queryForObject("SecurityPermission.isExist", permission);
    }
    
    @Override
    public SecurityPermission getByPermissionDesc(String permissionDesc)
    {
        return (SecurityPermission) sqlMapClientTemplate.queryForObject("SecurityPermission.getByPermissionDesc",
            permissionDesc);
    }
    
    @Override
    public SecurityPermission getByKeyName(String keyName)
    {
        return (SecurityPermission) sqlMapClientTemplate.queryForObject("SecurityPermission.getByKeyName",
            keyName);
    }
}
