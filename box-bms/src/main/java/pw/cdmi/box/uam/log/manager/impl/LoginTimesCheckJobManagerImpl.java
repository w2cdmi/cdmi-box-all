package pw.cdmi.box.uam.log.manager.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pw.cdmi.box.uam.log.manager.LoginTimesCheckJobManager;
import pw.cdmi.common.job.JobDefinition;
import pw.cdmi.common.job.JobState;
import pw.cdmi.common.job.JobType;
import pw.cdmi.common.job.exception.JobException;
import pw.cdmi.common.job.manage.JobManager;
import pw.cdmi.common.job.manage.service.JobService;
import pw.cdmi.common.job.quartz.QuartzJobDefinition;

@Component
public class LoginTimesCheckJobManagerImpl implements LoginTimesCheckJobManager
{
    public static final String SYSTEM_JOB_NAME = "CheckSystemLoginTimesJob";
    
    public static final String USER_JOB_NAME = "CheckUserLoginTimesJob";
    
    public static final String SYSTEM_JOB_BEAN_NAME = "checkSystemLoginTimesTask";
    
    public static final String USER_JOB_BEAN_NAME = "checkUserLoginTimesTask";
    
    @Autowired
    private JobService jobService;
    
    @Autowired
    private JobManager jobManager;
    
    @Override
    public void addSystemJobManager(String jobCron) throws JobException
    {
        JobDefinition jobDefinition = jobService.selectJobDefinition(jobService.getDefaultModelName(),
            SYSTEM_JOB_NAME);
        if (null != jobDefinition)
        {
            jobManager.stopJob(jobDefinition);
            if (jobDefinition instanceof QuartzJobDefinition)
            {
                ((QuartzJobDefinition) jobDefinition).setCron(jobCron);
            }
            
            jobDefinition.setState(JobState.RUNNING);
            
            jobService.updateJobDefinition(jobService.getDefaultModelName(),
                jobDefinition.getJobName(),
                jobDefinition);
        }
        else
        {
            jobService.createQuartzJob(SYSTEM_JOB_NAME,
                "systemLoginTimesCheckJob",
                SYSTEM_JOB_BEAN_NAME,
                "",
                JobType.ClusterCron,
                jobCron,
                true);
        }
        jobManager.startJob(jobDefinition);
    }
    
    @Override
    public void addUserJobManager(String jobCron) throws JobException
    {
        JobDefinition jobDefinition = jobService.selectJobDefinition(jobService.getDefaultModelName(),
            USER_JOB_NAME);
        if (null != jobDefinition)
        {
            jobManager.stopJob(jobDefinition);
            if (jobDefinition instanceof QuartzJobDefinition)
            {
                ((QuartzJobDefinition) jobDefinition).setCron(jobCron);
            }
            
            jobDefinition.setState(JobState.RUNNING);
            
            jobService.updateJobDefinition(jobService.getDefaultModelName(),
                jobDefinition.getJobName(),
                jobDefinition);
        }
        else
        {
            jobService.createQuartzJob(USER_JOB_NAME,
                "userLoginTimesCheckJob",
                USER_JOB_BEAN_NAME,
                "",
                JobType.ClusterCron,
                jobCron,
                true);
        }
        jobManager.startJob(jobDefinition);
    }
    
}
