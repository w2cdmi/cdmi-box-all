package com.huawei.sharedrive.app.mirror.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.huawei.sharedrive.app.mirror.domain.CopyTask;

import pw.cdmi.box.dao.BaseDAO;
import pw.cdmi.box.domain.Limit;

/**
 * 
 * @author w00355328
 *
 */
public interface CopyTaskSlaveDBDAO extends BaseDAO<CopyTask, String>
{
    /**
     * 获取某种类型下的某种状态的任务
     * @param exeType
     * @param state
     * @param limit
     * @return 
     */
    List<CopyTask>  getBystatusAndExeType(int exeType,int state,boolean isPop,Limit limit);
    
    
    
    /**
     * 更新任务POP状态
     * @param lstTask
     * @param isPop
     */
    void updateForPop(List<CopyTask> lstTask,boolean isPop);
    
    /**
     * 主要为了重用一个sqlconnect连接，提高效率
     * @param lstTask
     * @param isPop
     * 
     */
    void updateForPopForList(List<CopyTask> lstTask, boolean isPop);
    
    /**
     * 批量插入
     * @param lstTask
     */
    void createCopyTask(List<CopyTask>  lstTask);
    
    
    //触发等待激活的定时任务
    void activateTimeTask(String curTime);
    

    /**
     * 恢复已经激活但是超期未执行的任务,即state:waiting ,pop:false,exeType:timetype 
     * @param curTime
     */
    void deactivatOverdueTimeTask(String curTime);
    
    
    /**
     * 恢复没有执行的任务
     * @param outTime
     * @return
     */
    long recoveryNoExeTaskForPopState(Date outTime);
    
    /**
     * 统计任务状态,返回的MAP ，KEY 是任務數量，VALUE 是任務的文件總大小
     * @param state
     * @return
     */
    Map<Long, Long> statisticCopyTask(int state);
    
    /**
     * 恢复执行超时的任务，使任务为执行状态。
     * @param outTime
     * @return
     */
    long recoveryExeTimeOutTask(Date outTime);
    
    /**
     * 复制类型
     * @param task
     * @return
     */
     CopyTask  getTaskBySrcObjectAndResourceGroupID(CopyTask task);
    
    /**
     * 更新状态
     * exeType
     * @return
     */
    long recoveryFailedTaskForByExeType(int exeType);
    
    /**
     * 根据状态和错误码删除
     * @param errCode
     * @return
     */
    long deleteTaskByErrorCode(int errCode);
    
    /**
     * 根据错误码和状态列举任务
     * @param errCode
     * @return
     */
    List<CopyTask> lstTaskByErrorCode(int errCode);
    
    /**
     * 获取对象列表
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
     * 获取未完成的迁移任务的任务数
     * @param userId
     * @return
     */
    long getNotCompleteTaskForDataMigration(long userId);
    
    /**
     * 数据迁移清楚未执行的异步复制任务
     * @param userId
     * @return
     */
    long clearNotExeTaskForDataMigration(long userId);
    
    /**
     * 清理所有任务
     * @return
     */
    long cleanMirrorCopyTaskBySrcOwnedby(long userId);
    
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
}
