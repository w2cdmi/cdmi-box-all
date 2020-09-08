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
package com.huawei.sharedrive.uam.log.service.impl;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.log.dao.UserLogDAO;

import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;

@Component("createUserLogTablesTask")
public class CreateUserLogTablesTask extends QuartzJobTask
{
    private static final long serialVersionUID = 7974664678774563257L;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateUserLogTablesTask.class);
    
    @Autowired
    private UserLogDAO userLogDAO;
    
    @Override
    public void doTask(JobExecuteContext context, JobExecuteRecord record)
    {
        try
        {
            createTables(this.getParameter());
        }
        catch (Exception e)
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
        LOGGER.info("Start createTables, days:" + days);
        int remain = Integer.parseInt(days);
        Calendar ca = Calendar.getInstance();
        userLogDAO.createTable(ca.getTime());
        for (int i = 1; i < remain; i++)
        {
            ca.add(Calendar.DAY_OF_MONTH, 1);
            userLogDAO.createTable(ca.getTime());
        }
        LOGGER.info("createTables complement.");
    }
}
