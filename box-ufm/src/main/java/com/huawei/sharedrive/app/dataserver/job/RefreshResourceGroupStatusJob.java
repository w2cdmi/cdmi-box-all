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
package com.huawei.sharedrive.app.dataserver.job;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.dataserver.domain.ResourceGroup;
import com.huawei.sharedrive.app.dataserver.service.ResourceGroupService;
import com.huawei.sharedrive.app.utils.PropertiesUtils;

import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;

/**
 * 刷新资源组状态<br>
 * 该任务为集群任务，用于轮询各资源组状态
 * 
 * @author s90006125
 * 
 */
@Component("refreshResourceGroupStatusJob")
public class RefreshResourceGroupStatusJob extends QuartzJobTask
{
    private static final Logger LOGGER = LoggerFactory.getLogger(RefreshResourceGroupStatusJob.class);
    
    /** 资源组上报超时时长 */
    @Value("${cluster.resourcegroup.report.timeout}")
    private long resourceGroupTimeout = 60000L;
    
    @Autowired
    private ResourceGroupService resourceGroupService;
    
    private static final int MAX_QUEUE_SIZE = getMaxQueueSize();
    
    private BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(MAX_QUEUE_SIZE);
    
    private static final int MAX_QUEUE_WORKERS = getMaxQueueWorkers();
    
    private ThreadPoolExecutor pool = null;
    
    @PostConstruct
    public void init()
    {
        pool = new ThreadPoolExecutor(MAX_QUEUE_WORKERS, MAX_QUEUE_WORKERS, 1, TimeUnit.MINUTES, queue);
    }
    
    @PreDestroy
    public void destory()
    {
        if (pool != null)
        {
            pool.shutdown();
        }
    }
    
    private static int getMaxQueueSize()
    {
        return Integer.parseInt(PropertiesUtils.getProperty("resource.group.refresh.queue.size", "10"));
    }
    
    private static int getMaxQueueWorkers()
    {
        return Integer.parseInt(PropertiesUtils.getProperty("resource.group.refresh.workers",
            String.valueOf("5")));
        
    }
    
    private void refresh()
    {
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("Start Refresh ResourceGroup Status.");
        }
        
        List<ResourceGroup> list = resourceGroupService.listAllGroups();
        if (null == list)
        {
            return;
        }
        RefreshResourceGroupTask task = null;
        for (ResourceGroup group : list)
        {
            task = new RefreshResourceGroupTask(group, resourceGroupTimeout);
            pool.execute(task);
        }
        

    }
    
    @Override
    public void doTask(JobExecuteContext context, JobExecuteRecord record)
    {
        try
        {
            refresh();
        }
        catch (Exception e)
        {
            String message = "refresh resource group failed. [ " + e.getMessage() + " ]";
            LOGGER.warn(message, e);
            record.setSuccess(false);
            record.setOutput(message);
            throw e;
        }
    }
 
}
