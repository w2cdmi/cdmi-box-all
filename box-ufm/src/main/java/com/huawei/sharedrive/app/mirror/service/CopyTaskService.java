package com.huawei.sharedrive.app.mirror.service;

import java.util.Date;
import java.util.List;

import com.huawei.sharedrive.app.mirror.domain.CopyTask;
import com.huawei.sharedrive.app.mirror.domain.CopyTaskStatistic;

import pw.cdmi.box.domain.Limit;

public interface CopyTaskService
{
    /**
     * 批量存任务
     * @param lstCopyTask
     */
    void saveCopyTask(List<CopyTask> lstCopyTask );
    
    /**
     * 存单个任务
     * @param copyTask
     */
    void saveSigleCopyTask(CopyTask copyTask );
    
    
    /**
     * 更新任务包含状态等相关属性
     * @param copyTask
     */
    void updateCopyTask(CopyTask copyTask );
    
    
    /**
     * 获取任务出来，并标示任务已经被取
     * @return
     */
    List<CopyTask> getWaittingTaskAndSetPop(Limit limit);
    

    
    /**
     * 触发定期执行任务
     * @param curTime
     */
    void activateTimeTask(String curTime );
    
    /**
     * 去激活定时蜘蛛侠的任务
     * @param curTime
     */
    void deactivatOverdueTimeTask(String curTime );
    
    
    /**
     * 对已经弹出但是未执行的任务，重新置位
     * @param nowTime
     * @param minute
     */
    long recoveryNoExeTaskForPopState(Date outTime);
    
    
    /**
     * 获取任务
     * @param taskId
     * @return
     */
    CopyTask getCopyTask(String taskId);
    
    /**
     * 删除任务
     * @param task
     */
    void deleteCopyTask(CopyTask task);
    
    /**
     * 任务统计
     * @return
     */
    CopyTaskStatistic statisticCopyTask();
    
   
    
    
    /**
     * 检查任务是否重复，源对象是相同、目标资源组相同，任务就相同。
     * @param task
     * @return
     */
   CopyTask checkSameMirrorTask(CopyTask task);
   


  /**
   * 恢复任务执行超时时间，修改状态为waiting ,isPop为 false
   * @param outTime
   * @return
   */
   long recoveryExeTimeOutTask(Date outTime);
   
  /**
   * 
   * @param errCode
   * @return
   */
   long deleteTaskByErrorCode(int errCode);
   
   
   /**
    * 恢复执行失败的任务
    * @return
    */
   long recoveryFailedTask();
   
   
   /**
    * 通过源对象获取任务ID
    * @param srcObjectId
    * @return
    */
   List<CopyTask> getCopyTaskBySrcObjectId(String srcObjectId);
   
   /**
    * 彻底清除某用户的数据迁移的的任务
    * @param userId
    * @return
    */
   long deleteTaskForDataMigration(long userId);
   
   
   /**
    * 数据迁移清楚未执行的异步复制任务
    * @param userId
    * @return
    */
   long clearNotExeTaskForDataMigration(long userId);
   
   
   /**
    * 获取未完成的迁移任务的任务数
    * @param userId
    * @return
    */
   long getNotCompleteTaskForDataMigration(long userId);
   
   
   /**
    * 清理所有该用户的copytask
    * @return
    */
   long cleanMirrorCopyTask(long userId);
   
   /**
    * 将copytask任务中残留的state和sysconfig表中不一致的任务恢复
    * @param state
    * @return
    */
   long updateTaskStateSameWithSystemConfigState(int newState, int oldState);
   
   /**
    * 列举copytask表中所有任务的policyid
    * @return
    */
   List<Integer> selectAllPolicyId();
   
   /**
    * 根据策略删除任务
    * @param policyId
    */
   void deleteCopyTaskByPolicy(Integer policyId);
   
   /**
    * 根据错误码和状态列举任务
    * @param errCode
    * @return
    */
   List<CopyTask> lstTaskByErrorCode(int errCode);
}
