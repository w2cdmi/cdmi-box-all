package com.huawei.sharedrive.app.log.service.impl;

import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.huawei.sharedrive.app.log.service.UserLogService;

import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;
import pw.cdmi.common.log.UserLog;

/**
 * 从临时缓存日志文件读取数据写入数据库任务
 * 
 * @author l90003768
 * 
 */
public class WriteLogDbJob extends QuartzJobTask
{
    
    private static Logger logger = LoggerFactory.getLogger(WriteLogDbJob.class);
    
    @Autowired
    private UserLogService userLogService;
    
    public void write()
    {
        try
        {
            LinkedList<UserLog> userLogList = null;
            do
            {
                try
                {
                    userLogList = (LinkedList<UserLog>) LoggerCacheReader.readEventList();
                }
                catch (FileNotFoundException e)
                {
                    logger.warn("Can not find the temp log file. ");
                    if (LoggerCacheReader.findNewLoggerFile())
                    {
                        LoggerCacheReader.switchToNextReadFile(false);
                        continue;
                    }
                    break;
                }
                catch (LoggerReadingException e)
                {
                    logger.warn(e.getMessage());
                    break;
                }
                catch (Exception e)
                {
                    logger.error("", e);
                }
                if (!CollectionUtils.isEmpty(userLogList))
                {
                    batchWriteItems(userLogList);
                }
                LoggerCacheReader.switchToNextReadFile(true);
            } while (true);
        }
        catch (RuntimeException e)
        {
            logger.error("", e);
        }
        
    }
    
    private void batchWriteItems(List<UserLog> userLogList)
    {
        try
        {
            userLogService.batchWriteItems(userLogList);
        }
        catch (Exception e)
        {
            for (UserLog userLog : userLogList)
            {
                try
                {
                    userLogService.batchWriteItem(userLog);
                }
                catch (Exception e2)
                {
                    if (e2.getMessage().contains("Duplicate entry"))
                    {
                        logger.debug("Log has been recorded");
                    }
                    else
                    {
                        logger.warn("Fail to record log of message:" + userLog.getKeyword(), e2);
                    }
                }
            }
        }
    }
    
    @Override
    public void doTask(JobExecuteContext context, JobExecuteRecord record)
    {
        write();
    }
}
