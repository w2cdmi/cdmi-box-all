package pw.cdmi.box.uam.user.dao;

import java.util.List;

import pw.cdmi.box.uam.user.domain.AdminAppPermission;

public interface AdminAppPermissionDAO
{
    
    List<String> getAppByAdminId(Long adminId);
    
    List<Long> getAdminByAppId(String appId);
    
    void save(AdminAppPermission adminAppPermission);
    
    void delete(String appId, Long adminId);
    
    void deleteByAdminId(Long adminId);
    
}