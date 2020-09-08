package com.huawei.sharedrive.app.spacestatistics.job;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.spacestatistics.domain.AccountStatisticsInfo;
import com.huawei.sharedrive.app.spacestatistics.domain.FilesAdd;
import com.huawei.sharedrive.app.spacestatistics.manager.SpaceStatisticsManager;

import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;

@Component("spaceStatisticsTask")
public class SpaceStatisticsTask extends QuartzJobTask
{
    @Autowired
    private SpaceStatisticsManager spaceStatisticsManager;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SpaceStatisticsTask.class);
    
    @Override
    public void doTask(JobExecuteContext context, JobExecuteRecord record)
    {
        try
        {
            LOGGER.info("[SpaceStatisticsTask] begin to statistics" + context.getJobDefinition());
            Map<Long, FilesAdd> userChangedInfo = spaceStatisticsManager.getUserChangedInfo();
            Map<Long, AccountStatisticsInfo> accountChangedInfo = spaceStatisticsManager.getAccountChangedInfo();
            spaceStatisticsManager.updateUserInfo(userChangedInfo);
            spaceStatisticsManager.updateAccountInfo(accountChangedInfo);
        }
        catch (Exception e)
        {
            LOGGER.error("SpaceStatisticsTask error", e);
            record.setSuccess(false);
        }
    }
    
}
