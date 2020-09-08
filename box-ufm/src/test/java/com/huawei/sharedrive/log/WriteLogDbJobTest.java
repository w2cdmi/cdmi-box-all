package com.huawei.sharedrive.log;

import org.junit.Test;

import com.huawei.sharedrive.app.log.service.impl.WriteLogDbJob;

import pw.cdmi.common.job.JobDefinition;
import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;

public class WriteLogDbJobTest
{
    @Test
    public void doJobTest()
    {
        WriteLogDbJob writeLogDbJob = new WriteLogDbJob();
        writeLogDbJob.doTask(getJobExecuteContext(), new JobExecuteRecord());
        
    }
    
    private JobExecuteContext getJobExecuteContext()
    {
        JobDefinition definition = new JobDefinition(null, null)
        {
        };
       
        JobExecuteContext context = new JobExecuteContext(definition);
        return context;
    }
    
    
}
