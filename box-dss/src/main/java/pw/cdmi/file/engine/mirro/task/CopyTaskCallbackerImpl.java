/**
 * 
 */
package pw.cdmi.file.engine.mirro.task;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;
import pw.cdmi.core.utils.EnvironmentUtils;
import pw.cdmi.file.engine.manage.config.service.SystemConfigManager;
import pw.cdmi.file.engine.mirro.domain.CopyTask;
import pw.cdmi.file.engine.mirro.printer.MirrorPrinter;
import pw.cdmi.file.engine.mirro.service.CopyTaskService;

/**
 * 
 * 
 * @author w00186884
 * 
 */
@Service("copyTaskCallbacker")
public class CopyTaskCallbackerImpl extends QuartzJobTask
{
    
    @Autowired
    private CopyTaskService copyTaskService;
    
    @Autowired
    private ThreadPoolTaskExecutor copyTaskCallBackerExecutor;
    
    @Autowired
    private ThreadPoolTaskExecutor reCalcObjectMD5Executor;
    
    @Autowired
    private SystemConfigManager systemConfigManager;
    
    private static String exeAgent = EnvironmentUtils.getHostName();
    
    @Override
    public void doTask(JobExecuteContext context, JobExecuteRecord record)
    {
        // 灏嗗脊鍑烘墽琛屼絾鏄け璐ヨ秴杩�CopyTaskTool.POP_MAX_MINUTE 鍒嗛挓鐨勪换鍔℃帹杩涘緟鍥炶皟浠诲姟姹犮�
        copyTaskService.pushCallbackUnSuccessTask(exeAgent);
        
        List<CopyTask> taskLists = copyTaskService.queryWaitCallbackTasks(exeAgent);
        
        if (CollectionUtils.isEmpty(taskLists))
        {
            MirrorPrinter.info("CopyTaskCallbacker has no task executable,don't need report");
            record.setOutput("no task need report");
            return;
        }
        
        MirrorPrinter.info("CopyTaskCallbacker has " + taskLists.size() + " task to execute");
        CopyTaskCallbackerWorker callbackerWorker = null;
        ReCalcObjectMD5FWorkerForMirror reCalcObjectMD5FWorkerForMirror = null;
        for (CopyTask copyTask : taskLists)
        {
            // 根据异地复制总开关判断是否执行任务
            if (!systemConfigManager.isAllowCopyTaskRun())
            {
                break;
            }
            
            if (!systemConfigManager.isAllowCopyTaskRunByTime())
            {
                break;
            }
            
            // 根据异地复制总暂停开关判断是否执行任务
            if (systemConfigManager.isAllowCopyTaskPause())
            {
                break;
            }
            
            if (copyTask.getMd5() == null || null == copyTask.getBlockMD5())
            {
                MirrorPrinter.info("The task " + copyTask.getTaskId() + ",object "
                    + copyTask.getDestObjectId() + " not md5 or block md5,don't need report");
                reCalcObjectMD5FWorkerForMirror = new ReCalcObjectMD5FWorkerForMirror(copyTask);
                reCalcObjectMD5Executor.execute(reCalcObjectMD5FWorkerForMirror);
                continue;
            }
            
            MirrorPrinter.info("The task " + copyTask.getTaskId() + ",object " + copyTask.getDestObjectId()
                + "start report ufm");
            copyTask.setCopyStatus(copyTask.getCopyStatus() + 100);
            // 为减少回调产生的死锁。此处更新无必要要，删除
            // copyTaskService.updateTaskStatus(copyTask);
            
            callbackerWorker = new CopyTaskCallbackerWorker(copyTask);
            copyTaskCallBackerExecutor.execute(callbackerWorker);
        }
        
    }
    
}
