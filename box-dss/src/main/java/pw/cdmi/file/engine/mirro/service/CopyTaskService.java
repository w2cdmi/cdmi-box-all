/**
 * 
 */
package pw.cdmi.file.engine.mirro.service;

import java.util.List;
import java.util.Map;

import pw.cdmi.file.engine.mirro.domain.CopyTask;
import pw.cdmi.file.engine.mirro.domain.CopyTaskRunInfo;
import pw.cdmi.file.engine.object.domain.FileObject;

/**
 * @author w00186884
 * 
 */
public interface CopyTaskService
{
    
    /**
     * 查询当前节点的待执行任务,并更新为运行状态
     * 
     * @param exeAgent
     * @return
     */
    List<CopyTask> queryWaitCopyTasks(String exeAgent);
    
    /**
     * 批量插入或者替换
     * 
     * @param copyTaskList
     */
    void batchInsertOrReplace(List<CopyTask> copyTaskList);
    
    /**
     * 更新任务状态
     * 
     * @param copyTask
     */
    void updateTaskStatus(CopyTask copyTask);
    
    /**
     * 待回调的任务
     * 
     * @param exeAgent
     * @return
     */
    List<CopyTask> queryWaitCallbackTasks(String exeAgent);
    
    /**
     * 返回当前任务执行的统计信息
     * 
     * @return
     */
    Map<String, String> getTaskExeInfo();
    
    /**
     * 分片任务在回调时填写Md5值
     * 
     * @param fileObject
     */
    void updateMD5ByObjectId(FileObject fileObject);
    
    /**
     * 重置所有失败任务
     * 
     * @param exeAgent
     */
    void resetAllCopyFailedTask(String exeAgent);
    
    void pushCallbackUnSuccessTask(String exeAgent);
    
    void selfRepairTask(String exeAgent);
    
    void updateBrotherTaskStatus(String exeAgent);
    
    void lockTaskStatus(List<CopyTask> copyTaskListLock);
    
    void unLockCopyTask(CopyTask copyTask);
    
    /**
     * 
     * @param state
     */
    void pauseOrGoTask(int state);
    
    /**
     * 查询exeAgent为空的任务
     * 
     * @return
     */
    List<CopyTask> listExeAgengtIsNullCopyTask();
    
    void updateExeAgent(CopyTask copytask);
    
    void batchInsertOrReplaceWithExeAgent(List<CopyTask> paramList);
    
    void setExeAgentForCopyTask(String siteName, int limit);
    
    List<CopyTaskRunInfo> getTaskRunInfo();
}
