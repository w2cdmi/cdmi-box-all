package com.huawei.sharedrive.app.mirror.datamigration.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.huawei.sharedrive.app.mirror.datamigration.domain.UserDataMigrationTask;
import com.huawei.sharedrive.app.mirror.datamigration.service.UserDataMigrationTaskService;
import com.huawei.sharedrive.app.mirror.domain.MirrorCommonStatic;
import com.huawei.sharedrive.app.mirror.manager.CopyConfigLocalCache;
import com.huawei.sharedrive.app.mirror.service.CopyTaskService;
import com.huawei.sharedrive.app.utils.PropertiesUtils;

import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.daemon.DaemonJobTask;

/**
 * 数据迁移任务调度，调度到分析线程池中只数据扫描
 * 
 * @author c00287749
 * 
 */
@Service("userDataMigrationTaskJob")
public class UserDataMigrationTaskJob extends DaemonJobTask<Object>
{
    @Autowired
    private CopyConfigLocalCache copyConfigLocalCache;
    
    @Autowired
    private UserDataMigrationTaskService userDataMigrationTaskService;
    
    @Autowired
    private CopyTaskService copyTaskService;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UserDataMigrationTaskJob.class);
    
    private String selfPrivateIp = PropertiesUtils.getProperty("self.privateAddr", "127.0.0.1");
    
    @Override
    public boolean available(Object data)
    {
        if (null != data)
        {
            return true;
        }
        
        return false;
    }
    
    @Override
    public void doTask(JobExecuteContext arg0, JobExecuteRecord record, Object task)
    {
        if (null == task)
        {
            LOGGER.error("task is null");
            return;
        }
        
        try
        {
            // 数据迁移Task
            UserDataMigrationTask migrationTask = (UserDataMigrationTask) task;
            
            if (UserDataMigrationTask.EXECUTE_SCAN_STATUS == migrationTask.getStatus())
            {
                createNewAnalysisTask(record, migrationTask);
            }
        }
        catch (Exception e)
        {
            LOGGER.warn(e.getMessage(), e);
            record.setSuccess(false);
            record.setOutput("doTask failed");
        }
        
    }
    
    /**
     * 创建分析任务
     * 
     * @param record
     * @param task
     */
    private void createNewAnalysisTask(JobExecuteRecord record, UserDataMigrationTask migrationTask)
    {
        
        String info = "cloudUserId:" + migrationTask.getCloudUserId() + ",createAt:"
            + migrationTask.getCreatedAt() + ",ExeAgent:" + migrationTask.getExeAgent();
        
        // 先清除可能存在的垃圾数据
        long trashTask = copyTaskService.deleteTaskForDataMigration(migrationTask.getCloudUserId());
        if (trashTask != 0)
        {
            LOGGER.warn("It should not happen,the cloud user:" + migrationTask.getCloudUserId()
                + " had trash task ,number:" + trashTask);
        }
        
        if (DataMigrationTaskAnalysisThreadPool.addTask(new UserDataMigrationTaskAnalysis(migrationTask)))
        {
            info = "Add migrationTask to DataMigrationExecuteThreadPool succeed," + info;
            LOGGER.info(info);
            record.setSuccess(true);
            record.setOutput(info);
        }
        else
        {
            // 恢复到初始化状态
            migrationTask.setStatus(UserDataMigrationTask.INIT_STATUS);
            migrationTask.setExeAgent(null);
            userDataMigrationTaskService.updateStatus(migrationTask);
            
            info = "Add migrationTask to DataMigrationExecuteThreadPool failed," + info;
            LOGGER.warn(info);
            record.setSuccess(false);
            record.setOutput(info);
        }
    }
    
    @Override
    public UserDataMigrationTask takeData()
    {
        if (!copyConfigLocalCache.isSystemMirrorEnable())
        {
            LOGGER.info("mirror_global_enable is false");
            return null;
        }
        
        if (!copyConfigLocalCache.isSystemMirrorTimerEnable())
        {
            LOGGER.info("mirror_global_enable_timer is false");
            return null;
        } 
        
        if (MirrorCommonStatic.TASK_STATE_SYSTEM_PAUSE == copyConfigLocalCache.getSystemMirrorTaskState())
        {
            LOGGER.info("mirror_global_task_state is pause,migration not build task");
            return null;
        }
        
        if (!copyConfigLocalCache.isAllowCreateTaskByDB())
        {
            LOGGER.info("isAllowCreateTaskByDB false,note execute user data migration");
            return null;
        }
        
        return userDataMigrationTaskService.getOneTaskToExe(selfPrivateIp);
        
    }
    
    
    
}
