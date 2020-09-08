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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pw.cdmi.box.uam.log.dao.UserLoginLogDAO;
import pw.cdmi.box.uam.log.domain.UserLogListReq;
import pw.cdmi.box.uam.log.service.CheckLoginTimesAlarm;
import pw.cdmi.box.uam.system.service.LoginTimesService;
import pw.cdmi.common.domain.UserLoginTimesConfig;
import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;
import pw.cdmi.core.utils.DateUtils;

@Component("checkUserLoginTimesTask")
public class CheckUserLoginTimesTask extends QuartzJobTask
{
    private static final long serialVersionUID = -5543842093690117786L;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CheckUserLoginTimesTask.class);
    
    private static final String TABLE_PREFIX = "user_login_log_";
    
    @Autowired
    private UserLoginLogDAO userLoginLogDAO;
    
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
        long begainTime = currentTime - interval * 60 * 1000;
        long endTime = currentTime;
        List<String> tableList = getRecentTables();
        UserLogListReq req = new UserLogListReq();
        req.setBeginTime(begainTime / 1000);
        req.setEndTime(endTime / 1000);
        
        long total = 0;
        long tableMatchSize = 0;
        for (int i = 0; i < tableList.size(); i++)
        {
            try
            {
                tableMatchSize = userLoginLogDAO.getTotals(req, tableList.get(i));
            }
            catch (Exception e)
            {
                LOGGER.warn("doesn't exist:" + e.getMessage());
                continue;
            }
            total += tableMatchSize;
        }
        
        if (total >= threshold)
        {
            LOGGER.error("CheckUserLoginTimesTask, total:" + total + ",threshold:" + threshold);
            checkLoginTimesAlarm.sendUserLoginTimes(total, "CheckUserLoginTimesTask");
        }
    }
    
    private List<String> getRecentTables()
    {
        List<String> tableList = new ArrayList<String>(2);
        Calendar ca = Calendar.getInstance();
        String tableName;
        for (int i = 0; i < 2; i++)
        {
            tableName = TABLE_PREFIX
                + DateUtils.dateToString(ca.getTime(), UserLoginLogDAO.EVENT_LOG_DATE_PATTERN);
            tableList.add(tableName);
            ca.add(Calendar.DAY_OF_MONTH, -1);
        }
        return tableList;
    }
}
