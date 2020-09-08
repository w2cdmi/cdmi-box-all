package com.huawei.sharedrive.app.mirror.manager.statistic;

import com.huawei.sharedrive.app.common.systemtask.domain.SystemTask;
import com.huawei.sharedrive.app.mirror.domain.CopyPolicy;

public interface CopyPolicyStatistics
{
    /**
     * 为策略创建一个统计任务
     * @param policy
     */
    void createSystemTaskForCopyPolicy(CopyPolicy policy);
    
    /**
     * 删除策略相关的统计任务
     * @param policy
     */
    void deleteSystemTaskForCopyPolicy(int policyId);
    
    /**
     * 是否允许统计
     * @param policy
     * @return
     */
    boolean isAllowStatistic(CopyPolicy policy);
    
    /**
     * 更新统计任务的User
     * @param task
     */
    SystemTask setNextStatisticUser(SystemTask task);
    /**
     * 获取一个统计任务
     * @param pTaskId
     * @return
     */
    SystemTask getOneWaitingStatisticTask(String pTaskId);
    
    /**
     * 是否开启统计特性
     * @return
     */
    boolean isMirrorPolicyStatisticEnable();
    
    
    /**
     * 清理超时任务
     */
    void clearTimeOutSystemTaskForCopyPolicy();
    
    /**
     * 更新系统执行时间
     */
   void updateStatisticTaskExeTime(SystemTask task);
    
}
