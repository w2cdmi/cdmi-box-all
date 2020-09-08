package com.huawei.sharedrive.app.statistics.job;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.openapi.domain.statistics.UserCurrentStatisticsInfo;
import com.huawei.sharedrive.app.statistics.dao.UserStatisticsDao;
import com.huawei.sharedrive.app.statistics.domain.UserStatisticsDay;
import com.huawei.sharedrive.app.statistics.service.StatisticsDateUtils;
import com.huawei.sharedrive.app.statistics.service.UsersStatisticsService;

import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;

@Component("userStatisticsJob")
public class UserStatisticsJob extends QuartzJobTask
{
    /**
     * 日志记录器
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UserStatisticsJob.class);
    
    @Autowired
    private UserStatisticsDao userStatisticsDao;
    
    @Autowired
    private UsersStatisticsService usersStatisticsService;
    
    @Override
    public void doTask(JobExecuteContext context, JobExecuteRecord record)
    {
        try
        {
            insertStatisticsData();
        }
        catch (Exception e)
        {
            String message = "insertStatisticsData failed. [ " + e.getMessage() + " ]";
            LOGGER.warn(message, e);
            record.setSuccess(false);
            record.setOutput(message);
            throw e;
        }
    }
    
    private void insertStatisticsData()
    {
        Integer newDayId = StatisticsDateUtils.getDay();
        
        Integer oldDayId = StatisticsDateUtils.getLastDay(newDayId);
        
        List<UserStatisticsDay> historylist = userStatisticsDao.getFilterHistoryDays(oldDayId, null, null);
        
        List<UserCurrentStatisticsInfo> list = usersStatisticsService.getUserCurrentStatistics(UserStatisticsDao.TYPE_GROUPBY_ALL,
            null,
            null);
        
        UserStatisticsDay newItem = null;
        
        for (UserCurrentStatisticsInfo info : list)
        {
            newItem = new UserStatisticsDay();
            newItem.setDay(newDayId);
            newItem.setAppId(info.getAppId());
            newItem.setRegionId(info.getRegionId());
            newItem.setUserCount(info.getUserCount());
            newItem.setAddedUserCount(info.getUserCount());
            for (UserStatisticsDay hiItem : historylist)
            {
                if (StringUtils.equals(hiItem.getAppId(), info.getAppId())
                    && hiItem.getRegionId() == info.getRegionId())
                {
                    newItem.setAddedUserCount(info.getUserCount() - hiItem.getUserCount());
                    break;
                }
            }
            
            userStatisticsDao.addHistoryDay(newItem);
            
        }
    }
    
}
