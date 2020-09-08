package com.huawei.sharedrive.app.mirror.manager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.huawei.sharedrive.app.mirror.appdatamigration.manager.AppDataMigrationManager;
import com.huawei.sharedrive.app.mirror.domain.CopyTask;
import com.huawei.sharedrive.app.mirror.domain.CopyTaskStatistic;
import com.huawei.sharedrive.app.mirror.domain.CopyType;
import com.huawei.sharedrive.app.mirror.domain.MirrorCommonStatic;
import com.huawei.sharedrive.app.mirror.service.CopyTaskService;
import com.huawei.sharedrive.app.mirror.service.MirrorSystemConfigService;
import com.huawei.sharedrive.app.system.service.SystemConfigService;

import pw.cdmi.common.domain.SystemConfig;
import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;
import pw.cdmi.core.utils.DateUtils;
import pw.cdmi.core.utils.MethodLogAble;

/**
 * 定时检查当前任务表是否有到期应该执行的任务，重新把异常的任务恢复成可以执行
 * 
 * @author c00287749
 * 
 */
@Component("copyTaskTimer")
public class CopyTaskTimer extends QuartzJobTask
{
    
    @Autowired
    private CopyTaskService copyTaskService;
    
    @Autowired
    private CopyConfigLocalCache copyConfigLocalCache;
    
    @Autowired
    private MirrorSystemConfigService mirrorSystemConfigService;
    
    @Autowired
    private SystemConfigService systemConfigService;
    
    @Autowired
    private AppDataMigrationManager appDataMigrationManager;
    
    // 任務發送超時時間
    private int taskSendTimeOutMinute = 60;
    
    // 任務執行超時時間 24小時
    private int taskExeTimeOutMinute = 60 * 24;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CopyTaskTimer.class);
    
    @PostConstruct
    public void init()
    {
        taskSendTimeOutMinute = mirrorSystemConfigService.getCopyTaskSendTimeout();
        taskExeTimeOutMinute = mirrorSystemConfigService.getCopyTaskExeTimeout();
    }
    
    private void deleteCopyTask(List<CopyTask> lstCopyTask)
    {
        for (CopyTask copyTask : lstCopyTask)
        {
            if (copyTask.getCopyType() == CopyType.COPY_TYPE_APP_MIGRATION_CLEAR_OLD_DATA.getCopyType()
                || copyTask.getCopyType() == CopyType.COPY_TYPE_APP_MIGRATION_PERSISTED_OLD_DATA.getCopyType())
            {
                appDataMigrationManager.hasFailedFile(copyTask, "can not recover failed task");
            }
            copyTaskService.deleteCopyTask(copyTask);
        }
    }
    
    /**
     * 检查异常任务，并处理
     */
    private void handleExeFailedTask()
    {
        try
        {
            
            // 删除不能恢复的异常错误, 错误码可以配置
            String errStr = mirrorSystemConfigService.getErrorCodeForNeedDeleteFailedTask();
            if (StringUtils.isEmpty(errStr))
            {
                errStr = MirrorCommonStatic.ERROR_CODE_FOR_DELETE_TASK;
            }
            
            String[] errs = errStr.split(",");
            List<CopyTask> lstCopyTask = null;
            for (String err : errs)
            {
                lstCopyTask = copyTaskService.lstTaskByErrorCode(Integer.parseInt(err));
                if (lstCopyTask == null || lstCopyTask.isEmpty())
                {
                    LOGGER.info("lstTaskByErrorCode null");
                    continue;
                }
                LOGGER.info("delete " + lstCopyTask.size() + " uncovered task");
                deleteCopyTask(lstCopyTask);
            }
            
            // 恢复所有失败任务
            long recoveryNum = copyTaskService.recoveryFailedTask();
            
            LOGGER.info("failed task to recovery ,recoveryNum :" + recoveryNum);
            
        }
        catch (Exception e)
        {
            LOGGER.warn(e.getMessage(), e);
        }
    }
    
    /**
     * 处理执行超时的任务
     */
    private void recoveryExeTimeOutTask()
    {
        try
        {
            Date outTime = new Date();
            outTime = DateUtils.getDateBeforeMinute(outTime, taskExeTimeOutMinute);
            copyTaskService.recoveryExeTimeOutTask(outTime);
        }
        catch (Exception e)
        {
            LOGGER.warn(e.getMessage(), e);
        }
    }
    
    /**
     * 处理任务已经被弹出，但是并没有执行；即POP为1，state为waiting状态,并修改时间大于1小时
     */
    private void recoveryNoExeTaskForPopState()
    {
        try
        {
            Date outTime = new Date();
            outTime = DateUtils.getDateBeforeMinute(outTime, taskSendTimeOutMinute);
            copyTaskService.recoveryNoExeTaskForPopState(outTime);
        }
        catch (Exception e)
        {
            LOGGER.warn(e.getMessage(), e);
        }
    }
    
    /**
     * 定时任务只能在指定的时间段之间
     */
    private void activateTimeTask()
    {
        try
        {
            // 修改处于定时状态,使之成为任务处于等待状态
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            
            String nowDateString = format.format(new Date());
            
            // 定时处罚任务
            copyTaskService.activateTimeTask(nowDateString);
        }
        catch (Exception e)
        {
            LOGGER.warn(e.getMessage(), e);
        }
        
    }
    
    /**
     * 已经激活为等待的任务，还没有被POP的，如果时间到了，需要停止执行
     */
    private void deactivatOverdueTimeTask()
    {
        try
        {
            // 修改处于定时状态,使之成为任务处于等待状态
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            
            String nowDateString = format.format(new Date());
            
            // 去激活
            copyTaskService.deactivatOverdueTimeTask(nowDateString);
        }
        catch (Exception e)
        {
            LOGGER.warn(e.getMessage(), e);
        }
        
    }
    
    /**
     * 在暂停任务时，如果ispop=1，那么会形成ispop=1 and state=4 的任务 在恢复任务时，ispop=1 and
     * state=4这种状态的任务不会被恢复 只有在ispop超时后，才会将ispop修改为0，这里再修改state和系统的状态一致:state=4 --> state=0
     */
    private void recoveryTaskStateNotRight()
    {
        SystemConfig systemConfig = systemConfigService.getConfig(MirrorCommonStatic.MIRROR_GLOBAL_TASK_STATE);
        if (systemConfig.getValue().equals("" + MirrorCommonStatic.TASK_STATE_WAITTING))
        {
            copyTaskService.updateTaskStateSameWithSystemConfigState(MirrorCommonStatic.TASK_STATE_WAITTING,
                MirrorCommonStatic.TASK_STATE_SYSTEM_PAUSE);
        }
    }
    
    @MethodLogAble
    @Override
    public void doTask(JobExecuteContext arg0, JobExecuteRecord record)
    {
        if (!copyConfigLocalCache.isSystemMirrorEnable())
        {
            LOGGER.debug("mirror_global_enable is false");
            return;
        }
        
        if (!copyConfigLocalCache.isSystemMirrorTimerEnable())
        {
            LOGGER.info("this System mirror time enable is false");
            return;
        }
        
        // 定期检查等等执行任务的状态,激活这些定时任务
        activateTimeTask();
        
        // 已经激活为等待的任务，还没有被POP的，如果时间到了，需要停止执行
        deactivatOverdueTimeTask();
        
        // 更新状态尚为执行但是已经获取的任务，上一次更新时间和现在的时间大于1小时的（在取出任务到发送任务之间的时间）
        recoveryNoExeTaskForPopState();
        
        // 修复在系统复制任务状态为运行的情况下，数据库表中state=4的任务
        recoveryTaskStateNotRight();
        
        // 处理执行错误的任务
        handleExeFailedTask();
        
        // 处理执行超时时间的任务
        recoveryExeTimeOutTask();
        
        // 统计任务
        CopyTaskStatistic statistic;
        String msg = null;
        try
        {
            statistic = copyTaskService.statisticCopyTask();
            msg = statistic.toOutJsonStr();
        }
        catch (Exception e)
        {
            LOGGER.warn(e.getMessage(), e);
        }
        
        LOGGER.info(msg);
        
        // 反馈统计信息
        record.setOutput(msg);
        record.setSuccess(true);
        
    }
    
}
