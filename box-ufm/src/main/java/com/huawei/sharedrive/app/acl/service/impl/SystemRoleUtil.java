package com.huawei.sharedrive.app.acl.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.acl.dao.ResourceRoleDAO;
import com.huawei.sharedrive.app.acl.domain.ResourceRole;

@Service("systemRoleUtil")
public class SystemRoleUtil
{
    
    @Autowired
    private ResourceRoleDAO resourceRoleDAO;
    
    private volatile Map<String, ResourceRole> cachedRoleMap = null;
    
    private final Object lock = new Object();
    
    /**
     * 判断角色是否存在
     * 
     * @param role
     * @return
     */
    public boolean checkRoleExist(String role)
    {
        if (null == getRole(role))
        {
            return false;
        }
        return true;
    }
    
    public ResourceRole getRole(String role)
    {
        return getCachedRoleMap().get(role);
    }
    
    private Map<String, ResourceRole> getCachedRoleMap()
    {
        if (cachedRoleMap == null)
        {
            synchronized (lock)
            {
                if (cachedRoleMap == null)
                {
                    cachedRoleMap = buildCachedMap();
                }
            }
        }
        return cachedRoleMap;
    }
    
    private Map<String, ResourceRole> buildCachedMap()
    {
        List<ResourceRole> lstRole = resourceRoleDAO.listResourceRole();
        Map<String, ResourceRole> map = new HashMap<String, ResourceRole>((int) (lstRole.size() * 1.5));
        for (ResourceRole role : lstRole)
        {
            map.put(role.getResourceRole(), role);
        }
        return map;
    }
    
}
