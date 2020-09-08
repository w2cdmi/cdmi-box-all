package com.huawei.sharedrive.app.spacestatistics.job;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.spacestatistics.manager.ModifySpaceDBTaskManager;

import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;

@Component("modifySpaceDBTask")
public class ModifySpaceDBTask extends QuartzJobTask
{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ModifySpaceDBTask.class);
    
    @Autowired
    private ModifySpaceDBTaskManager modifySpaceDBTaskManager;
    
    @Override
    public void doTask(JobExecuteContext context, JobExecuteRecord record)
    {
        try
        {
            LOGGER.info("[ModifyDBTask] begin to statistics" + context.getJobDefinition());
            modifySpaceDBTaskManager.modifySpaceDB();
        }
        catch (Exception e)
        {
            LOGGER.error("ModifyDBTask error", e);
            record.setSuccess(false);
        }
    }
    
    public void doTask1() throws SQLException
    {
        modifySpaceDBTaskManager.modifySpaceDB();
    }
}
