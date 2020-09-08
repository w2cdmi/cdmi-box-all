package com.huawei.sharedrive.app.mirror.datamigration.manager;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.mirror.datamigration.domain.UserDataMigrationTask;
import com.huawei.sharedrive.app.mirror.datamigration.service.UserDataMigrationTaskService;
import com.huawei.sharedrive.app.mirror.domain.MirrorCommonStatic;
import com.huawei.sharedrive.app.mirror.manager.CopyConfigLocalCache;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;

@Service("migrationTaskCheckTimer")
public class MigrationTaskCheckTimer extends QuartzJobTask
{
    @Autowired
    private UserDataMigrationTaskService userDataMigrationTaskService;
    
    @Autowired
    private UserDataMigrationTaskManager userDataMigrationTaskManager;
    
    @Autowired
    private CopyConfigLocalCache copyConfigLocalCache;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MigrationTaskCheckTimer.class);
    
   
    private static final int DEFAULT_LENGTH = 200;

    
    @Override
    public void doTask(JobExecuteContext arg0, JobExecuteRecord record)
    {
        try
        {
            String msg = null;
            if (!copyConfigLocalCache.isSystemMirrorEnable())
            {
                msg = "mirror_global_enable is false,not check task running";
                LOGGER.info(msg);
                record.setSuccess(true);
                record.setOutput(msg);
                return;
            }
            
            if (!copyConfigLocalCache.isSystemMirrorTimerEnable())
            {
                msg = "mirror_global_enable_timer is false,not check task running";
                LOGGER.info(msg);
                record.setSuccess(true);
                record.setOutput(msg);
                return;
            } 
            
            if (MirrorCommonStatic.TASK_STATE_SYSTEM_PAUSE == copyConfigLocalCache.getSystemMirrorTaskState())
            {
                msg = "mirror_global_task_state is pause,not check task running";
                LOGGER.info(msg);
                record.setSuccess(true);
                record.setOutput(msg);
                return;
            }
            
            //检查任务执行状态
            checkTaskExecutingStatus();
            
            record.setSuccess(true);
            record.setOutput("MigrationTaskCheckTimer doTask succesed");
        } catch (Exception e)
        {
            LOGGER.warn(e.getMessage(), e);
            record.setSuccess(false);
            record.setOutput("MigrationTaskCheckTimer doTask failed");
        }
    }
    
    /**
     * 處理構造任務的，做當前所有任務的執行情況，對于長期處于未執行狀態，但是又沒有復制數據的任務，要做清理
     */
    private void checkTaskExecutingStatus()
    {
        
        Limit limit = new Limit();
        limit.setOffset(0L);
        limit.setLength(DEFAULT_LENGTH);
        String info;
        List<UserDataMigrationTask> lst;
        while (true)
        {
            // 歷史任務做遍歷
            lst = userDataMigrationTaskService.listTask(limit);
            if (null == lst || lst.isEmpty())
            {
                LOGGER.info("The system is not user data migration task!");
                break;
            }
            
            for (UserDataMigrationTask task : lst)
            {
                if (task.getStatus() == UserDataMigrationTask.EXECUTE_MIGRATION_STATUS)
                {
                    // 檢查任務是否完成
                    info = userDataMigrationTaskManager.checkDataMigrationTask(task);
                    LOGGER.info(info);
                }
                else if (task.getStatus() == UserDataMigrationTask.FAILED_STATUS
                    || task.getStatus() == UserDataMigrationTask.COMPELETE_STATUS)
                {
                    // 檢查任務是否清理
                    info = userDataMigrationTaskManager.cleanCompleteDataMigrationTask(task);
                    LOGGER.info(info);
                }
                else if(task.getStatus() == UserDataMigrationTask.EXECUTE_SCAN_STATUS)
                {
                    //检查扫描超时时间
                    info = userDataMigrationTaskManager.checkTaskScanTimeOut(task);
                    LOGGER.info(info); 
                }
                else
                {
                    continue;
                }
            }
            
            if (lst.size() < DEFAULT_LENGTH)
            {
                break;
            }
            
            limit.setOffset(limit.getOffset() + DEFAULT_LENGTH);
        }
        
    }
    
}
