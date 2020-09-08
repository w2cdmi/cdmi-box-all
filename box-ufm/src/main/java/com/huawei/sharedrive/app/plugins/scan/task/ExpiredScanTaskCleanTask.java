package com.huawei.sharedrive.app.plugins.scan.task;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.plugins.scan.manager.SecurityScanManager;

import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;

/**
 * 超时扫描任务清理
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2015-5-28
 * @see
 * @since
 */
@Component("expiredScanTaskCleanTask")
public class ExpiredScanTaskCleanTask extends QuartzJobTask
{
    
    private static final int DEFAULT_EXPIRED_DAYS = 3;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ExpiredScanTaskCleanTask.class);
    
    @Autowired
    private SecurityScanManager securityScanManager;
    
    @Override
    public void doTask(JobExecuteContext context, JobExecuteRecord record)
    {
        try
        {
            // 扫描任务超时时间
            int days = DEFAULT_EXPIRED_DAYS;
            if (StringUtils.isNotBlank(getParameter()))
            {
                days = Integer.parseInt(getParameter());
            }
            LOGGER.info("Execute expired scan task clear task. Expired days: {}", days);
            int result = securityScanManager.deleteScanTaskBefore(days);
            record.setSuccess(true);
            record.setOutput("Clean expired scan task success.");
            LOGGER.info("Clean expired scan task success. Expired task count: {}", result);
        }
        catch (RuntimeException e)
        {
            String message = "Clean expired scan task failed.";
            LOGGER.error(message);
            record.setSuccess(false);
            record.setOutput(message);
        }
        catch (Exception e)
        {
            String message = "Clean expired scan task failed.";
            LOGGER.error(message);
            record.setSuccess(false);
            record.setOutput(message);
        }
    }
    
}
