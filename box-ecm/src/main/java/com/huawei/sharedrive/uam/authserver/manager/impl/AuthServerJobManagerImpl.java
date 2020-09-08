package com.huawei.sharedrive.uam.authserver.manager.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.authserver.manager.AuthServerJobManager;

import pw.cdmi.common.domain.enterprise.ldap.LdapNodeFilterConfig;
import pw.cdmi.common.job.JobDefinition;
import pw.cdmi.common.job.JobState;
import pw.cdmi.common.job.JobType;
import pw.cdmi.common.job.exception.JobException;
import pw.cdmi.common.job.manage.JobManager;
import pw.cdmi.common.job.manage.service.JobService;
import pw.cdmi.common.job.quartz.QuartzJobDefinition;

@Component
public class AuthServerJobManagerImpl implements AuthServerJobManager
{
    public static final String JOB_NAME = "authServerJobManager_sync_users_";
    
    public static final String SYNC_BEAN_NAME = "syncUserJob";
    
    @Autowired
    private JobService jobService;
    
    @Autowired
    private JobManager jobManager;
    
    @Override
    public void addJobManager(LdapNodeFilterConfig ldapNodeFilterConfig, Long authServerId)
        throws JobException
    {
        if (null == ldapNodeFilterConfig)
        {
            return;
        }
        
        String jobName = getSyncUserJobName(authServerId);
        
        JobDefinition jobDefinition = jobService.selectJobDefinition(jobService.getDefaultModelName(),
            jobName);
        
        if (null != jobDefinition)
        {
            jobManager.stopJob(jobDefinition);
            if (jobDefinition instanceof QuartzJobDefinition)
            {
                ((QuartzJobDefinition) jobDefinition).setCron(ldapNodeFilterConfig.getSyncCycle());
            }
            
            if (ldapNodeFilterConfig.getIsTimingSync())
            {
                jobDefinition.setState(JobState.RUNNING);
            }
            else
            {
                jobDefinition.setState(JobState.STOP);
            }
            
            jobService.updateJobDefinition(jobService.getDefaultModelName(),
                jobDefinition.getJobName(),
                jobDefinition);
        }
        else if (ldapNodeFilterConfig.getIsTimingSync()
                && StringUtils.isNotBlank(ldapNodeFilterConfig.getSyncCycle()))
        {
            jobService.createQuartzJob(jobName,
                "sync users job for authServerId : " + authServerId,
                SYNC_BEAN_NAME,
                String.valueOf(authServerId),
                JobType.ClusterCron,
                ldapNodeFilterConfig.getSyncCycle(),
                true);
        }
        if (ldapNodeFilterConfig.getIsTimingSync()
            && StringUtils.isNotBlank(ldapNodeFilterConfig.getSyncCycle()))
        {
            jobManager.startJob(jobDefinition);
        }
    }
    
    private String getSyncUserJobName(Long authServerId)
    {
        return JOB_NAME + authServerId;
    }
}
