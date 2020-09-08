package com.huawei.sharedrive.app.plugins.scan.service;

import java.util.List;

import com.huawei.sharedrive.app.plugins.scan.domain.SecurityScanTask;
import com.huawei.sharedrive.app.plugins.scan.domain.SecurityStatus;
import com.huawei.sharedrive.app.plugins.scan.domain.SecurityType;

public interface SecurityScanService
{
    /**
     * 创建安全扫描任务
     * 
     * @param task
     */
    void createScanTask(SecurityScanTask task);
    
    /**
     * 删除N天前创建的扫描任务
     * 
     * @param date
     * @return
     */
    int deleteCreatedBefore(int days);
    
    /**
     * 删除安全扫描任务
     * 
     * @param taskId
     * @return
     */
    int deleteScanTask(String taskId);
    
    /**
     * 删除安全扫描任务
     * 
     * @param objectId
     * @return
     */
    int deleteScanTaskByObjectId(String objectId);
    
    /**
     * 获取文件当前的安全状态
     * 
     * @param objectId
     * @param type
     * @return
     */
    SecurityStatus getSecurityStatus(String objectId, SecurityType type);
    
    
    /**
     * 获取文件当前的標記
     * 
     * @param objectId
     * @return
     */
    int getSecurityLabel(String objectId);
    
    /**
     * 获取当前的任务总数
     * 
     * @return
     */
    int getTotalTasks(byte status);
    
    /**
     * 安全扫描特性是否开启
     * 
     * @return
     */
    boolean isSecurityScanEnable();
    
    
    int getSecurityScanMode();
    
    
    /**
     * 判断系统中是否已存在相同文件的扫描任务
     * 
     * @param objectId
     * @param dssId
     * @param priority
     * @return
     */
    boolean isSecurityScanTaskExist(String objectId, int dssId, int priority);
    
    /**
     * 更新对象安全标识
     * 
     * @param securityType
     * @param securityLabel
     * @param objectId
     * @return
     */
    int updateSecurityLabel(SecurityType securityType, int securityLabel, String objectId);
    
    List<Long> getOwnedByObjectId(String objectId);
    
    int updateINodeKIAStatus(long ownedBy, String objectId, int kiaStatus);
}
