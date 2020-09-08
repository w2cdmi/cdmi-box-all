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
package pw.cdmi.box.uam.log.service.impl;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pw.cdmi.box.uam.log.dao.UserLoginLogDAO;
import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;

@Component("userLoginLogDropTablesTask")
public class UserLoginLogDropTablesTask extends QuartzJobTask
{
    private static final long serialVersionUID = 5196790117481361893L;
    
    private static final int MAX_TABLE_DELETE_INDEX = 10;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UserLoginLogDropTablesTask.class);
    
    @Autowired
    private UserLoginLogDAO userLoginLogDAO;
    
    @Override
    public void doTask(JobExecuteContext context, JobExecuteRecord record)
    {
        try
        {
            dropTables(this.getParameter());
        }
        catch (Exception e)
        {
            String message = "drop user_login_log table failed. [ " + e.getMessage() + " ]";
            LOGGER.warn(message, e);
            record.setSuccess(false);
            record.setOutput(message);
            throw e;
        }
    }
    
    private void dropTables(String days)
    {
        LOGGER.info("Start dropTables, days:" + days);
        int drop = Integer.parseInt(days);
        Calendar ca = Calendar.getInstance();
        userLoginLogDAO.createTable(ca.getTime());
        for (int i = drop; i < MAX_TABLE_DELETE_INDEX; i++)
        {
            ca.add(Calendar.DAY_OF_MONTH, -i);
            userLoginLogDAO.dropTable(ca.getTime());
        }
        LOGGER.info("dropTables complement.");
    }
}
