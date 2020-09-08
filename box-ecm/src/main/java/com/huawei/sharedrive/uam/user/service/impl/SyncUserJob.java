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
package com.huawei.sharedrive.uam.user.service.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.enterpriseadminlog.domain.LogOwner;
import com.huawei.sharedrive.uam.enterpriseuser.manager.SyncEnterpriseUserManager;

import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;

@Service("syncUserJob")
public class SyncUserJob extends QuartzJobTask
{
    private static final long serialVersionUID = 1944096201515510384L;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncUserJob.class);
    
    @Autowired
    private SyncEnterpriseUserManager syncEnterpriseUserManager;
    
    @Override
    public void doTask(JobExecuteContext context, JobExecuteRecord record)
    {
        String authServerId = this.getParameter();
        LOGGER.info("sync user task begin");
        try
        {
            
            LogOwner logOwner = new LogOwner();
            InetAddress addr = InetAddress.getLocalHost();
            logOwner.setIp(addr.getHostAddress());
            logOwner.setLoginName(this.getJobDefinition().getJobName());
            
            syncEnterpriseUserManager.syncEnterpriseUser(Long.valueOf(authServerId), logOwner);
            LOGGER.info("sync user task end");
        }
        catch (UnknownHostException e)
        {
            LOGGER.info("sync user task fail:" + e.getMessage());
        }
    }
    
}
