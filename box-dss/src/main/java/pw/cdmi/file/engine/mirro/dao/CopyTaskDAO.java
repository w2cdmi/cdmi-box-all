package pw.cdmi.file.engine.mirro.dao;

import java.util.List;

import pw.cdmi.file.engine.mirro.domain.CopyTask;
import pw.cdmi.file.engine.mirro.domain.CopyTaskRunInfo;

public interface CopyTaskDAO
{
    List<CopyTask> queryWaitCopyTask(String paramString);
    
    void batchInsertOrReplace(List<CopyTask> paramList);
    
    void updateTaskStatus(CopyTask paramCopyTask);
    
    List<CopyTask> queryWaitCallbackTasks(String paramString);
    
    void lockTaskStatus(List<CopyTask> copyTaskList);
    
    void updateCrashTaskStatus(String exeAgent);
    
    void updateBrotherTaskStatus(String exeAgent);
    
    void updateMD5ByObjectId(CopyTask task);
    
    void deleteSucessHistoryTask(String exeAgent);
    
    void pushCopyUnSuccessTask(String exeAgent);
    
    void resetAllCopyFailedTask(String exeAgent);
    
    void pushCallbackUnSuccessTask(String exeAgent);
    
    void restoreCrashTaskStatus(String exeAgent);
    
    void deleteCallback700Task(CopyTask task);

    List<CopyTask> queryCallBack700Task(String exeAgent);

    void unLockTaskStatus(CopyTask copyTask);
    
    List<CopyTask> listAllCopyTask();
    
    void pauseOrGoTask(int state);
    
    List<CopyTask> listExeAgengtIsNullCopyTask();
    
    void updateExeAgent(CopyTask copytask);
    
    void batchInsertOrReplaceWithExeAgent(List<CopyTask> paramList);
    
    void setExeAgentForCopyTask(String siteName, int limit);
    
    List<CopyTaskRunInfo> getTaskRunInfo();
}
