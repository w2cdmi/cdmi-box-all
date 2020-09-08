package com.huawei.sharedrive.app.system.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.app.dataserver.exception.BusinessException;
import com.huawei.sharedrive.app.system.service.impl.LicenseServiceImpl;
import com.huawei.sharedrive.common.license.CseLicenseConstants;
import com.huawei.sharedrive.common.license.CseLicenseInfo;

import pw.cdmi.common.alarm.AlarmHelper;
import pw.cdmi.common.alarm.common.LicenseOvertimeAlarm;
import pw.cdmi.common.alarm.common.LicenseTeamNumberLimitAlarm;
import pw.cdmi.common.alarm.common.LicenseUserNumberLimitAlarm;
import pw.cdmi.common.alarm.common.LicenseValidateAlarm;
import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;

public class LicenseAlarmHelper extends QuartzJobTask
{
    private static Logger logger = LoggerFactory.getLogger(LicenseAlarmHelper.class);
    
    private LicenseOvertimeAlarm licenseOvertimeAlarm;
    
    private LicenseUserNumberLimitAlarm licenseUserNumberLimitAlarm;
    
    private LicenseTeamNumberLimitAlarm licenseTeamNumberLimitAlarm;
    
    private LicenseValidateAlarm licenseValidateAlarm;
    
    private AlarmHelper alarmHelper;
    
    /**
     * 检查License是否即将到期
     */
    public void checkExpirationTime()
    {
        CseLicenseInfo currentLicense = LicenseServiceImpl.currentLicense();
        if(null == currentLicense)
        {
            logger.info("Current license is null");
            return;
        }
        if(!StringUtils.equals(currentLicense.getDeadline(), CseLicenseConstants.PERMANENT))
        {
            Calendar deadline = getDateFromString(currentLicense.getDeadline());
            if(null == deadline)
            {
                logger.info("deadline is null.");
                return;
            }
            Calendar now = Calendar.getInstance();
            deadline.add(Calendar.DAY_OF_YEAR, 0 - licenseOvertimeAlarm.getObligateDate());
            LicenseOvertimeAlarm newAlarm = new LicenseOvertimeAlarm(licenseOvertimeAlarm, currentLicense.getDeadline());
            if(deadline.before(now))
            {
                logger.info("Send LicenseOvertimeAlarm, deadline is [ " + currentLicense.getDeadline() + " ]");
                alarmHelper.sendAlarm(newAlarm);
            }
            else
            {
                alarmHelper.sendRecoverAlarm(newAlarm);
            }
        }
        else
        {
            logger.info(currentLicense.getDeadline());
            alarmHelper.sendRecoverAlarm(licenseOvertimeAlarm);
        }
    }
    
    /**
     * 检查用户数是否已到阈值
     * @param currentUsers
     * @param maxUsers
     */
    public void checkUsers(int currentUsers, int maxUsers)
    {
        float threshold = licenseUserNumberLimitAlarm.getThreshold();
        int max = (int) (maxUsers * threshold);
        LicenseUserNumberLimitAlarm newAlarm = new LicenseUserNumberLimitAlarm(licenseUserNumberLimitAlarm, currentUsers, maxUsers);
        newAlarm.getAlarmReport().setM_strParamterValue(newAlarm.getParameter());
        if(currentUsers >= max)
        {
            logger.info(newAlarm.toString());
            logger.info("Send licenseUserNumberLimitAlarm, currentUsers is [ " + currentUsers + ", maxUsers is " + maxUsers + " ]");
            alarmHelper.sendAlarm(newAlarm);
        }
        else
        {
            alarmHelper.sendRecoverAlarm(newAlarm);
        }
    }
    
    public void checkValidate(CseLicenseInfo cseLicenseInfo)
    {
        if(null == cseLicenseInfo)
        {
            logger.info("Send licenseValidateAlarm,");
            alarmHelper.sendAlarm(licenseValidateAlarm);
        }
        else
        {
            alarmHelper.sendRecoverAlarm(licenseValidateAlarm);
        }
    }
    
    /**
     * 检查用户数是否已到阈值
     * @param currentTeams
     * @param maxTeams
     */
    public void checkTeams(long currentTeams, long maxTeams)
    {
        float threshold = licenseTeamNumberLimitAlarm.getThreshold();
        int max = (int) (maxTeams * threshold);
        LicenseTeamNumberLimitAlarm newAlarm = new LicenseTeamNumberLimitAlarm(licenseTeamNumberLimitAlarm, currentTeams, maxTeams);
        if(currentTeams >= max)
        {
            logger.info("Send licenseTeamNumberLimitAlarm, currentTeams is [ " + currentTeams + ", maxTeams is " + max + " ]");
            alarmHelper.sendAlarm(newAlarm);
        }
        else
        {
            alarmHelper.sendRecoverAlarm(newAlarm);
        }
    }
    
    private static Calendar getDateFromString(String str)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        try
        {
            calendar.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(str));
        }
        catch (Exception e)
        {
            return null;
        }
        return calendar;
    }

    public LicenseOvertimeAlarm getLicenseOvertimeAlarm()
    {
        return licenseOvertimeAlarm;
    }

    public void setLicenseOvertimeAlarm(LicenseOvertimeAlarm licenseOvertimeAlarm)
    {
        this.licenseOvertimeAlarm = licenseOvertimeAlarm;
    }

    public LicenseUserNumberLimitAlarm getLicenseUserNumberLimitAlarm()
    {
        return licenseUserNumberLimitAlarm;
    }

    public void setLicenseUserNumberLimitAlarm(LicenseUserNumberLimitAlarm licenseUserNumberLimitAlarm)
    {
        this.licenseUserNumberLimitAlarm = licenseUserNumberLimitAlarm;
    }

    public LicenseTeamNumberLimitAlarm getLicenseTeamNumberLimitAlarm()
    {
        return licenseTeamNumberLimitAlarm;
    }

    public void setLicenseTeamNumberLimitAlarm(LicenseTeamNumberLimitAlarm licenseTeamNumberLimitAlarm)
    {
        this.licenseTeamNumberLimitAlarm = licenseTeamNumberLimitAlarm;
    }

    public AlarmHelper getAlarmHelper()
    {
        return alarmHelper;
    }

    public void setAlarmHelper(AlarmHelper alarmHelper)
    {
        this.alarmHelper = alarmHelper;
    }

    /**
     * 定时任务框架，定时检查license
     */
    @Override
    public void doTask(JobExecuteContext context, JobExecuteRecord record)
    {
        try
        {
            checkExpirationTime();
        }
        catch(BusinessException e)
        {
            String message = "checkExpirationTime failed. [ " + e.getMessage() + " ]";
            logger.warn(message, e);
            record.setSuccess(false);
            record.setOutput(message);
        }
    }

    public LicenseValidateAlarm getLicenseValidateAlarm()
    {
        return licenseValidateAlarm;
    }

    public void setLicenseValidateAlarm(LicenseValidateAlarm licenseValidateAlarm)
    {
        this.licenseValidateAlarm = licenseValidateAlarm;
    }
}
