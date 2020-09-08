package com.huawei.sharedrive.uam.system.statistic;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.authapp.service.AuthAppService;
import com.huawei.sharedrive.uam.system.statistic.dao.SystemStatDAO;
import com.huawei.sharedrive.uam.system.statistic.domain.SystemStat;
import com.huawei.sharedrive.uam.terminal.service.TerminalService;
import com.huawei.sharedrive.uam.user.service.UserService;

import pw.cdmi.common.domain.Terminal;
import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;
import pw.cdmi.uam.domain.AuthApp;

@Service("systemStatTask")
public class SystemStatTask extends QuartzJobTask
{
    private static final long serialVersionUID = 7344823941636379131L;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SystemStatTask.class);
    
    @PostConstruct
    public void init()
    {
    }
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AuthAppService authAppService;
    
    @Autowired
    private TerminalService terminalService;
    
    @Autowired
    private SystemStatDAO systemStatDAO;
    
    private long getLoginUserCountByWeb(String appId, Date beginDate, Date endDate)
    {
        return terminalService.getAgentCountByLoginAt(appId, Terminal.CLIENT_TYPE_IOS, beginDate, endDate);
    }
    
    private long getLoginUserCountByAndroidMobile(String appId, Date beginDate, Date endDate)
    {
        return terminalService.getAgentCountByLoginAt(appId, Terminal.CLIENT_TYPE_ANDROID, beginDate, endDate);
    }
    
    private long getLoginUserCountByIOSMobile(String appId, Date beginDate, Date endDate)
    {
        return terminalService.getAgentCountByLoginAt(appId, Terminal.CLIENT_TYPE_IOS, beginDate, endDate);
    }
    
    private long getLoginUserCountByPC(String appId, Date beginDate, Date endDate)
    {
        return terminalService.getAgentCountByLoginAt(appId, Terminal.CLIENT_TYPE_PC, beginDate, endDate);
    }
    
    private long getUserTotalByAppID(String appId)
    {
        return userService.getAppUserTotal(appId);
    }
    
    private long getAppUserLoginTotal(String appId, Date beginDate, Date curDate)
    {
        return userService.getAppUserLoginTotal(appId, beginDate, curDate);
    }
    
    public void createSystemStat(SystemStat stat)
    {
        systemStatDAO.create(stat);
    }
    
    @Override
    public void doTask(JobExecuteContext context, JobExecuteRecord record)
    {
        try
        {
            List<AuthApp> lstApp = authAppService.getAuthAppList(null, null, null);
            
            if (null == lstApp || lstApp.isEmpty())
            {
                return;
            }
            Calendar curCalendar = Calendar.getInstance();
            curCalendar.set(Calendar.HOUR_OF_DAY, 0);
            curCalendar.set(Calendar.SECOND, 0);
            curCalendar.set(Calendar.MINUTE, 0);
            Date curDate = curCalendar.getTime();
            Calendar preCalendar = Calendar.getInstance();
            preCalendar.setTime(curDate);
            preCalendar.add(Calendar.DAY_OF_MONTH, -1);
            Date beginDate = preCalendar.getTime();
            SystemStat stat;
            Date now = new Date();
            for (AuthApp app : lstApp)
            {
                stat = new SystemStat();
                stat.setStatDate(beginDate);
                stat.setAppId(app.getAuthAppId());
                stat.setTotalUser(getUserTotalByAppID(app.getAuthAppId()));
                stat.setLoginUserCount(getAppUserLoginTotal(app.getAuthAppId(), beginDate, curDate));
                stat.setWebAccessAgentCount(getLoginUserCountByWeb(app.getAuthAppId(), beginDate, curDate));
                stat.setPcAccessAgentCount(getLoginUserCountByPC(app.getAuthAppId(), beginDate, curDate));
                stat.setAndroidAccessCount(getLoginUserCountByAndroidMobile(app.getAuthAppId(),
                    beginDate,
                    curDate));
                stat.setCreateDate(now);
                stat.setIosAccessCount(getLoginUserCountByIOSMobile(app.getAuthAppId(), beginDate, curDate));
                systemStatDAO.create(stat);
            }
            
        }
        catch (Exception e)
        {
            LOGGER.error(e.getMessage(), e);
        }
    }
    
    public String getName()
    {
        
        return SystemStatTask.class.getCanonicalName();
    }
}
