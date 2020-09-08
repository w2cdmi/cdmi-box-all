package com.huawei.sharedrive.app.plugins.scan.dao;

import java.util.Date;
import java.util.List;

import com.huawei.sharedrive.app.plugins.scan.domain.SecurityScanTask;

/**
 * 安全扫描任务DAO
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2015-4-11
 * @see
 * @since
 */
public interface SecurityScanTaskDAO
{
    /**
     * 创建安全扫描任务
     * 
     * @param securityScanTask
     */
    void create(SecurityScanTask securityScanTask);
    
    /**
     * 删除安全扫描任务
     * 
     * @param taskId
     */
    int delete(String taskId);
    
    /**
     * 根据object id删除安全扫描任务
     * 
     * @param taskId
     */
    int deleteByObjectId(String objectId);
    
    /**
     * 删除某个时间点前创建的扫描任务
     * 
     * @param date
     * @return
     */
    int deleteCreatedBefore(Date date);
    
    /**
     * 根据objectId和dssId查询扫描任务
     * 
     * @param objectId
     * @param dssId
     * @return
     */
    List<SecurityScanTask> getByObjectIdAndDSSId(String objectId, int dssId);
    
    /**
     * 查询当前尚未完成的任务总数
     * 
     * @return
     */
    int getTotalTasks(byte status);
    
    /**
     * 更新任务状态
     * 
     * @param status
     * @param modifiedAt
     * @param taskId
     * @return
     */
    int updateStatus(Byte status, Date modifiedAt, String taskId);
    
    List<Long> getOwnedByByObjectId(String objectId);
}
