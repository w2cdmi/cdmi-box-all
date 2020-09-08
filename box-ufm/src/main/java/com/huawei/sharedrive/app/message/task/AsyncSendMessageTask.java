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
package com.huawei.sharedrive.app.message.task;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.message.websocket.MessageListener;

import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.JobState;
import pw.cdmi.common.job.daemon.DaemonJobActiveUtils;
import pw.cdmi.common.job.daemon.DaemonJobTask;
import pw.cdmi.common.job.exception.JobException;

/**
 * 
 * @author s90006125
 * 
 */
@Service("asyncSendMessageTask")
public class AsyncSendMessageTask extends DaemonJobTask<String>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncSendMessageTask.class);
    
    private static final BlockingQueue<String> MESSAGE_QUEUE = new LinkedBlockingQueue<String>(20);
    
    @Autowired
    private DaemonJobActiveUtils daemonJobActiveUtils;
    
    @Override
    public void doTask(JobExecuteContext context, JobExecuteRecord record, String data)
    {
        List<Session> sessions = MessageListener.getAllSession();
        if (sessions.isEmpty())
        {
            LOGGER.warn("current is no session.");
            return;
        }
        
        for (Session session : sessions)
        {
            try
            {
                session.getBasicRemote().sendText(data);
            }
            catch (Exception e)
            {
                LOGGER.error("Notify client failed.", e);
            }
        }
    }
    
    @Override
    public String takeData() throws InterruptedException
    {
        return MESSAGE_QUEUE.take();
    }
    
    public void notice(String message)
    {
        if (this.getJobDefinition().getState() == JobState.STOP)
        {
            LOGGER.warn("job {} is stop.", this.getJobDefinition().getJobName());
            return;
        }
        try
        {
            MESSAGE_QUEUE.put(message);
        }
        catch (InterruptedException e)
        {
            LOGGER.error("add message Failed. [ {} ]", message);
        }
        
        try
        {
            daemonJobActiveUtils.activeJob(this.getJobDefinition().getJobName());
        }
        catch (JobException e)
        {
            LOGGER.error("active job failed.", e);
        }
    }
    
    @Override
    public boolean available(String data)
    {
        return true;
    }
    
}
