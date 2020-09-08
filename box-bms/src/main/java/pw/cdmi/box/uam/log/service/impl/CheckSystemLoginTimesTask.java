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

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pw.cdmi.box.uam.log.dao.SystemLoginLogDao;
import pw.cdmi.box.uam.log.domain.QueryCondition;
import pw.cdmi.box.uam.log.service.CheckLoginTimesAlarm;
import pw.cdmi.box.uam.system.service.LoginTimesService;
import pw.cdmi.common.domain.UserLoginTimesConfig;
import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;

@Component("checkSystemLoginTimesTask")
public class CheckSystemLoginTimesTask extends QuartzJobTask
{
    private static final long serialVersionUID = -7392437643086848919L;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CheckSystemLoginTimesTask.class);
    
    @Autowired
    private SystemLoginLogDao systemLoginLogDao;
    
    @Autowired
    private LoginTimesService loginTimesService;
    
    @Autowired
    private CheckLoginTimesAlarm checkLoginTimesAlarm;
    
    @Override
    public void doTask(JobExecuteContext context, JobExecuteRecord record)
    {
        try
        {
            checkLoginTimes();
        }
        catch (Exception e)
        {
            String message = "checkLoginTimes failed. [ " + e.getMessage() + " ]";
            LOGGER.warn(message, e);
            record.setSuccess(false);
            record.setOutput(message);
            throw e;
        }
    }
    
    private void checkLoginTimes()
    {
        UserLoginTimesConfig config = loginTimesService.getUserLoginTimesConfig();
        
        if (config == null)
        {
            return;
        }
        int threshold = config.getThreshold();
        int interval = config.getInterval();
        long currentTime = System.currentTimeMillis();
        long begainTime = currentTime - 1000L * 60 * interval;
        long endTime = currentTime;
        QueryCondition filter = new QueryCondition();
        filter.setStartTime(new Date(begainTime));
        filter.setEndTime(new Date(endTime));
        long tableMatchSize = 0L;
        try
        {
            tableMatchSize = systemLoginLogDao.getFilterdCount(filter);
        }
        catch (Exception e)
        {
            LOGGER.warn("doesn't exist:" + e.getMessage());
            return;
        }
        
        if (tableMatchSize >= threshold)
        {
            LOGGER.error("CheckSystemLoginTimesTask, total:" + tableMatchSize + ",threshold:" + threshold);
            checkLoginTimesAlarm.sendSystemLoginTimes(tableMatchSize, "CheckSystemLoginTimesTask");
            
        }
    }
}
