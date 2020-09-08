package com.huawei.sharedrive.app.statistics.service.impl;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.InternalServerErrorException;
import com.huawei.sharedrive.app.openapi.domain.statistics.MilestioneInfo;
import com.huawei.sharedrive.app.openapi.domain.statistics.RestUserCurrentStatisticsRequest;
import com.huawei.sharedrive.app.openapi.domain.statistics.RestUserHistoryStatisticsRequest;
import com.huawei.sharedrive.app.openapi.domain.statistics.TimePoint;
import com.huawei.sharedrive.app.openapi.domain.statistics.UserClusterStatisticsInfo;
import com.huawei.sharedrive.app.openapi.domain.statistics.UserCurrentStatisticsInfo;
import com.huawei.sharedrive.app.openapi.domain.statistics.UserHistoryStatisticsInfo;
import com.huawei.sharedrive.app.statistics.dao.UserStatisticsDao;
import com.huawei.sharedrive.app.statistics.domain.UserStatisticsDay;
import com.huawei.sharedrive.app.statistics.service.UsersStatisticsService;
import com.huawei.sharedrive.app.utils.BusinessConstants;

@Service("usersStatisticsService")
public class UsersStatisticsServiceImpl implements UsersStatisticsService
{
    private static final class MyComparator implements Comparator<MilestioneInfo>, Serializable
    {
        /**
         * 
         */
        private static final long serialVersionUID = -6051785567083004826L;
        
        @Override
        public int compare(MilestioneInfo arg0, MilestioneInfo arg1)
        {
            if (arg0.getMilestone() > arg1.getMilestone())
            {
                return 1;
            }
            if (arg0.getMilestone() < arg1.getMilestone())
            {
                return -1;
            }
            return 0;
        }
    }
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UsersStatisticsServiceImpl.class);
    
    private static final int MB_TO_BYTE = 1 * 1024 * 1024;
    
    @Autowired
    private UserStatisticsDao userStatisticsDao;
    
    @Override
    public List<UserCurrentStatisticsInfo> getUserCurrentStatistics(
        RestUserCurrentStatisticsRequest restStatistiscRequest)
    {
        return addAllTablesByRegionAndAppId(userStatisticsDao.getUserCurrentStatistics(restStatistiscRequest));
    }
    
    @Override
    public List<UserCurrentStatisticsInfo> getUserCurrentStatistics(String groupBy, Integer regionId,
        String appId) throws BaseRunException
    {
        return addAllTablesByRegionAndAppId(userStatisticsDao.getUserCurrentStatistics(groupBy,
            regionId,
            appId));
    }
    
    @Override
    public List<UserHistoryStatisticsInfo> getUserHistoryStatistics(
        RestUserHistoryStatisticsRequest restStatistiscRequest)
    {
        Integer beginDay = getDay(restStatistiscRequest.getBeginTime());
        Integer endDay = getDay(restStatistiscRequest.getEndTime());
        
        List<UserStatisticsDay> list = userStatisticsDao.getHistoryDaysByRange(beginDay,
            endDay,
            restStatistiscRequest.getRegionId(),
            restStatistiscRequest.getAppId());
        
        // "day", "week", "month", "season", "year"
        if ("day".equals(restStatistiscRequest.getInterval()))
        {
            list = getDayList(list, Calendar.DAY_OF_YEAR);
        }
        else if ("week".equals(restStatistiscRequest.getInterval()))
        {
            list = getDayList(list, Calendar.WEEK_OF_YEAR);
        }
        else if ("month".equals(restStatistiscRequest.getInterval()))
        {
            list = getDayList(list, Calendar.MONTH);
        }
        else if ("season".equals(restStatistiscRequest.getInterval()))
        {
            list = getSeasonDayList(list);
        }
        else if ("year".equals(restStatistiscRequest.getInterval()))
        {
            list = getDayList(list, Calendar.YEAR);
        }
        
        try
        {
            return transHistoryData(list, restStatistiscRequest.getInterval());
        }
        catch (ParseException e)
        {
            LOGGER.error("getUserHistoryStatistics fail", e);
            throw new InternalServerErrorException("ParseException", e);
        }
    }
    
    /**
     * 将历史统计数据按照每天相加（如：20150401这天的多个应用数据加在一起）
     * 
     * @param list
     * @param restStatistiscRequest
     * @return
     */
    private List<UserStatisticsDay> addHistoryDataToDays(List<UserStatisticsDay> list)
    {
        ArrayList<UserStatisticsDay> tempList = new ArrayList<UserStatisticsDay>(
            BusinessConstants.INITIAL_CAPACITIES);
        
        // 将一天的数据全部汇总
        boolean isFind = false;
        for (UserStatisticsDay info : list)
        {
            for (UserStatisticsDay item : tempList)
            {
                if (info.getDay() == item.getDay())
                {
                    item.setUserCount(item.getUserCount() + info.getUserCount());
                    item.setAddedUserCount(item.getAddedUserCount() + info.getAddedUserCount());
                    isFind = true;
                    break;
                }
            }
            if (!isFind)
            {
                tempList.add(info);
            }
            isFind = false;
        }
        return tempList;
    }
    
    /**
     * 将所有table中的数据按照appId和regionId进行统计
     * 
     * @param list
     * @return
     */
    private List<UserCurrentStatisticsInfo> addAllTablesByRegionAndAppId(List<UserCurrentStatisticsInfo> list)
    {
        ArrayList<UserCurrentStatisticsInfo> result = new ArrayList<UserCurrentStatisticsInfo>(
            BusinessConstants.INITIAL_CAPACITIES);
        
        boolean isFind = false;
        
        for (UserCurrentStatisticsInfo info : list)
        {
            for (UserCurrentStatisticsInfo item : result)
            {
                if (item.getRegionId() == null && info.getRegionId() == null
                    && StringUtils.equals(item.getAppId(), info.getAppId()))
                {
                    item.setUserCount(item.getUserCount() + info.getUserCount());
                    isFind = true;
                    break;
                }
                if (item.getRegionId() != null && info.getRegionId() != null
                    && item.getRegionId().intValue() == info.getRegionId().intValue()
                    && StringUtils.equals(item.getAppId(), info.getAppId()))
                {
                    item.setUserCount(item.getUserCount() + info.getUserCount());
                    isFind = true;
                    break;
                }
            }
            if (!isFind)
            {
                result.add(info);
            }
            isFind = false;
        }
        
        return result;
    }
    
    private Calendar getCalenderFromDay(Calendar caInfo, UserStatisticsDay info)
    {
        caInfo.set(Calendar.YEAR, info.getDay() / 10000);
        caInfo.set(Calendar.MONTH, (info.getDay() % 10000) / 100 - 1);
        caInfo.set(Calendar.DAY_OF_MONTH, info.getDay() % 100);
        return caInfo;
    }
    
    public static Integer getDay(Long dateTime)
    {
        Integer beginDay = null;
        if (dateTime != null)
        {
            Calendar ca = Calendar.getInstance();
            ca.setTime(new Date(dateTime));
            beginDay = ca.get(Calendar.YEAR) * 10000 + (ca.get(Calendar.MONTH) + 1) * 100
                + ca.get(Calendar.DAY_OF_MONTH);
        }
        return beginDay;
    }
    
    /**
     * 按天 周 月 年 统计方法
     * 
     * @param list
     * @param unit
     * @param restStatistiscRequest
     * @return
     */
    private List<UserStatisticsDay> getDayList(List<UserStatisticsDay> list, int unit)
    {
        List<UserStatisticsDay> tempList = addHistoryDataToDays(list);
        
        ArrayList<UserStatisticsDay> result = new ArrayList<UserStatisticsDay>(
            BusinessConstants.INITIAL_CAPACITIES);
        
        boolean isFind = false;
        for (UserStatisticsDay info : tempList)
        {
            for (UserStatisticsDay item : result)
            {
                if ((getCalenderFromDay(Calendar.getInstance(), item).get(unit) == getCalenderFromDay(Calendar.getInstance(),
                    info).get(unit))
                    && info.getDay() / 10000 == item.getDay() / 10000)
                {
                    item.setUserCount(info.getUserCount());
                    item.setAddedUserCount(item.getAddedUserCount() + info.getAddedUserCount());
                    isFind = true;
                    break;
                }
            }
            if (!isFind)
            {
                UserStatisticsDay temp = new UserStatisticsDay();
                temp.setDay(info.getDay());
                temp.setUserCount(info.getUserCount());
                temp.setAddedUserCount(info.getAddedUserCount());
                result.add(temp);
            }
            isFind = false;
        }
        
        return result;
    }
    
    /**
     * 季度数据特殊处理
     * 
     * @param list
     * @param restStatistiscRequest
     * @return
     */
    private List<UserStatisticsDay> getSeasonDayList(List<UserStatisticsDay> list)
    {
        List<UserStatisticsDay> tempList = addHistoryDataToDays(list);
        
        ArrayList<UserStatisticsDay> result = new ArrayList<UserStatisticsDay>(
            BusinessConstants.INITIAL_CAPACITIES);
        
        boolean isFind = false;
        for (UserStatisticsDay info : tempList)
        {
            
            for (UserStatisticsDay item : result)
            {
                
                // Calendar.MONTH 为 0~11 通过，除以3划分为4个季度，（0,1,2）（3,4,5）（6,7,8）（9,10,11）
                if ((getCalenderFromDay(Calendar.getInstance(), item).get(Calendar.MONTH) / 3 == getCalenderFromDay(Calendar.getInstance(),
                    info).get(Calendar.MONTH) / 3)
                    && (info.getDay() / 10000 == item.getDay() / 10000))
                {
                    item.setUserCount(info.getUserCount());
                    item.setAddedUserCount(item.getAddedUserCount() + info.getAddedUserCount());
                    isFind = true;
                    break;
                }
            }
            if (!isFind)
            {
                UserStatisticsDay temp = new UserStatisticsDay();
                temp.setDay(info.getDay());
                temp.setUserCount(info.getUserCount());
                temp.setAddedUserCount(info.getAddedUserCount());
                result.add(temp);
            }
            isFind = false;
        }
        
        return result;
    }
    
    private List<UserHistoryStatisticsInfo> transHistoryData(List<UserStatisticsDay> list, String interval)
        throws ParseException
    {
        ArrayList<UserHistoryStatisticsInfo> result = new ArrayList<UserHistoryStatisticsInfo>(
            BusinessConstants.INITIAL_CAPACITIES);
        
        UserHistoryStatisticsInfo temp = null;
        
        TimePoint timePoint = null;
        for (UserStatisticsDay info : list)
        {
            timePoint = TimePoint.convert(info.getDay(), interval);
            temp = new UserHistoryStatisticsInfo();
            temp.setAdded((int) info.getAddedUserCount());
            temp.setTimePoint(timePoint);
            temp.setUserCount(info.getUserCount());
            
            result.add(temp);
        }
        
        return result;
    }
    
    @Override
    public List<UserClusterStatisticsInfo> getUserClusterStatistics(List<MilestioneInfo> milestiones)
        throws BaseRunException
    {
        if (milestiones.isEmpty())
        {
            return new ArrayList<UserClusterStatisticsInfo>(0);
        }
        Collections.sort(milestiones, new MyComparator());
        
        ArrayList<UserClusterStatisticsInfo> list = new ArrayList<UserClusterStatisticsInfo>(10);
        long begin = 0L;
        long end = 0L;
        
        int size = milestiones.size();
        for (int i = 0; i < size; i++)
        {
            end = milestiones.get(i).getMilestone() * MB_TO_BYTE;
            if (i == 0)
            {
                begin = 0L;
            }
            else
            {
                begin = milestiones.get(i - 1).getMilestone() * MB_TO_BYTE;
            }
            list.add(userStatisticsDao.getClusterStatisticsInfo(begin, end));
        }
        begin = milestiones.get(milestiones.size() - 1).getMilestone() * MB_TO_BYTE;
        list.add(userStatisticsDao.getClusterStatisticsInfo(begin, null));
        return list;
    }
    
    @Override
    public List<UserStatisticsDay> getHistoryList(Integer beginTime, Integer endTime)
    {
        List<UserStatisticsDay> userStatisticsDayList = userStatisticsDao.getHistoryDaysByRange(beginTime,
            endTime,
            null,
            null);
        return userStatisticsDayList;
    }
}
