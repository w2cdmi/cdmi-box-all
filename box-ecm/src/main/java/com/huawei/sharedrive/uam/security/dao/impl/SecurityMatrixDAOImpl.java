package com.huawei.sharedrive.uam.security.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.security.dao.SecurityMatrixDAO;
import com.huawei.sharedrive.uam.security.domain.SecurityMatrix;
import com.huawei.sharedrive.uam.security.domain.SecurityMatrixQueryCondition;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

@Service("securityMatrixDAO")
@SuppressWarnings("deprecation")
public class SecurityMatrixDAOImpl extends AbstractDAOImpl implements SecurityMatrixDAO
{
    
    @Override
    public void insert(SecurityMatrix securityMatrix)
    {
        sqlMapClientTemplate.insert("SecurityMatrix.insert", securityMatrix);
    }
    
    @Override
    public void delete(SecurityMatrix securityMatrix)
    {
        sqlMapClientTemplate.delete("SecurityMatrix.delete", securityMatrix);
    }
    
    @Override
    public void update(SecurityMatrix securityMatrix, SecurityMatrix oldSecurityMatrix)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("filter", securityMatrix);
        map.put("oldRoleName", oldSecurityMatrix.getRoleName());
        sqlMapClientTemplate.update("SecurityMatrix.update", map);
    }
    
    @SuppressWarnings({"unchecked"})
    @Override
    public List<SecurityMatrix> getAll(SecurityMatrixQueryCondition queryCondition, Order order, Limit limit)
    {
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("filter", queryCondition);
        if (order == null)
        {
            order = new Order("appId, userType, networkType, deviceType, srcResourceType, resTypeCode", false);
        }
        map.put("order", order);
        map.put("limit", limit);
        return (List<SecurityMatrix>) sqlMapClientTemplate.queryForList("SecurityMatrix.getFilterd", map);
    }
    
    @Override
    public Integer getFilterdCount(SecurityMatrixQueryCondition queryCondition)
    {
        Map<String, Object> map = new HashMap<String, Object>(1);
        map.put("filter", queryCondition);
        return (Integer) sqlMapClientTemplate.queryForObject("SecurityMatrix.getFilterdCount", map);
    }
    
    @Override
    public SecurityMatrix getSecurityMatrix(SecurityMatrix securityMatrix)
    {
        Map<String, Object> map = new HashMap<String, Object>(1);
        map.put("filter", securityMatrix);
        return (SecurityMatrix) sqlMapClientTemplate.queryForObject("SecurityMatrix.getSecurityMatrix", map);
    }
    
    @Override
    public Integer isExist(SecurityMatrix securityMatrix)
    {
        Map<String, Object> map = new HashMap<String, Object>(1);
        map.put("filter", securityMatrix);
        return (Integer) sqlMapClientTemplate.queryForObject("SecurityMatrix.isExist", map);
    }
    
    @Override
    public Integer isCiteByPermissionValue(String permissionValue)
    {
        return (Integer) sqlMapClientTemplate.queryForObject("SecurityMatrix.isCiteByPermissionValue",
            permissionValue);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<SecurityMatrix> queryMatrixByNFactor(SecurityMatrix queryObject)
    {
        return (List<SecurityMatrix>) sqlMapClientTemplate.queryForList("SecurityMatrix.queryMatrixByNFactor",
            queryObject);
    }
    
    @Override
    public Integer isCiteByResExtendType(Integer type, Integer code)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("code", code);
        String factor = "";
        if (type == 1)
        {
            factor = "userType";
        }
        else if (type == 2)
        {
            factor = "networkType";
        }
        else if (type == 3)
        {
            factor = "deviceType";
        }
        else if (type == 4)
        {
            factor = "srcResourceType";
        }
        else if (type == 5)
        {
            factor = "resTypeCode";
        }
        else if (type == 6)
        {
            factor = "AppId";
        }
        else
        {
            factor = "0";
        }
        map.put("factor", factor);
        return (Integer) sqlMapClientTemplate.queryForObject("SecurityMatrix.isCiteByResExtendType", map);
    }
    
    @Override
    public boolean hasEnabledSecurityMartix()
    {
        Integer result = (Integer) sqlMapClientTemplate.queryForObject("SecurityMatrix.hasEnabledSecurityMartix");
        return result > 0;
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public List getSecurityMartixRoleNames()
    {
        List result = sqlMapClientTemplate.queryForList("SecurityMatrix.getSecurityMartixRoleNames");
        return result;
    }
}
