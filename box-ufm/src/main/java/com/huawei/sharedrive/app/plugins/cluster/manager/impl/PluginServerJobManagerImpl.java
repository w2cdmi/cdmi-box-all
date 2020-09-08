package com.huawei.sharedrive.app.plugins.cluster.manager.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.plugins.cluster.domain.PluginServiceCluster;
import com.huawei.sharedrive.app.plugins.cluster.manager.PluginServerJobManager;

import pw.cdmi.common.job.JobDefinition;
import pw.cdmi.common.job.JobType;
import pw.cdmi.common.job.exception.JobException;
import pw.cdmi.common.job.manage.JobScheduler;
import pw.cdmi.common.job.manage.service.JobService;
import pw.cdmi.common.job.quartz.QuartzJobDefinition;

@Service("pluginServerJobManager")
public class PluginServerJobManagerImpl implements PluginServerJobManager
{
    public static final Logger LOGGER = LoggerFactory.getLogger(PluginServerJobManagerImpl.class);
    
    @Autowired
    private JobService jobService;
    
    @Autowired
    private JobScheduler jobScheduler;
    
    @Override
    public void addJobManager(PluginServiceCluster cluster) throws JobException
    {
        if (null == cluster)
        {
            return;
        }
        String jobName = getSyncJobName(cluster);
        
        LOGGER.info("add job" + jobName);
        
        JobDefinition jobDefinition = jobService.selectJobDefinition(MODEL, jobName);
        
        if (null != jobDefinition)
        {
            jobScheduler.stopJob(jobDefinition);
            if (jobDefinition instanceof QuartzJobDefinition)
            {
                ((QuartzJobDefinition) jobDefinition).setCron(paseToCron(cluster.getMonitorPeriod()));
            }
            jobScheduler.updateJob(MODEL, jobName, jobDefinition);
        }
        else
        {
            if (cluster.getMonitorPeriod() != 0)
            {
                
                jobDefinition = jobService.createQuartzJob(MODEL,
                    jobName,
                    "Plugin[" + cluster.getAppId() + "] service cluster[" + cluster.getClusterId()
                        + "] check status",
                    SYNC_BEAN_NAME,
                    String.valueOf(cluster.getClusterId()),
                    JobType.Cron,
                    paseToCron(cluster.getMonitorPeriod()),
                    true);
            }
        }
        if (null != jobDefinition)
        {
            jobScheduler.startJob(jobDefinition);
        }
        
    }
    
    @Override
    public void deleteJobManager(PluginServiceCluster cluster) throws JobException
    {
        String jobName = getSyncJobName(cluster);
        
        JobDefinition jobDefinition = jobService.selectJobDefinition(MODEL, jobName);
        
        LOGGER.info(jobName);
        if (null != jobDefinition)
        {
            jobScheduler.stopJob(jobDefinition);
            jobService.deleteJob(MODEL, jobName);
        }
    }
    
    private String getSyncJobName(PluginServiceCluster cluster)
    {
        return JOB_NAME + cluster.getAppId() + JOB_CLUSTER + cluster.getClusterId();
    }
    
    private String paseToCron(int monitorCycle) throws JobException
    {
        monitorCycle = monitorCycle / 60;
        if (monitorCycle > 60 || monitorCycle < 1)
        {
            LOGGER.info("con't cast int to Cron  ,monitorCycle" + monitorCycle + "  is  not in 1~59");
            throw new JobException("con't cast int to Cron  ,monitorCycle" + monitorCycle
                + "  is  not in 1~59");
        }
        StringBuffer sb = new StringBuffer(" * * * ?");
        sb.insert(0, "0/" + monitorCycle);
        sb.insert(0, "0 ");
        LOGGER.info("job cron " + sb.toString());
        return sb.toString();
        
    }
}
