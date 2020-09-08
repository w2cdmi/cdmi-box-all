package com.huawei.sharedrive.uam.user.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.user.dao.AdminAppPermissionDAO;
import com.huawei.sharedrive.uam.user.domain.AdminAppPermission;
import com.huawei.sharedrive.uam.user.service.AdminAppPermissionService;

@Component
public class AdminAppPermissionServiceImpl implements AdminAppPermissionService
{
    
    @Autowired
    private AdminAppPermissionDAO adminAppPermissionDAO;
    
    @Override
    public List<String> getAppByAdminId(Long adminId)
    {
        return adminAppPermissionDAO.getAppByAdminId(adminId);
    }
    
    @Override
    public List<Long> getAdminByAppId(String appId)
    {
        return adminAppPermissionDAO.getAdminByAppId(appId);
    }
    
    @Override
    public void save(List<Long> adminIds, String appId)
    {
        AdminAppPermission appPermission;
        for (Long id : adminIds)
        {
            appPermission = new AdminAppPermission();
            appPermission.setAdminId(id);
            appPermission.setAppId(appId);
            adminAppPermissionDAO.save(appPermission);
        }
    }
    
    @Override
    public void delete(String appId, Long adminId)
    {
        adminAppPermissionDAO.delete(appId, adminId);
    }
    
}
