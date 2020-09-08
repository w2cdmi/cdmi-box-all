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
package pw.cdmi.box.uam.message.task;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pw.cdmi.box.uam.message.service.MessageService;
import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;

/**
 * 
 */
@Service("expiredMsgCleanTask")
public class ExpiredMsgCleanTask extends QuartzJobTask
{
    private static final long serialVersionUID = 1092653626034176154L;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ExpiredMsgCleanTask.class);
    
    private Integer messageStatusTableNum = null;
    
    @Autowired
    private MessageService messageService;
    
    @Override
    public void doTask(JobExecuteContext context, JobExecuteRecord record)
    {
        Date now = new Date();
        
        LOGGER.info("start clean expired message.");
        
        int tableNum = this.getMessageStatusTableNum();
        for (int i = 0; i < tableNum; i++)
        {
            try
            {
                messageService.cleanExpiredMessageStatus(now, i);
            }
            catch (Exception e)
            {
                String message = "clear table system_message_status_" + i + " failed.";
                LOGGER.error(message, e);
                record.setSuccess(false);
                record.setOutput(StringUtils.trimToEmpty(record.getOutput()) + ';' + message);
            }
        }
        
        try
        {
            messageService.cleanExpiredMessage(now);
        }
        catch (Exception e)
        {
            String message = "clear table system_message failed.";
            LOGGER.error(message, e);
            record.setSuccess(false);
            record.setOutput(StringUtils.trimToEmpty(record.getOutput()) + ';' + message);
        }
        
        LOGGER.info("end clean expired message.");
    }
    
    private Integer getMessageStatusTableNum()
    {
        if (null == this.messageStatusTableNum)
        {
            this.messageStatusTableNum = Integer.parseInt(this.getParameter());
        }
        
        return messageStatusTableNum;
    }
}
