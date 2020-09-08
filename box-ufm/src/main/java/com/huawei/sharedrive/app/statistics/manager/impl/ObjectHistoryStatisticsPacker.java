package com.huawei.sharedrive.app.statistics.manager.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.openapi.domain.statistics.ObjectHistoryStatisticsInfo;
import com.huawei.sharedrive.app.openapi.domain.statistics.ObjectHistoryStatisticsResponse;
import com.huawei.sharedrive.app.openapi.domain.statistics.TimePoint;
import com.huawei.sharedrive.app.statistics.domain.ObjectStatisticsDay;

/**
 * 历史节点统计数据封装器
 * 
 * @author l90003768
 * 
 */
@SuppressWarnings("unchecked")
public final class ObjectHistoryStatisticsPacker
{
    private ObjectHistoryStatisticsPacker()
    {
    }
    
    /**
     * 计算获取增量数据
     * 
     * @param list
     */
    public static void calculateAddedData(List<ObjectStatisticsDay> list)
    {
        if (list.isEmpty())
        {
            return;
        }
        ObjectStatisticsDay firstData = list.get(0);
        firstData.clearAddedData();
        int size = list.size();
        ObjectStatisticsDay beforeData = null;
        ObjectStatisticsDay nextData = null;
        for (int i = 0; i < size - 1; i++)
        {
            beforeData = list.get(i);
            nextData = list.get(i + 1);
            nextData.setAddedData(beforeData);
        }
    }
    
    public static Calendar getCalendar(int day)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, day / 10000);
        calendar.set(Calendar.MONTH, day % 10000 / 100 - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day % 100);
        return calendar;
    }
    
    /**
     * 获取每月的统计数据
     * 
     * @param list
     * @return
     */
    public static List<ObjectStatisticsDay> getMonthList(List<ObjectStatisticsDay> list)
    {
        if (list.isEmpty())
        {
            return Collections.EMPTY_LIST;
        }
        int size = list.size();
        List<ObjectStatisticsDay> monthList = new ArrayList<ObjectStatisticsDay>(10);
        ObjectStatisticsDay thisData = null;
        ObjectStatisticsDay nextData = null;
        for (int i = 0; i < size - 1; i++)
        {
            thisData = list.get(i);
            nextData = list.get(i + 1);
            if (!thisData.withSameDay(nextData))
            {
                monthList.add(thisData);
            }
            else
            {
            	nextData.setAddedData2(thisData);
            }
        }
        monthList.add(list.get(size - 1));
        return monthList;
    }
    
    /**
     * 获取每月的统计数据
     * 
     * @param list
     * @return
     */
    public static List<ObjectStatisticsDay> getSeasonList(List<ObjectStatisticsDay> list)
    {
        if (list.isEmpty())
        {
            return Collections.EMPTY_LIST;
        }
        int size = list.size();
        List<ObjectStatisticsDay> season = new ArrayList<ObjectStatisticsDay>(10);
        ObjectStatisticsDay thisData = null;
        ObjectStatisticsDay nextData = null;
        for (int i = 0; i < size - 1; i++)
        {
            thisData = list.get(i);
            nextData = list.get(i + 1);
            if (!thisData.withSameSeason(nextData))
            {
            	season.add(thisData);
            }
            else
            {
            	nextData.setAddedData2(thisData);
            }
        }
        season.add(list.get(size - 1));
        return season;
    }
    
    /**
     * 获取每月的统计数据
     * 
     * @param list
     * @return
     */
    public static List<ObjectStatisticsDay> getWeekList(List<ObjectStatisticsDay> list)
    {
        if (list.isEmpty())
        {
            return Collections.EMPTY_LIST;
        }
        int size = list.size();
        List<ObjectStatisticsDay> weekList = new ArrayList<ObjectStatisticsDay>(10);
        ObjectStatisticsDay thisData = null;
        ObjectStatisticsDay nextData = null;
        for (int i = 0; i < size - 1; i++)
        {
            thisData = list.get(i);
            nextData = list.get(i + 1);
            if (!thisData.withSameWeek(nextData))
            {
                weekList.add(thisData);
            }
            else
            {
            	nextData.setAddedData2(thisData);
            }
        }
        weekList.add(list.get(size - 1));
        return weekList;
    }
    
    /**
     * 获取每月的统计数据
     * 
     * @param list
     * @return
     */
    public static List<ObjectStatisticsDay> getYearList(List<ObjectStatisticsDay> list)
    {
        if (list.isEmpty())
        {
            return Collections.EMPTY_LIST;
        }
        int size = list.size();
        List<ObjectStatisticsDay> yearList = new ArrayList<ObjectStatisticsDay>(10);
        ObjectStatisticsDay thisData = null;
        ObjectStatisticsDay nextData = null;
        for (int i = 0; i < size - 1; i++)
        {
            thisData = list.get(i);
            nextData = list.get(i + 1);
            if (!thisData.withSameYear(nextData))
            {
                yearList.add(thisData);
            }
            else
            {
            	nextData.setAddedData2(thisData);
            }
        }
        yearList.add(list.get(size - 1));
        return yearList;
    }
    
    public static ObjectHistoryStatisticsResponse packObjectHistoryList(List<ObjectStatisticsDay> list,
        String interval) throws ParseException
    {
        List<ObjectHistoryStatisticsInfo> dataList = new ArrayList<ObjectHistoryStatisticsInfo>(10);
        if (TimePoint.INTERVAL_DAY.equalsIgnoreCase(interval))
        {
            for (ObjectStatisticsDay objectStatistics : list)
            {
                dataList.add(ObjectHistoryStatisticsInfo.convert(objectStatistics, interval));
            }
            if (!dataList.isEmpty())
            {
               // dataList.get(0).clearAddedData();
            }
        }
        else if (TimePoint.INTERVAL_WEEK.equalsIgnoreCase(interval))
        {
            List<ObjectStatisticsDay> monthList = getWeekList(list);
            //calculateAddedData(monthList);
            for (ObjectStatisticsDay objectStatistics : monthList)
            {
                dataList.add(ObjectHistoryStatisticsInfo.convert(objectStatistics, interval));
            }
        }
        else if (TimePoint.INTERVAL_MONTH.equalsIgnoreCase(interval))
        {
            List<ObjectStatisticsDay> monthList = getMonthList(list);
            //calculateAddedData(monthList);
            for (ObjectStatisticsDay objectStatistics : monthList)
            {
                dataList.add(ObjectHistoryStatisticsInfo.convert(objectStatistics, interval));
            }
        }
        else if (TimePoint.INTERVAL_SEANSON.equalsIgnoreCase(interval))
        {
            List<ObjectStatisticsDay> monthList = getSeasonList(list);
            //calculateAddedData(monthList);
            for (ObjectStatisticsDay objectStatistics : monthList)
            {
                dataList.add(ObjectHistoryStatisticsInfo.convert(objectStatistics, interval));
            }
        }
        else if (TimePoint.INTERVAL_YEAR.equalsIgnoreCase(interval))
        {
            List<ObjectStatisticsDay> monthList = getYearList(list);
            //calculateAddedData(monthList);
            for (ObjectStatisticsDay objectStatistics : monthList)
            {
                dataList.add(ObjectHistoryStatisticsInfo.convert(objectStatistics, interval));
            }
        }
        else
        {
            throw new InvalidParamException("Error interval");
        }
        resetSizeMb(dataList);
        ObjectHistoryStatisticsResponse response = new ObjectHistoryStatisticsResponse(dataList);
        return response;
    }
    
    private static void resetSizeMb(List<ObjectHistoryStatisticsInfo> dataList)
    {
        for (ObjectHistoryStatisticsInfo objectStatis : dataList)
        {
            objectStatis.resetSizeMb();
        }
    }
}
