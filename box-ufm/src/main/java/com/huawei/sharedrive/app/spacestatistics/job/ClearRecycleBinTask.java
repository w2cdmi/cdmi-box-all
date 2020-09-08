package com.huawei.sharedrive.app.spacestatistics.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.spacestatistics.manager.ClearRecycleBinManager;

import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;

@Component("clearRecycleBinTask")
public class ClearRecycleBinTask extends QuartzJobTask
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ClearRecycleBinTask.class);
    
    @Autowired
    private ClearRecycleBinManager clearRecycleBinManager;
    
    @Override
    public void doTask(JobExecuteContext context, JobExecuteRecord record)
    {
        try
        {
            LOGGER.info("[SpaceStatisticsTask] begin to statistics" + context.getJobDefinition());
            clearRecycleBinManager.clearRecycleBin();
            
        }
        catch (Exception e)
        {
            LOGGER.error("SpaceStatisticsTask error", e);
            record.setSuccess(false);
        }
    }
    
}
