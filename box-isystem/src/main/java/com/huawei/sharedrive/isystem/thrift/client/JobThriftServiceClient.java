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
package com.huawei.sharedrive.isystem.thrift.client;

import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransport;

import com.huawei.sharedrive.common.job.thrift.JobInfo;
import com.huawei.sharedrive.common.job.thrift.JobThriftService;
import com.huawei.sharedrive.common.job.thrift.ThriftJobExecuteRecord;

import pw.cdmi.common.thrift.client.AbstractThriftClient;

/**
 * 
 * @author s90006125
 *         
 */
public class JobThriftServiceClient extends AbstractThriftClient implements JobThriftService.Iface
{
    private static final String SERVICE_NAME = "JobThriftService";
    
    private JobThriftService.Client client;
    
    public JobThriftServiceClient(TTransport transport)
    {
        super(transport, SERVICE_NAME);
        client = new JobThriftService.Client(getProtocol());
    }
    
    @Override
    public JobInfo createJob(JobInfo jobInfo) throws TException
    {
        return this.client.createJob(jobInfo);
    }
    
    @Override
    public List<JobInfo> listAllJobDetail() throws TException
    {
        return this.client.listAllJobDetail();
    }
    
    @Override
    public List<ThriftJobExecuteRecord> listJobExecuteRecord(JobInfo jobInfo) throws TException
    {
        return this.client.listJobExecuteRecord(jobInfo);
    }
    
    @Override
    public void startJob(JobInfo jobInfo) throws TException
    {
        this.client.startJob(jobInfo);
    }
    
    @Override
    public void stopJob(JobInfo jobInfo) throws TException
    {
        this.client.stopJob(jobInfo);
    }
    
    @Override
    public void updateJob(String jobName, JobInfo jobInfo) throws TException
    {
        this.client.updateJob(jobName, jobInfo);
    }
    
}
