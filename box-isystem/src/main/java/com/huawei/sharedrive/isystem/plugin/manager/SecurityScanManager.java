package com.huawei.sharedrive.isystem.plugin.manager;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.common.job.thrift.JobInfo;
import com.huawei.sharedrive.isystem.common.systemtask.domain.TaskKeyConstant;
import com.huawei.sharedrive.isystem.common.systemtask.service.SystemTaskService;
import com.huawei.sharedrive.isystem.exception.BadRquestException;
import com.huawei.sharedrive.isystem.plugin.domain.SecurityScanTask;
import com.huawei.sharedrive.isystem.plugin.service.SecurityScanService;
import com.huawei.sharedrive.isystem.thrift.client.JobThriftServiceClient;

import pw.cdmi.common.thrift.client.ThriftClientProxyFactory;

@Component
public class SecurityScanManager
{
    @Autowired
    private SecurityScanService securityScanService;
    
    @Autowired
    private SystemTaskService systemTaskService;
    
    @Autowired
    private ThriftClientProxyFactory ufmThriftClientProxyFactory;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityScanManager.class);
    
    public int getWaitingSecurityScanTaskNum()
    {
        return securityScanService.getTotalTasks(SecurityScanTask.STATUS_WAIING);
    }
    
    public int getTableScanTaskNum(int status)
    {
        long result = systemTaskService.getSystemTaskTotalsByTaskKeyAndState(TaskKeyConstant.SYSTEM_SCAN_TASK,
            status);
        return ((Long) result).intValue();
    }
    
    public void updateQuartzJobTask(int exeStartAt, int exeEndAt)
    {
        String jobTime = securityScanService.parseToCron(exeStartAt, exeEndAt);
        securityScanService.updateScanJob(jobTime);
    }
    
    public void restartJob(String jobName)
    {
        JobInfo job = new JobInfo();
        job.setJobName(jobName);
        job.setClusterId(-1);
        try
        {
            ufmThriftClientProxyFactory.getProxy(JobThriftServiceClient.class).stopJob(job);
            
            ufmThriftClientProxyFactory.getProxy(JobThriftServiceClient.class).startJob(job);
        }
        catch (TException e)
        {
            LOGGER.error("restart job [ " + jobName + "] failed.", e);
            throw new BadRquestException(e);
        }
    }
    
    public void stopJob(String jobName)
    {
        JobInfo job = new JobInfo(); 
        job.setJobName(jobName);
        job.setClusterId(-1);
        try
        {
            ufmThriftClientProxyFactory.getProxy(JobThriftServiceClient.class).stopJob(job);
        }
        catch (TException e)
        {
            LOGGER.error("stop job [ " + jobName + "] failed.", e);
            throw new BadRquestException(e);
        }
    }
}
