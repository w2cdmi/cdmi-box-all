/**
 * 
 */
package pw.cdmi.file.engine.mirro.task;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pw.cdmi.common.job.JobDefinition;
import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobDefinition;
import pw.cdmi.common.job.quartz.QuartzJobTask;
import pw.cdmi.core.utils.EnvironmentUtils;
import pw.cdmi.file.engine.manage.config.service.SystemConfigManager;
import pw.cdmi.file.engine.mirro.domain.CopyTask;
import pw.cdmi.file.engine.mirro.domain.CopyTaskStatus;
import pw.cdmi.file.engine.mirro.printer.MirrorPrinter;
import pw.cdmi.file.engine.mirro.service.CopyTaskService;
import pw.cdmi.file.engine.mirro.util.CopyTaskTool;

/**
 * @author w00186884
 * 
 */
@Service("copyTaskRunner")
public class CopyTaskRunner extends QuartzJobTask
{
    
    @Autowired
    private CopyTaskDispatcher copyTaskDispatcher;
    
    @Autowired
    private CopyTaskService copyTaskService;
    
    @Autowired
    private SystemConfigManager systemConfigManager;
    
    private static final String EXEAGENT = EnvironmentUtils.getHostName();
    
    private static final JobDefinition JOBDEFINITION = new QuartzJobDefinition();
    
    
    static
    {
        JOBDEFINITION.setJobName("copyTaskRunner");
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void doTask(JobExecuteContext context, JobExecuteRecord record)
    {
        doJob(record);
    }
    
    private void doJob(JobExecuteRecord record)
    {
        // 做点自修复的动作
        copyTaskService.selfRepairTask(EXEAGENT);
        
        if (copyTaskDispatcher.isStart())
        {
            List<CopyTask> copyTaskList = copyTaskService.queryWaitCopyTasks(EXEAGENT);
            List<CopyTask> copyTaskListLock = null;
            int sendTaskNum = 0;
            if (CollectionUtils.isEmpty(copyTaskList))
            {
                MirrorPrinter.info(CopyTaskTool.LOG_TAG + " No copy task need executed");
            }
            else
            {
                boolean result = false;
                for (CopyTask copyTask : copyTaskList)
                {
                    // 根据异地复制总开关判断是否执行任务
                    if (!systemConfigManager.isAllowCopyTaskRun())
                    {
                        break;
                    }
                    
                    if(!systemConfigManager.isAllowCopyTaskRunByTime())
                    {
                        break;
                    }
                    
                    // 根据异地复制总暂停开关判断是否执行任务
                    if (systemConfigManager.isAllowCopyTaskPause())
                    {
                        break;
                    }
                    
                    // 先将任务都加上锁
                    copyTaskListLock = new ArrayList<CopyTask>(1);
                    copyTask.setCopyStatus(CopyTaskStatus.RUNNING.getCode());
                    copyTask.setExeAgent(EXEAGENT);
                    copyTaskListLock.add(copyTask);
                    copyTaskService.lockTaskStatus(copyTaskListLock);
                    sendTaskNum = sendTaskNum + 1;
                    
                    result = copyTaskDispatcher.addCopyTask(copyTask);
                    if (result)
                    {
                        MirrorPrinter.info(CopyTaskTool.LOG_TAG
                            + "copy task running obtain a task and add to task pool,currnet task pool size [{}]"+
                            copyTaskDispatcher.getQueueSize());
                    }
                    else
                    {
                        copyTask.setCopyStatus(CopyTaskStatus.INPUT.getCode());
                        copyTaskService.unLockCopyTask(copyTask);
                        sendTaskNum = sendTaskNum - 1;
                    }
                }
            }
            record.setOutput("The copyTaskDispatcher receive task count [" + sendTaskNum + "]");
            record.setSuccess(true);
        }
        else
        {
            record.setOutput("The copyTaskDispatcher start failed. ");
            record.setSuccess(false);
        }
    }
}
