/*
 * Copyright Notice:
 *      Copyright  1998-2009, Huawei Technologies Co., Ltd.  ALL Rights Reserved.
 *
 *      Warning: This computer software sourcecode is protected by copyright law
 *      and international treaties. Unauthorized reproduction or distribution
 *      of this sourcecode, or any portion of it, may result in severe civil and
 *      criminal penalties, and will be prosecuted to the maximum extent
 *      possible under the law.
 */
package com.huawei.sharedrive.app.isystem.thrift;

import java.util.ArrayList;
import java.util.List;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.huawei.sharedrive.app.dataserver.domain.DataCenter;
import com.huawei.sharedrive.app.dataserver.domain.ResourceGroup;
import com.huawei.sharedrive.app.dataserver.service.DCService;
import com.huawei.sharedrive.app.dataserver.thrift.client.JobThriftServiceClient;
import com.huawei.sharedrive.app.dns.service.DssDomainService;
import com.huawei.sharedrive.common.job.thrift.JobInfo;
import com.huawei.sharedrive.common.job.thrift.ThriftJobExecuteRecord;

import pw.cdmi.common.job.manage.thrift.JobThriftServiceImpl;
import pw.cdmi.core.utils.MethodLogAble;

/**
 * 
 * @author s90006125
 * 
 */
public class JobThriftServiceImplOnUfm extends JobThriftServiceImpl
{
    private static final Logger LOGGER = LoggerFactory.getLogger(JobThriftServiceImplOnUfm.class);
    
    @Autowired
    private DCService dcService;
    
    @Autowired
    private DssDomainService dssDomainService;
    
    private static final int UFM_CLUSTER_ID = -1;
    
    @Override
    @MethodLogAble(newLogId = true)
    public JobInfo createJob(JobInfo jobInfo) throws TException
    {
        if (isUfm(jobInfo))
        {
            try
            {
                return super.createJob(jobInfo);
            }
            catch (TException e)
            {
                LOGGER.error(e.getMessage());
                throw e;
            }
        }
        
        // 远程到DSS上创建定时任务
        JobThriftServiceClient client = null;
        try
        {
            client = getClient(jobInfo);
            return client.createJob(jobInfo);
        }
        catch (Exception e)
        {
            String message = "create job on [ " + jobInfo.getClusterId() + " ] failed.";
            LOGGER.error(message);
            throw new TException(message, e);
        }
        finally
        {
            if (null != client)
            {
                client.close();
            }
        }
        
    }
    
    @Override
    @MethodLogAble(newLogId = true)
    public List<JobInfo> listAllJobDetail() throws TException
    {
        // 列举UFM的任务列表
        List<JobInfo> jobInfos = super.listAllJobDetail();
        if (null == jobInfos)
        {
            jobInfos = new ArrayList<JobInfo>(0);
        }
        
        for (JobInfo job : jobInfos)
        {
            job.setClusterId(UFM_CLUSTER_ID);
        }
        
        // 列举所有DSS的任务列表
        List<DataCenter> dataCenters = dcService.listDataCenter();
        if (null == dataCenters || dataCenters.isEmpty())
        {
            LOGGER.info("data center is empty.");
            return jobInfos;
        }
        
        JobThriftServiceClient client = null;
        List<JobInfo> dcJobs = null;
        for (DataCenter dc : dataCenters)
        {
            try
            {
                client = getClient(dc);
                dcJobs = client.listAllJobDetail();
            }
            catch (Exception e)
            {
                // 其中一个DC获取失败，不影响其他DC的获取
                LOGGER.error("list job for dc [ " + dc.getId() + ", " + dc.getName() + " ] failed.");
                continue;
            }
            finally
            {
                if (null != client)
                {
                    client.close();
                }
            }
            
            if (null == dcJobs || dcJobs.isEmpty())
            {
                continue;
            }
            
            for (JobInfo info : dcJobs)
            {
                info.setModel(info.getModel() + '[' + dc.getName() + ']');
                info.setClusterId(dc.getId());
            }
            
            jobInfos.addAll(dcJobs);
        }
        
        return jobInfos;
    }
    
    @Override
    @MethodLogAble(newLogId = true)
    public List<ThriftJobExecuteRecord> listJobExecuteRecord(JobInfo jobInfo) throws TException
    {
        if (isUfm(jobInfo))
        {
            return super.listJobExecuteRecord(jobInfo);
        }
        JobThriftServiceClient client = null;
        try
        {
            client = getClient(jobInfo);
            return client.listJobExecuteRecord(jobInfo);
        }
        finally
        {
            if (null != client)
            {
                client.close();
            }
        }
    }
    
    @Override
    @MethodLogAble(newLogId = true)
    public void startJob(JobInfo jobInfo) throws TException
    {
        if (isUfm(jobInfo))
        {
            super.startJob(jobInfo);
        }
        else
        {
            JobThriftServiceClient client = null;
            try
            {
                client = getClient(jobInfo);
                client.startJob(jobInfo);
            }
            finally
            {
                if (null != client)
                {
                    client.close();
                }
            }
        }
    }
    
    @Override
    @MethodLogAble(newLogId = true)
    public void stopJob(JobInfo jobInfo) throws TException
    {
        if (isUfm(jobInfo))
        {
            super.stopJob(jobInfo);
        }
        else
        {
            JobThriftServiceClient client = null;
            try
            {
                client = getClient(jobInfo);
                client.stopJob(jobInfo);
            }
            finally
            {
                if (null != client)
                {
                    client.close();
                }
            }
        }
    }
    
    @Override
    @MethodLogAble(newLogId = true)
    public void updateJob(String jobName, JobInfo jobInfo) throws TException
    {
        if (isUfm(jobInfo))
        {
            super.updateJob(jobName, jobInfo);
        }
        else
        {
            JobThriftServiceClient client = null;
            try
            {
                client = getClient(jobInfo);
                client.updateJob(jobName, jobInfo);
            }
            finally
            {
                if (null != client)
                {
                    client.close();
                }
            }
        }
    }
    
    private JobThriftServiceClient getClient(JobInfo jobInfo) throws TException
    {
        DataCenter dataCenter = dcService.getDataCenter(jobInfo.getClusterId());
        
        return getClient(dataCenter);
    }
    
    private JobThriftServiceClient getClient(DataCenter dataCenter) throws TException
    {
        if (dataCenter == null)
        {
            String message = "dataCenter is not exist";
            LOGGER.warn(message);
            throw new TException(message);
        }
        ResourceGroup group = dataCenter.getResourceGroup();
        
        // 选择一个可用的节点，发送激活请求
        String domain = dssDomainService.getDomainByDssId(group);
        return new JobThriftServiceClient(domain, group.getManagePort());
    }
    
    private boolean isUfm(JobInfo jobInfo)
    {
        return UFM_CLUSTER_ID == jobInfo.getClusterId();
    }
}
