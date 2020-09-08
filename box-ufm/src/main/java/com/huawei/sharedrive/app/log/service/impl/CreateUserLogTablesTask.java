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
package com.huawei.sharedrive.app.log.service.impl;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.log.dao.UserLogDAO;
import com.huawei.sharedrive.app.system.dao.SystemConfigDAO;

import pw.cdmi.common.domain.SystemConfig;
import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;
import pw.cdmi.core.utils.DateUtils;

/**
 * 
 * @author s90006125
 *
 */
@Component("createUserLogTablesTask")
public class CreateUserLogTablesTask extends QuartzJobTask
{
    private static final long serialVersionUID = -5904486553304115650L;
    
    public static long getSerialVersionUID()
    {
        return serialVersionUID;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateUserLogTablesTask.class);
    
    @Autowired
    private UserLogDAO userLogDAO;
    
    @Autowired
    private SystemConfigDAO systemConfigDAO;
    
    private static String earlyLogDay = null;
    
    private static final String KEY_EARLY_LOG_DAY = "userlog.first.name";
    
    private static final String TABLE_PREFIX = "user_log_";
    
    @Override
    public void doTask(JobExecuteContext context, JobExecuteRecord record)
    {
        try
        {
            createTables(this.getParameter());
        }
        catch(Exception e)
        {
            String message = "create user_log table failed. [ " + e.getMessage() + " ]";
            LOGGER.warn(message, e);
            record.setSuccess(false);
            record.setOutput(message);
            throw e;
        }
    }
    
    public void createTables(String days)
    {
        int remain = Integer.parseInt(days);
        Calendar ca = Calendar.getInstance();
        userLogDAO.createTable(ca.getTime());
        // 循环创建指定日期的日志表
        for (int i = 1; i < remain; i++)
        {
            ca.add(Calendar.DAY_OF_MONTH, 1);
            userLogDAO.createTable(ca.getTime());
            if (null == earlyLogDay)
            {
                SystemConfig item = systemConfigDAO.get(KEY_EARLY_LOG_DAY);
                if (item == null)
                {
                    item = new SystemConfig("", KEY_EARLY_LOG_DAY, TABLE_PREFIX
                        + DateUtils.dateToString(ca.getTime(), UserLogDAO.EVENT_LOG_DATE_PATTERN));
                    systemConfigDAO.create(item);
                    setEarlyLogDay(ca);
                }
            }
        }
    }
    
    private static void setEarlyLogDay(Calendar ca)
    {
        earlyLogDay = TABLE_PREFIX
            + DateUtils.dateToString(ca.getTime(), UserLogDAO.EVENT_LOG_DATE_PATTERN);
    }
}
