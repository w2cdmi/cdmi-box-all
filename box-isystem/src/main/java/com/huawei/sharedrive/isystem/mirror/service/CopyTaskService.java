package com.huawei.sharedrive.isystem.mirror.service;

import com.huawei.sharedrive.isystem.mirror.domain.CopyPolicy;

public interface CopyTaskService
{
    
    /**
     * 统计当前任务状态
     * 
     * @return
     */
    CopyTaskStatistic statisticCurrentTaskInfo();

    CopyTaskStatistic statisticCurrentTaskInfo(CopyPolicy copyPolicy);

    long pauseOrGoTask(int state);
    
}
