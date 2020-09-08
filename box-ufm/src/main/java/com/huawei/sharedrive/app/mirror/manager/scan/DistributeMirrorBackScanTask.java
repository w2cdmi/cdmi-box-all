package com.huawei.sharedrive.app.mirror.manager.scan;

import java.util.ArrayList;
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
import com.huawei.sharedrive.app.common.systemtask.domain.UserDBInfo;
import com.huawei.sharedrive.app.common.systemtask.service.SystemTaskService;
import com.huawei.sharedrive.app.common.systemtask.service.UserDBInfoService;
import com.huawei.sharedrive.app.core.backtask.AbstractScanTask;
import com.huawei.sharedrive.app.files.dao.impl.INodeDAOImpl;
import com.huawei.sharedrive.app.mirror.appdatamigration.manager.AppDataMigrationManager;
import com.huawei.sharedrive.app.mirror.manager.CopyConfigLocalCache;

import pw.cdmi.common.config.service.ConfigManager;
import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.daemon.DaemonJobActiveUtils;
import pw.cdmi.common.job.exception.JobException;
import pw.cdmi.core.utils.MethodLogAble;
import pw.cdmi.core.utils.RandomGUID;

@Service("distributeMirrorBackScanTask")
public class DistributeMirrorBackScanTask extends AbstractScanTask
{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DistributeMirrorBackScanTask.class);
    
    private static final int MAX_USERDB_COUNT = 16;
    
    private static final long TIMEOUT_TIME = 20 * 60 * 1000;
    
    @Autowired
    private ConfigManager configManager;
    
    @Autowired
    private UserDBInfoService userDBInfoService;
    
    @Autowired
    private SystemTaskService systemTaskService;
    
    @Autowired
    private DaemonJobActiveUtils daemonJobActiveUtils;
    
    @Autowired
    private CopyConfigLocalCache copyConfigLocalCache;
    
    @Autowired
    private AppDataMigrationManager appDataMigrationManager;
    
    @Override
    @MethodLogAble
    public void doTask(JobExecuteContext context, JobExecuteRecord record)
    {
        
        LOGGER.info("DistributeMirrorBackScanTask begin.");
        
        try
        {
            if (!copyConfigLocalCache.isSystemMirrorEnable())
            {
                LOGGER.info("mirror_global_enable is false");
                return;
            }
            
            if (!copyConfigLocalCache.isSystemMirrorTimerEnable())
            {
                LOGGER.info("mirror_global_enable_timer is false");
                return;
            }
            
            // 检测上一个任务执行情况
            if (!checkLastTask())
            {
                configManager.setConfig(TaskKeyConstant.MIRROR_BACK_SCAN_TASK, "");
                String message = "last task not complete,not create new task.";
                LOGGER.warn(message);
                record.setSuccess(true);
                record.setOutput(message);
                return;
            }
            
            // 清除当前的任务。
            clearMirrorScanTask();
            
            // 分片任务
            String pTaskID = distributeScanTask();
            
            if (StringUtils.isNotBlank(pTaskID))
            {
                // 发送消息
                daemonJobActiveUtils.activeJob(MirrorBackScanTask.getJobName());
                
                // 任务创建统计将要迁移所有文件数量和大小
                appDataMigrationManager.statisticTotalFileNumAndSize();
                
                String message = "distribute mirror scan success.";
                LOGGER.info(message);
                record.setSuccess(true);
                record.setOutput(message);
            }
            else
            {
                String message = "distribute mirror  scan task error.";
                LOGGER.warn(message);
                record.setSuccess(false);
                record.setOutput(message);
            }
        }
        catch (JobException e)
        {
            LOGGER.error("DistributeMirrorBackScanTask failed.");
        }
        LOGGER.info("DistributeMirrorBackScanTask end.");
    }
    
    private SystemTask createDistributeFileScanTask(Date createTime)
    {
        SystemTask task = new SystemTask();
        task.setCreateTime(createTime);
        task.setTaskId(new RandomGUID().getValueAfterMD5());
        task.setState(SystemTask.TASK_STATE_BEGIN);
        task.setTaskKey(TaskKeyConstant.DISTRIBUTE_MIRROR_BACK_SCAN_TASK);
        return task;
    }
    
    /**
     * 创建任务
     * 
     * @param pTask
     * @param db
     * @param tableName
     * @param tableNumber
     * @param lastModfied
     * @return
     */
    
    private SystemTask createMirrorFileScanTableTask(SystemTask pTask, UserDBInfo db, String tableName,
        int tableNumber)
    {
        SystemTask task = new SystemTask();
        task.setCreateTime(pTask.getCreateTime());
        task.setTaskId(new RandomGUID().getValueAfterMD5() + '_' + tableName);
        task.setpTaskId(pTask.getTaskId());
        task.setState(SystemTask.TASK_STATE_BEGIN);
        ScanTableInfo tableInfo = new ScanTableInfo();
        tableInfo.setDbName(db.getDbName());
        tableInfo.setDbNumber(db.getDbNumber());
        tableInfo.setTableName(tableName);
        tableInfo.setTableNumber(tableNumber);
        task.setTaskKey(TaskKeyConstant.MIRROR_BACK_SCAN_TASK);
        task.setTaskInfo(ScanTableInfo.toJsonStr(tableInfo));
        return task;
    }
    
    /**
     * 清理任务，保留一个distribution任务用来计算扫描时间
     */
    private void clearMirrorScanTask()
    {
        List<SystemTask> lstSystemTask = systemTaskService.listSystemTaskByTaskKey(TaskKeyConstant.DISTRIBUTE_MIRROR_BACK_SCAN_TASK);
        for (SystemTask systemTask : lstSystemTask)
        {
            if (systemTask.getState() == SystemTask.TASK_STATE_END)
            {
                systemTaskService.deleteTask(systemTask.getTaskId());
                LOGGER.info("delect systemtask ,taskkey is:"
                    + TaskKeyConstant.DISTRIBUTE_MIRROR_BACK_SCAN_TASK + " and taskid is:"
                    + systemTask.getTaskId());
            }
        }
        for (SystemTask systemTask : lstSystemTask)
        {
            if (systemTask.getState() == SystemTask.TASK_STATE_BEGIN)
            {
                // 这里状态为begin的应该只有一个
                systemTaskService.updateTaskState(SystemTask.TASK_STATE_END, systemTask.getTaskId());
                LOGGER.info("update systemtask ,,taskkey is:"
                    + TaskKeyConstant.DISTRIBUTE_MIRROR_BACK_SCAN_TASK + " and taskid is:"
                    + systemTask.getTaskId());
            }
        }
        systemTaskService.deleteTaskByTaskKey(TaskKeyConstant.MIRROR_BACK_SCAN_TASK);
    }
    
    /**
     * 分配任务
     * 
     * @return
     */
    private String distributeScanTask()
    {
        
        List<UserDBInfo> dbInfos = userDBInfoService.listAll();
        
        if (null == dbInfos || dbInfos.isEmpty())
        {
            LOGGER.error("Data exception ,userdb is null");
            return null;
        }
        
        // 做数据库数量校验
        if (dbInfos.size() > MAX_USERDB_COUNT)
        {
            LOGGER.error("Data exception ,userdb count >" + MAX_USERDB_COUNT);
            return null;
        }
        
        List<SystemTask> allTask = new ArrayList<SystemTask>(dbInfos.size() + 1);
        Date createTime = new Date();
        SystemTask pTask = createDistributeFileScanTask(createTime);
        allTask.add(pTask);
        
        SystemTask task = null;
        for (UserDBInfo db : dbInfos)
        {
            for (int i = 0; i < INodeDAOImpl.TABLE_COUNT; i++)
            {
                task = createMirrorFileScanTableTask(pTask, db, "inode_" + i, i);
                allTask.add(task);
            }
        }
        
        // 添加任务表
        systemTaskService.createTask(allTask);
        return pTask.getTaskId();
        
    }
    
    /**
     * 检测上一次任务执行
     */
    private boolean checkLastTask()
    {
        /**
         * 这一部分用来恢复state=1的卡住的任务（发送原因，重启服务，异常等
         */
        try
        {
            List<SystemTask> lstRunning = systemTaskService.listSystemTaskByTaskKeyAndState(TaskKeyConstant.MIRROR_BACK_SCAN_TASK,
                SystemTask.TASK_STATE_RUNING);
            if (null != lstRunning)
            {
                Date temp = null;
                for (SystemTask task : lstRunning)
                {
                    temp = new Date();
                    if (null == task.getExeUpdateTime())
                    {
                        LOGGER.info("task exeupdatetime is null, update it ,taskid is:" + task.getTaskId());
                        task.setExeUpdateTime(temp);
                        systemTaskService.updateTask(task);
                        continue;
                    }
                    if (temp.getTime() - task.getExeUpdateTime().getTime() > TIMEOUT_TIME)
                    {
                        LOGGER.info("Running task:" + task.getTaskInfo()
                            + " cost too long time ,recovery this task:" + task.getTaskId());
                        // task.setExeAgent(null);
                        task.setState(SystemTask.TASK_STATE_END);
                        task.setExeUpdateTime(temp);
                        // 更新执行失败的任务
                        systemTaskService.updateTask(task);
                    }
                }
            }
        }
        catch (Exception e)
        {
            LOGGER.error(e.getMessage(), e);
        }
        
        try
        {
            TaskExecuteInfo info = systemTaskService.getTaskExecuteInfo(TaskKeyConstant.MIRROR_BACK_SCAN_TASK);
            
            if (info.getTaskStateBeginNumber() == 0L && info.getTaskStateRunningNumber() == 0L
                && info.getTaskStateErrorNumber() == 0L)
            {
                if (appDataMigrationManager.checkNoAppMigrationTaskIsLeft())
                {
                    return true;
                }
            }
            
        }
        catch (Exception e)
        {
            LOGGER.error(e.getMessage(), e);
        }
        
        try
        {
            List<SystemTask> lstFailed = systemTaskService.listSystemTaskByTaskKeyAndState(TaskKeyConstant.MIRROR_BACK_SCAN_TASK,
                SystemTask.TASK_STATE_ERROR);
            if (null != lstFailed)
            {
                Date temp = null;
                for (SystemTask task : lstFailed)
                {
                    LOGGER.info("Failed task:" + task.getTaskInfo() + ",recovery this task:"
                        + task.getTaskId());
                    task.setExeAgent(null);
                    task.setState(SystemTask.TASK_STATE_BEGIN);
                    temp = new Date();
                    task.setExeUpdateTime(temp);
                    // 更新执行失败的任务
                    systemTaskService.updateTask(task);
                }
                
            }
        }
        catch (Exception e)
        {
            LOGGER.error("recovery failed task error", e);
        }
        
        return false;
        
    }
    
}
