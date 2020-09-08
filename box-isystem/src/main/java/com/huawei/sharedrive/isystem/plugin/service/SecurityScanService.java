package com.huawei.sharedrive.isystem.plugin.service;

public interface SecurityScanService
{
    
    /**
     * 获取当前的任务总数
     * 
     * @return
     */
    int getTotalTasks(byte status);
    
    String parseToCron(int exeStartAt, int exeEndAt);
    
    void updateScanJob(String jobCron);
}
