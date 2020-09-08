package pw.cdmi.box.uam.log.manager;

import pw.cdmi.common.job.exception.JobException;

public interface LoginTimesCheckJobManager
{
    void addSystemJobManager(String jobCron) throws JobException;
    
    void addUserJobManager(String jobCron) throws JobException;
}
