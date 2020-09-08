package com.huawei.sharedrive.isystem.mirror.dao;

import java.util.Map;

import com.huawei.sharedrive.isystem.mirror.domain.CopyPolicy;

/**
 * 
 * @author c00287749
 * 
 */

public interface CopyTaskDAO
{
    
    /**
     * 统计任务状态,返回的MAP ，KEY 是任務數量，VALUE 是任務的文件總大小
     * 
     * @param state
     * @return
     */
    Map<Long, Long> statisticCopyTask(int state);
    
    Map<Long, Long> statisticCopyTask(int state, CopyPolicy copyPolicy);
    
    /**
     * 
     * @param state
     * @return
     */
    long pauseOrGoTask(int state);
    
    void deleteCopyTaskByPolicy(Integer policyId);
}
