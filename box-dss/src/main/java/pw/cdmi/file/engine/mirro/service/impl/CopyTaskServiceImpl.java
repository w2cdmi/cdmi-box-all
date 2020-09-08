/**
 * 
 */
package pw.cdmi.file.engine.mirro.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pw.cdmi.file.engine.mirro.dao.CopyTaskDAO;
import pw.cdmi.file.engine.mirro.dao.CopyTaskPartDAO;
import pw.cdmi.file.engine.mirro.domain.CopyTask;
import pw.cdmi.file.engine.mirro.domain.CopyTaskRunInfo;
import pw.cdmi.file.engine.mirro.domain.CopyTaskStatus;
import pw.cdmi.file.engine.mirro.service.CopyTaskService;
import pw.cdmi.file.engine.mirro.util.CopyTaskTool;
import pw.cdmi.file.engine.object.domain.FileObject;

/**
 * @author w00186884
 * 
 */
@Service("copyTaskService")
public class CopyTaskServiceImpl implements CopyTaskService
{
    private static final String TOTAL= "total";
    private static final String INPUT = "input";
    
    private static final String RUNNING ="running";
    
    private static final String SUCCESSD ="successd";
    private static final String FAILED = "failed";
    
    private static final String NOTEXIST = "notexist";
    private static final String CALLBACKSUCCESS = "callbacksuccess";
    private static final String CALLBACKFAILED = "callbackfailed";
    private static final String OTHERS = "others";
    @Autowired
    private CopyTaskDAO copyTaskDAO;
    
    @Autowired
    private CopyTaskPartDAO copyTaskPartDAO;
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<CopyTask> queryWaitCopyTasks(String exeAgent)
    {
        return copyTaskDAO.queryWaitCopyTask(exeAgent);
    }
    
    @Override
    public void lockTaskStatus(List<CopyTask> copyTaskListLock)
    {
        copyTaskDAO.lockTaskStatus(copyTaskListLock);
    }
    
    @Override
    public void unLockCopyTask(CopyTask copyTask)
    {
        copyTaskDAO.unLockTaskStatus(copyTask);
    }
    
    @Override
    public void batchInsertOrReplace(List<CopyTask> copyTaskList)
    {
        copyTaskDAO.batchInsertOrReplace(copyTaskList);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateTaskStatus(CopyTask copyTask)
    {
        copyTaskDAO.updateTaskStatus(copyTask);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public List<CopyTask> queryWaitCallbackTasks(String exeAgent)
    {
        return copyTaskDAO.queryWaitCallbackTasks(exeAgent);
    }
    
    @Override
    public void updateMD5ByObjectId(FileObject fileObject)
    {
        CopyTask task = new CopyTask();
        task.setDestObjectId(fileObject.getObjectID());
        task.setMd5(CopyTaskTool.getMD5(fileObject.getSha1()));
        task.setBlockMD5(CopyTaskTool.getBloackMD5(fileObject.getSha1()));
        copyTaskDAO.updateMD5ByObjectId(task);
    }
    
    @Override
    public void pushCallbackUnSuccessTask(String exeAgent)
    {
        copyTaskDAO.pushCallbackUnSuccessTask(exeAgent);
    }
    
    @Override
    public void updateBrotherTaskStatus(String exeAgent)
    {
        copyTaskDAO.updateBrotherTaskStatus(exeAgent);
    }
    
    @Override
    public void resetAllCopyFailedTask(String exeAgent)
    {
        // 失败次数超过50次的重新开始
        copyTaskDAO.resetAllCopyFailedTask(exeAgent);
        
        // 如果因为意外中断导致事物还未来得及提交的状态,只处理running
        copyTaskDAO.restoreCrashTaskStatus(exeAgent);
    }
    
    @Override
    public Map<String, String> getTaskExeInfo()
    {
        // TODO
        Map<String, String> map = new HashMap<String, String>(3);
        List<CopyTask> lstCopyTasks =  copyTaskDAO.listAllCopyTask();
        int total = lstCopyTasks.size();
        int input = 0;
        int running = 0;
        int success = 0;
        int failed =0;
        int notexist = 0;
        int callbacksuccess =0;
        int calbackfailed = 0;
        int others = 0;
        for(CopyTask copyTask : lstCopyTasks)
        {
            if(copyTask.getCopyStatus()==CopyTaskStatus.INPUT.getCode()
                || copyTask.getCopyStatus()==CopyTaskStatus.PAUSE.getCode())
            {
                input++;
            }
            else if(copyTask.getCopyStatus()==CopyTaskStatus.RUNNING.getCode())
            {
                running++;
            }
            else if(copyTask.getCopyStatus()==CopyTaskStatus.SUCCESS.getCode())
            {
                success++;
            }
            else if(copyTask.getCopyStatus()==CopyTaskStatus.FAILED.getCode())
            {
                failed++;
            }
            else if(copyTask.getCopyStatus()==CopyTaskStatus.NOTEXIST.getCode())
            {
                notexist++;
            }
            else if(copyTask.getCopyStatus()==CopyTaskStatus.CALLBACKSUCCESS.getCode())
            {
                callbacksuccess++;
            }
            else if(copyTask.getCopyStatus()==CopyTaskStatus.CALLBACKFAILED.getCode())
            {
                calbackfailed++;
            }
            else
            {
                others++;
            }
        }
        map.put(TOTAL, ""+total);
        map.put(INPUT, input+"");
        map.put(RUNNING, running+"");
        map.put(SUCCESSD, success+"");
        map.put(FAILED, failed+"");
        map.put(NOTEXIST, notexist+"");
        map.put(CALLBACKSUCCESS,callbacksuccess+"");
        map.put(CALLBACKFAILED, calbackfailed+"");
        map.put(OTHERS, others+"");
        return map;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.huawei.sharedrive.dataserver.mirro.service.CopyTaskService#selfRepairTask(java.lang.String)
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void selfRepairTask(String exeAgent)
    {
        // 如果因为意外停止的任务，需要做特殊处理，将状态更改为失败，默认超过4小时且还在running状态的认为是失败的数据
        copyTaskDAO.updateCrashTaskStatus(exeAgent);
        
        // 删除成功的历史数据
        copyTaskDAO.deleteSucessHistoryTask(exeAgent);
        copyTaskPartDAO.deleteUnExistTaskPart();
        
        // 如果兄弟节点挂掉，则将兄弟站点的任务抢过来
        this.updateBrotherTaskStatus(exeAgent);
        
        // 将弹出执行但是失败超过 CopyTaskTool.POP_MAX_MINUTE 分钟的任务推进待执行任务池。
        copyTaskDAO.pushCopyUnSuccessTask(exeAgent);
        
        // 将Callbak失败的数据如果因为一些特殊状态导致则重置
        List<CopyTask> failedTasks = copyTaskDAO.queryCallBack700Task(exeAgent);
        if (CollectionUtils.isNotEmpty(failedTasks))
        {
            for (CopyTask task : failedTasks)
            {
                copyTaskPartDAO.deleteByTaskId(task.getTaskId());
                copyTaskDAO.deleteCallback700Task(task);
            }
        }
    }

    @Override
    public void pauseOrGoTask(int state)
    {
        copyTaskDAO.pauseOrGoTask(state);
    }
    
    @Override
    public List<CopyTask> listExeAgengtIsNullCopyTask()
    {
        return copyTaskDAO.listExeAgengtIsNullCopyTask();
    }
    
    @Override
    public void updateExeAgent(CopyTask copytask)
    {
        copyTaskDAO.updateExeAgent(copytask);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void batchInsertOrReplaceWithExeAgent(List<CopyTask> paramList)
    {
        copyTaskDAO.batchInsertOrReplaceWithExeAgent(paramList);
    }
    
    @Override
    public void setExeAgentForCopyTask(String siteName, int limit)
    {
        copyTaskDAO.setExeAgentForCopyTask(siteName, limit);
    }
    
    @Override
    public List<CopyTaskRunInfo> getTaskRunInfo()
    {
        return copyTaskDAO.getTaskRunInfo();
    }
}
