package pw.cdmi.box.uam.user.service;

import java.util.List;

public interface AdminAppPermissionService
{
    /**
     * @param adminId
     * @return
     */
    List<String> getAppByAdminId(Long adminId);
    
    /**
     * @param appId
     * @return
     */
    List<Long> getAdminByAppId(String appId);
    
    /**
     * @param adminIds
     * @param appId
     */
    void save(List<Long> adminIds, String appId);
    
    /**
     * @param appId
     * @param adminId
     */
    void delete(String appId, Long adminId);
}
