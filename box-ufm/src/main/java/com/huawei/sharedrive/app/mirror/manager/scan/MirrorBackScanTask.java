package com.huawei.sharedrive.app.mirror.manager.scan;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.common.systemtask.domain.ScanTableInfo;
import com.huawei.sharedrive.app.common.systemtask.domain.SystemTask;
import com.huawei.sharedrive.app.common.systemtask.domain.TaskExecuteInfo;
import com.huawei.sharedrive.app.common.systemtask.domain.TaskKeyConstant;
import com.huawei.sharedrive.app.common.systemtask.service.SystemTaskService;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.manager.FilesInnerManager;
import com.huawei.sharedrive.app.mirror.domain.CopyTask;
import com.huawei.sharedrive.app.mirror.domain.JobParament;
import com.huawei.sharedrive.app.mirror.domain.TableScanBreakInfo;
import com.huawei.sharedrive.app.mirror.manager.CopyConfigLocalCache;
import com.huawei.sharedrive.app.mirror.manager.CopyPolicyHandle;
import com.huawei.sharedrive.app.mirror.manager.CopyTaskManager;
import com.huawei.sharedrive.app.mirror.service.CopyTaskService;
import com.huawei.sharedrive.app.mirror.service.TableScanBreakInfoService;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.daemon.DaemonJobTask;
import pw.cdmi.core.utils.EnvironmentUtils;
import pw.cdmi.core.utils.JsonUtils;
import pw.cdmi.core.utils.MethodLogAble;

/**
 * 后台扫描任务
 * 
 * @author c00287749
 * 
 */
@Service("mirrorBackScanTask")
public class MirrorBackScanTask extends DaemonJobTask<Object>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MirrorBackScanTask.class);
    
    @Autowired
    private CopyTaskManager copyTaskManager;
    
    @Autowired
    private CopyConfigLocalCache copyConfigLocalCache;
    
    @Autowired
    private SystemTaskService systemTaskService;
    
    @Autowired
    private FilesInnerManager filesInnerManager;
    
    @Autowired
    private CopyPolicyHandle copyPolicyHandle;
    
    @Autowired
    private CopyTaskService copyTaskService;
    
    @Autowired
    private TableScanBreakInfoService tableScanBreakInfoService;
    
    private static final String EXEAGENT = EnvironmentUtils.getHostName();
    
    // 每次列举1万次
    private final static int LENGTH_MAX = 10000;
    
    // 休眠五分钟
    private final static long SLEEP_TIME = 1000 * 60 * 5;
    
    /**
     * 获取JOB 名字
     * 
     * @return
     */
    public static String getJobName()
    {
        return "mirrorBackScanTask";
    }
    
    @Override
    public boolean available(Object object)
    {
        if (null == object)
        {
            return false;
        }
        LOGGER.info("get null here ");
        return true;
    }
    
    /**
     * 执行具体的任务
     */
    @MethodLogAble
    @Override
    public void doTask(JobExecuteContext context, JobExecuteRecord record, Object object)
    {
        
        SystemTask task = (SystemTask) object;
        ScanTableInfo scanTableInfo = ScanTableInfo.toObject(task.getTaskInfo());
        Limit limit = new Limit();
        limit.setLength(LENGTH_MAX);
        limit.setOffset(0L);
        limit.checkInnerParameter();
        long dealNumber = 0L;
        
        TableScanBreakInfo breakInfo = checkScanTaskBreak(task.getTaskId());
        
        if(null != breakInfo)
        {
            limit.setOffset(breakInfo.getLimitOffset());
            tableScanBreakInfoService.delete(task.getTaskId());
            LOGGER.info("find scan breakpoint,scan continue from " +breakInfo.getLimitOffset()+ " to end");
        }
        
        LOGGER.info("scan table begin task id is:" + task.getTaskId() + " ,userdb :"
            + scanTableInfo.getDbNumber() + ",table:" + scanTableInfo.getTableNumber() + ",limit offset:"
            + limit.getOffset() + ",limit length:" + limit.getLength());
        try
        {
            
            List<INode> lstDeleteNode = null;
            Date date = null;
            
            for (;;)
            {
                // 异地复制总开关控制表的扫描,若总开关关闭，退去当次扫描，将当前扫描表任务恢复，等待下一次执行
                if (!copyConfigLocalCache.isSystemMirrorEnable())
                {
                    String agent = task.getExeAgent();
                    LOGGER.info("mirror_global_enable is false,scan table is stop");
                    task.setExeAgent(null);
                    task.setState(SystemTask.TASK_STATE_BEGIN);
                    task.setExeUpdateTime(new Date());
                    systemTaskService.updateTask(task);
                    
                    String msg = "mirror scan back:" + scanTableInfo.toStr() + ",beacuse switch settting";
                    LOGGER.info(msg);
                    record.setOutput(msg);
                    record.setSuccess(true);
                    record.setExecuteMachine(agent);
                    
                    TableScanBreakInfo scanBreakInfo = new TableScanBreakInfo();
                    scanBreakInfo.setSysTaskId(task.getTaskId());
                    scanBreakInfo.setLimitOffset(limit.getOffset());
                    scanBreakInfo.setBreakTime(new Date());
                    scanBreakInfo.setLength(LENGTH_MAX);
                    scanBreakInfo.setModel("ufm");
                    scanBreakInfo.setOutPut(msg);
                    
                    insertBreakPoint(scanBreakInfo);
                    return;
                }
                
                if (!copyConfigLocalCache.isSystemMirrorTimerEnable())
                {
                    String agent = task.getExeAgent();
                    LOGGER.info("mirror_global_enable_timer is false");
                    task.setExeAgent(null);
                    task.setState(SystemTask.TASK_STATE_BEGIN);
                    task.setExeUpdateTime(new Date());
                    systemTaskService.updateTask(task);
                   
                    String msg = "mirror scan back:" + scanTableInfo.toStr() + ",beacuse Timer settting";
                    LOGGER.info(msg);
                    record.setOutput(msg);
                    record.setSuccess(true);
                    record.setExecuteMachine(agent);
                    
                    TableScanBreakInfo scanBreakInfo = new TableScanBreakInfo();
                    scanBreakInfo.setSysTaskId(task.getTaskId());
                    scanBreakInfo.setLimitOffset(limit.getOffset());
                    scanBreakInfo.setBreakTime(new Date());
                    scanBreakInfo.setLength(LENGTH_MAX);
                    scanBreakInfo.setModel("ufm");
                    scanBreakInfo.setOutPut(msg);
                    
                    insertBreakPoint(scanBreakInfo);
                    return;
                }
                
                // 是否允许创建任务表
                if (!copyConfigLocalCache.isAllowCreateTaskByDB())
                {
                    LOGGER.info("isAllowCreateTaskByDB false,coutinue");
                    Thread.sleep(SLEEP_TIME);
                    
                    systemTaskService.updateExecuteState(SystemTask.TASK_STATE_RUNING,
                        new Date(),
                        task.getTaskId());
                    continue;
                }
                
                lstDeleteNode = filesInnerManager.lstContentNode(scanTableInfo.getDbNumber(),
                    scanTableInfo.getTableNumber(),
                    limit);
                
                if (null == lstDeleteNode || lstDeleteNode.isEmpty())
                {
                    LOGGER.info("lstDeleteNode NULL,scan table end,userdb :" + scanTableInfo.getDbNumber()
                        + ",table:" + scanTableInfo.getTableNumber() + ",limit offset:" + limit.getOffset()
                        + ",limit length:" + limit.getLength());
                    break;
                }
                
                LOGGER.info("scan table get inode count is " + lstDeleteNode.size());
                
                dealNumber = dealNumber + bulidTask(lstDeleteNode);
                
                date = new Date();
                // 再次修改任務的時間，一邊distributemirrorbackscantask的掃描
                systemTaskService.updateExecuteState(SystemTask.TASK_STATE_RUNING, date, task.getTaskId());
                
                // 判断是否退出
                if (lstDeleteNode.size() < LENGTH_MAX)
                {
                    break;
                }
                
                // 设置新的便宜量
                limit.setOffset(limit.getOffset() + lstDeleteNode.size());
                limit.checkInnerParameter();
                
            }
            
            LOGGER.info("task [" + task.getTaskId() + "] " + "success, and set state to end");
            systemTaskService.updateExecuteState(SystemTask.TASK_STATE_END, new Date(), task.getTaskId());
            
            String msg = "mirror scan finish:" + scanTableInfo.toStr() + "total files:" + dealNumber;
            LOGGER.info(msg);
            record.setOutput(msg);
            record.setSuccess(true);
            record.setExecuteMachine(task.getExeAgent());
            
        }
        catch (RuntimeException e)
        {
            LOGGER.info(e.getMessage(), e);
            updateErrorState(record, task, scanTableInfo, limit, e);
        }
        catch (Exception e)
        {
            LOGGER.info(e.getMessage(), e);
            updateErrorState(record, task, scanTableInfo, limit, e);
        }
    }
    
    /**
     * 获取执行任务
     */
    
    @Override
    @MethodLogAble
    public Object takeData()
    {
        
        if (!copyConfigLocalCache.isSystemMirrorEnable())
        {
            LOGGER.info("this System mirror enable is false");
            return null;
        }
        
        if (!copyConfigLocalCache.isSystemMirrorTimerEnable())
        {
            LOGGER.info("this System mirror time enable is false");
            return null;
        }
        
        if (!copyConfigLocalCache.isAllowCreateTaskByDB())
        {
            LOGGER.info("isAllowCreateTaskByDB false");
            return null;
        }
        
        JobParament jobParament = parseParament();
        
        if (!checkCurrentTime(jobParament))
        {
            LOGGER.error("checkTime failed,paramter" + jobParament.getExecuteTime() + ",date:" + new Date());
            return null;
        }
        
        if (!checkAgent(jobParament))
        {
            LOGGER.error("checkAgent failed,paramter" + jobParament.getExecuteAgent() + ",date:" + new Date());
            return null;
        }
        
        TaskExecuteInfo info = systemTaskService.getTaskExecuteInfo(TaskKeyConstant.MIRROR_BACK_SCAN_TASK);
        if (info.getTaskStateBeginNumber() == 0L && info.getTaskStateRunningNumber() == 0L
            && info.getTaskStateErrorNumber() == 0L)
        {
            LOGGER.info("TaskExecuteInfo is null ");
            return null;
        }
        
        SystemTask task = null;
        try
        {
            task = systemTaskService.getOneWaitingExecuteTask(null, TaskKeyConstant.MIRROR_BACK_SCAN_TASK);
        }
        catch (Exception e)
        {
            LOGGER.error("getOneWaitingExecuteTask error ," + e.getMessage(), e);
        }
        
        if (null != task)
        {
            LOGGER.info("get a task success , and the task id is:" + task.getTaskId());
        }
        
        return task;
        
    }
    
    /**
     * 生成任务
     * 
     * @param lstDeleteNode
     * @return
     */
    private long bulidTask(List<INode> lstDeleteNode)
    {
        if (null == lstDeleteNode || lstDeleteNode.isEmpty())
        {
            return 0L;
        }
        
        long dealNumber = 0L;
        
        // 形成任务表
        List<CopyTask> lstTask = null;
        for (INode iNode : lstDeleteNode)
        {
            // 判断数据是否彻底删除，是则不复制。如果复制对象不是版本或者文件，也不复制
            if ((iNode.getType() != INode.TYPE_FILE && iNode.getType() != INode.TYPE_VERSION)
                || iNode.getStatus() == INode.STATUS_DELETE)
            {
                continue;
            }
            
            try
            {
                
                // 形成任务表。
                lstTask = copyPolicyHandle.buildCopyTaskForSingleNode(iNode);
                
                if (null == lstTask || lstTask.isEmpty())
                {
                    LOGGER.info("create task ,task is null or size is 0");
                    continue;
                }
                
                // 重复任务检查
                lstTask = copyTaskManager.filterSameTask(lstTask);
                
                // 批量写数据库
                copyTaskService.saveCopyTask(lstTask);
            }
            catch (Exception e)
            {
                // 如果是一个文件的失败，不能影响整个表的扫描
                LOGGER.error("build copytask for inode [id:" + iNode.getId() + ",ownedby:"
                    + iNode.getOwnedBy() + "] failed",
                    e);
                continue;
            }
            
            dealNumber++;
        }
        return dealNumber;
    }
    
    private boolean checkAgent(JobParament jobParament)
    {
        if (jobParament == null)
        {
            LOGGER.info("jobParament is null");
            return true;
        }
        String allowAgent = jobParament.getExecuteAgent();
        if (StringUtils.isBlank(allowAgent))
        {
            LOGGER.info("allowAgent is null or blank ,just return true");
            return true;
        }
        if (allowAgent.indexOf(EXEAGENT) < 0)
        {
            LOGGER.info("allowAgent is not include this agent");
            return false;
        }
        return true;
    }
    
    private JobParament parseParament()
    {
        String parament = this.getParameter();
        return JsonUtils.stringToObject(parament, JobParament.class);
    }
    
    @SuppressWarnings("deprecation")
    private boolean checkCurrentTime(JobParament jobParament)
    {
        try
        {
            // 参数配置，只允许晚上进行扫描,时间格式只记录是 凌晨1-6
            if (jobParament == null)
            {
                LOGGER.info("jobParament is null");
                return true;
            }
            String timeConfig = jobParament.getExecuteTime();
            if (StringUtils.isBlank(timeConfig))
            {
                LOGGER.info("timeConfig is null or blank ,just return true");
                return true;
            }
            int beginTime;
            int endTime;
            String[] times = timeConfig.split("-");
            if (2 == times.length)
            {
                beginTime = Integer.parseInt(times[0]);
                endTime = Integer.parseInt(times[1]);
                Date date = new Date();
                if (beginTime <= endTime && beginTime <= date.getHours() && date.getHours() <= endTime)
                {
                    return true;
                }
                else if (beginTime >= endTime && (beginTime <= date.getHours() || date.getHours() <= endTime))
                {
                    return true;
                }
                return false;
            }
        }
        catch (RuntimeException e)
        {
            LOGGER.warn(e.getMessage(), e);
            return false;
        }
        catch (Exception e)
        {
            LOGGER.warn(e.getMessage(), e);
            return false;
        }
        
        return true;
    }
    
    private void updateErrorState(JobExecuteRecord record, SystemTask task, ScanTableInfo scanTableInfo,
        Limit limit, Exception e)
    {
        systemTaskService.updateExecuteState(SystemTask.TASK_STATE_ERROR, new Date(), task.getTaskId());
        String msg = "mirror scan error:" + scanTableInfo.toStr() + ",offset:" + limit.getOffset();
        LOGGER.warn(msg);
        record.setOutput(msg);
        record.setSuccess(false);
        record.setExecuteMachine(task.getExeAgent());
        LOGGER.error(e.getMessage(), e);
    }
    
    private TableScanBreakInfo checkScanTaskBreak(String taskId)
    {
        TableScanBreakInfo  tableScanBreakInfo= tableScanBreakInfoService.getTableScanBreakInfobyId(taskId);
        
        return tableScanBreakInfo;
    }
    
    private void insertBreakPoint(TableScanBreakInfo breakInfo)
    {
        tableScanBreakInfoService.insert(breakInfo);
    }
}
