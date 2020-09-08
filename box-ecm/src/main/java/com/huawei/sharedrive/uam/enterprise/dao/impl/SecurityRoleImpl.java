package com.huawei.sharedrive.uam.enterprise.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.enterprise.dao.SecurityRoleDao;
import com.huawei.sharedrive.uam.enterprise.domain.SecurityRole;
import com.huawei.sharedrive.uam.enterprise.service.SecurityRoleGenerateService;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

@SuppressWarnings({"unchecked", "deprecation"})
@Component
public class SecurityRoleImpl extends CacheableSqlMapClientDAO implements SecurityRoleDao
{
    @Autowired
    private SecurityRoleGenerateService securityRoleGenerateService;
    
    @Override
    public long create(SecurityRole securityRole)
    {
        long id = securityRoleGenerateService.getNextId();
        securityRole.setId(id);
        sqlMapClientTemplate.insert("SecurityRole.insert", securityRole);
        return id;
    }
    
    @Override
    public boolean isDuplicateValues(SecurityRole securityRole)
    {
        int count = (int) sqlMapClientTemplate.queryForObject("SecurityRole.getDuplicateValues", securityRole);
        if (count > 0)
        {
            return true;
        }
        return false;
    }
    
    @Override
    public int getFilterdCount(SecurityRole filter)
    {
        Map<String, Object> map = new HashMap<String, Object>(1);
        map.put("filter", filter);
        return (Integer) sqlMapClientTemplate.queryForObject("SecurityRole.getFilterdCount", map);
    }
    
    @Override
    public List<SecurityRole> getFilterd(SecurityRole filter, Order order, Limit limit)
    {
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("filter", filter);
        map.put("order", order);
        map.put("limit", limit);
        return sqlMapClientTemplate.queryForList("SecurityRole.getFilterd", map);
    }
    
    @Override
    public SecurityRole getById(long id)
    {
        if (isCacheSupported())
        {
            String key = SecurityRole.CACHE_KEY_PREFIX_ID + id;
            SecurityRole securityRole = (SecurityRole) getCacheClient().getCache(key);
            if (securityRole != null)
            {
                return securityRole;
            }
            securityRole = (SecurityRole) sqlMapClientTemplate.queryForObject("SecurityRole.getById", id);
            if (securityRole == null)
            {
                return null;
            }
            getCacheClient().setCache(key, securityRole);
            return securityRole;
        }
        
        return (SecurityRole) sqlMapClientTemplate.queryForObject("SecurityRole.getById", id);
    }
    
    @Override
    public void updateEnterpriseInfo(SecurityRole securityRole)
    {
        sqlMapClientTemplate.update("SecurityRole.modify", securityRole);
        String key = SecurityRole.CACHE_KEY_PREFIX_ID + securityRole.getId();
        deleteCacheAfterCommit(key);
    }
    
    @Override
    public long getByDomainExclusiveId(SecurityRole securityRole)
    {
        long count = (long) sqlMapClientTemplate.queryForObject("SecurityRole.getByDomainExclusiveId",
            securityRole);
        return count;
    }
    
    @Override
    public long getMaxId()
    {
        Object maxUserIdObject = sqlMapClientTemplate.queryForObject("SecurityRole.getMaxId", null);
        if (maxUserIdObject == null)
        {
            return 0L;
        }
        else
        {
            return (long) maxUserIdObject;
        }
    }
    
    private static Logger logger = LoggerFactory.getLogger(SecurityRoleImpl.class);
    
    @Override
    public void delete(SecurityRole securityRole)
    {
        sqlMapClientTemplate.delete("SecurityRole.delete", securityRole);
        try
        {
            if (isCacheSupported())
            {
                String key = SecurityRole.CACHE_KEY_PREFIX_ID + securityRole.getId().longValue();
                getCacheClient().deleteCache(key);
            }
        }
        catch (Exception e)
        {
            logger.warn("Error when delete cache.", e);
        }
    }
}
