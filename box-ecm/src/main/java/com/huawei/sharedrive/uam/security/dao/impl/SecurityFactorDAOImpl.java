package com.huawei.sharedrive.uam.security.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.security.dao.SecurityFactorDAO;
import com.huawei.sharedrive.uam.security.domain.SecurityFactor;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

@Service("securityFactorDAO")
@SuppressWarnings("deprecation")
public class SecurityFactorDAOImpl extends AbstractDAOImpl implements SecurityFactorDAO
{
    
    @Override
    public void insert(SecurityFactor securityFactor)
    {
        sqlMapClientTemplate.insert("SecurityFactor.insert", securityFactor);
    }
    
    @Override
    public void delete(Integer type, Integer code)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("type", type);
        map.put("code", code);
        sqlMapClientTemplate.delete("SecurityFactor.delete", map);
    }
    
    @Override
    public void update(SecurityFactor securityFactor, SecurityFactor oldSecurityFactor)
    {
        Map<String, Object> map = new HashMap<String, Object>(6);
        map.put("filter", securityFactor);
        map.put("oldType", oldSecurityFactor.getType().getType());
        map.put("oldCode", oldSecurityFactor.getCode());
        map.put("oldName", oldSecurityFactor.getName());
        map.put("oldDescription", oldSecurityFactor.getDescription());
        map.put("oldIsSystem", oldSecurityFactor.getIsSystem());
        sqlMapClientTemplate.update("SecurityFactor.update", map);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<SecurityFactor> getAll(SecurityFactor securityFactor, Order order, Limit limit)
    {
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("filter", securityFactor);
        map.put("order", order);
        map.put("limit", limit);
        return (List<SecurityFactor>) sqlMapClientTemplate.queryForList("SecurityFactor.getFilterd", map);
    }
    
    @Override
    public Integer getFilterdCount(SecurityFactor securityFactor)
    {
        Map<String, Object> map = new HashMap<String, Object>(1);
        map.put("filter", securityFactor);
        return (Integer) sqlMapClientTemplate.queryForObject("SecurityFactor.getFilterdCount", map);
    }
    
    @Override
    public Integer isExist(SecurityFactor securityFactor)
    {
        Map<String, Object> map = new HashMap<String, Object>(1);
        map.put("filter", securityFactor);
        return (Integer) sqlMapClientTemplate.queryForObject("SecurityFactor.isExist", map);
    }
    
    @Override
    public SecurityFactor getSecurityFactorByCode(Integer type, Integer code)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("type", type);
        map.put("code", code);
        return (SecurityFactor) sqlMapClientTemplate.queryForObject("SecurityFactor.getSecurityFactorByCode",
            map);
    }
    
    @Override
    public SecurityFactor getSecurityFactorByName(Integer type, String name)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("type", type);
        map.put("name", name);
        return (SecurityFactor) sqlMapClientTemplate.queryForObject("SecurityFactor.getSecurityFactorByName",
            map);
    }
    
}
