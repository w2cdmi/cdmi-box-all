package com.huawei.sharedrive.isystem.plugin.dao;

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
     * 查询指定状态任务总数
     * 
     * @return
     */
    int getTotalTasks(byte status);
    
}
