package com.huawei.sharedrive.app.statistics.manager.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.openapi.domain.statistics.NodeHistoryStatisticsInfo;
import com.huawei.sharedrive.app.openapi.domain.statistics.NodeHistoryStatisticsResponse;
import com.huawei.sharedrive.app.openapi.domain.statistics.TimePoint;
import com.huawei.sharedrive.app.statistics.domain.NodeStatisticsDay;

/**
 * 历史节点统计数据封装器
 * 
 * @author l90003768
 * 
 */
@SuppressWarnings("unchecked")
public final class NodeHistoryStatisticsPacker
{
    private NodeHistoryStatisticsPacker()
    {
    }
    
    /**
     * 判断该天是否同年同月靠前的天数
     * 
     * @param day
     * @param nextDay
     * @return
     */
    public static boolean boforeDayWithSameDay(long day, long nextDay)
    {
        long year = day / 10000;
        long nextYear = nextDay / 10000;
        if (year != nextYear)
        {
            return false;
        }
        long month = (day % 10000) / 100;
        long nextMonth = (nextDay % 10000) / 100;
        if (month != nextMonth)
        {
            return false;
        }
        if (day % 100 < nextDay % 100)
        {
            return true;
        }
        return false;
    }
    
    /**
     * 判断该天是否同年同一季度靠前的天数
     * 
     * @param day
     * @param nextDay
     * @return
     */
    public static boolean boforeDayWithSameSeason(long day, long nextDay)
    {
        long year = day / 10000;
        long nextYear = nextDay / 10000;
        if (year != nextYear)
        {
            return false;
        }
        
        Calendar thisDate = getCalendar((int) day);
        Calendar nextDate = getCalendar((int) nextDay);
        // Calendar.MONTH 为 0~11 通过，除以3划分为4个季度，（0,1,2）（3,4,5）（6,7,8）（9,10,11）
        int season1 = thisDate.get(Calendar.MONTH) / 3;
        int season2 = nextDate.get(Calendar.MONTH) / 3;
        if (season1 != season2)
        {
            return false;
        }
        if (thisDate.get(Calendar.DAY_OF_YEAR) < nextDate.get(Calendar.DAY_OF_YEAR))
        {
            return true;
        }
        return false;
    }
    
    /**
     * 判断该天是否同年同周靠前的天数
     * 
     * @param day
     * @param nextDay
     * @return
     */
    public static boolean boforeDayWithSameWeek(long day, long nextDay)
    {
        long year = day / 10000;
        long nextYear = nextDay / 10000;
        if (year != nextYear)
        {
            return false;
        }
        Calendar thisDate = getCalendar((int) day);
        Calendar nextDate = getCalendar((int) nextDay);
        if (thisDate.get(Calendar.WEEK_OF_YEAR) != nextDate.get(Calendar.WEEK_OF_YEAR))
        {
            return false;
        }
        if (thisDate.get(Calendar.DAY_OF_WEEK) < nextDate.get(Calendar.DAY_OF_WEEK))
        {
            return true;
        }
        return false;
    }
    
    /**
     * 判断该天是否同年同一季度靠前的天数
     * 
     * @param day
     * @param nextDay
     * @return
     */
    public static boolean boforeDayWithSameYear(long day, long nextDay)
    {
        long year = day / 10000;
        long nextYear = nextDay / 10000;
        if (year != nextYear)
        {
            return false;
        }
        if (day % 10000 < nextDay % 10000)
        {
            return true;
        }
        return false;
    }
    
    /**
     * 计算获取增量数据
     * 
     * @param list
     */
    public static void calculateAddedData(List<NodeStatisticsDay> list)
    {
        if (list.isEmpty())
        {
            return;
        }
        NodeStatisticsDay firstData = list.get(0);
        firstData.clearAddedData();
        int size = list.size();
        NodeStatisticsDay beforeData = null;
        NodeStatisticsDay nextData = null;
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
    public static List<NodeStatisticsDay> getMonthList(List<NodeStatisticsDay> list)
    {
        if (list.isEmpty())
        {
            return Collections.EMPTY_LIST;
        }
        int size = list.size();
        List<NodeStatisticsDay> monthList = new ArrayList<NodeStatisticsDay>(10);
        NodeStatisticsDay thisData = null;
        NodeStatisticsDay nextData = null;
        for (int i = 0; i < size - 1; i++)
        {
            thisData = list.get(i);
            nextData = list.get(i + 1);
            if (!boforeDayWithSameDay(thisData.getDay(), nextData.getDay()))
            {
                monthList.add(thisData);
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
    public static List<NodeStatisticsDay> getSeasonList(List<NodeStatisticsDay> list)
    {
        if (list.isEmpty())
        {
            return Collections.EMPTY_LIST;
        }
        int size = list.size();
        List<NodeStatisticsDay> season = new ArrayList<NodeStatisticsDay>(10);
        NodeStatisticsDay thisData = null;
        NodeStatisticsDay nextData = null;
        for (int i = 0; i < size - 1; i++)
        {
            thisData = list.get(i);
            nextData = list.get(i + 1);
            if (!boforeDayWithSameSeason(thisData.getDay(), nextData.getDay()))
            {
                season.add(thisData);
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
    public static List<NodeStatisticsDay> getWeekList(List<NodeStatisticsDay> list)
    {
        if (list.isEmpty())
        {
            return Collections.EMPTY_LIST;
        }
        int size = list.size();
        List<NodeStatisticsDay> weekList = new ArrayList<NodeStatisticsDay>(10);
        NodeStatisticsDay thisData = null;
        NodeStatisticsDay nextData = null;
        for (int i = 0; i < size - 1; i++)
        {
            thisData = list.get(i);
            nextData = list.get(i + 1);
            if (!boforeDayWithSameWeek(thisData.getDay(), nextData.getDay()))
            {
                weekList.add(thisData);
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
    public static List<NodeStatisticsDay> getYearList(List<NodeStatisticsDay> list)
    {
        if (list.isEmpty())
        {
            return Collections.EMPTY_LIST;
        }
        int size = list.size();
        List<NodeStatisticsDay> yearList = new ArrayList<NodeStatisticsDay>(10);
        NodeStatisticsDay thisData = null;
        NodeStatisticsDay nextData = null;
        for (int i = 0; i < size - 1; i++)
        {
            thisData = list.get(i);
            nextData = list.get(i + 1);
            if (!boforeDayWithSameYear(thisData.getDay(), nextData.getDay()))
            {
                yearList.add(thisData);
            }
        }
        yearList.add(list.get(size - 1));
        return yearList;
    }
    
    public static NodeHistoryStatisticsResponse packeNodeHistoryList(List<NodeStatisticsDay> list,
        String interval) throws ParseException
    {
        NodeHistoryStatisticsResponse response = new NodeHistoryStatisticsResponse();
        List<NodeHistoryStatisticsInfo> dataList = new ArrayList<NodeHistoryStatisticsInfo>(10);
        if (TimePoint.INTERVAL_DAY.equalsIgnoreCase(interval))
        {
            for (NodeStatisticsDay nodeStatistics : list)
            {
                dataList.add(NodeHistoryStatisticsInfo.convert(nodeStatistics, interval));
            }
            if (!dataList.isEmpty())
            {
                dataList.get(0).clearAddedData();
            }
        }
        else if (TimePoint.INTERVAL_WEEK.equalsIgnoreCase(interval))
        {
            List<NodeStatisticsDay> monthList = getWeekList(list);
            calculateAddedData(monthList);
            for (NodeStatisticsDay nodeStatistics : monthList)
            {
                dataList.add(NodeHistoryStatisticsInfo.convert(nodeStatistics, interval));
            }
        }
        else if (TimePoint.INTERVAL_MONTH.equalsIgnoreCase(interval))
        {
            List<NodeStatisticsDay> monthList = getMonthList(list);
            calculateAddedData(monthList);
            for (NodeStatisticsDay nodeStatistics : monthList)
            {
                dataList.add(NodeHistoryStatisticsInfo.convert(nodeStatistics, interval));
            }
        }
        else if (TimePoint.INTERVAL_SEANSON.equalsIgnoreCase(interval))
        {
            List<NodeStatisticsDay> monthList = getSeasonList(list);
            calculateAddedData(monthList);
            for (NodeStatisticsDay nodeStatistics : monthList)
            {
                dataList.add(NodeHistoryStatisticsInfo.convert(nodeStatistics, interval));
            }
        }
        else if (TimePoint.INTERVAL_YEAR.equalsIgnoreCase(interval))
        {
            List<NodeStatisticsDay> monthList = getYearList(list);
            calculateAddedData(monthList);
            for (NodeStatisticsDay nodeStatistics : monthList)
            {
                dataList.add(NodeHistoryStatisticsInfo.convert(nodeStatistics, interval));
            }
        }
        else
        {
            throw new InvalidParamException("Error interval");
        }
        resetSizeMb(dataList);
        response.setData(dataList);
        response.setTotalCount(response.getData().size());
        return response;
    }
    
    private static void resetSizeMb(List<NodeHistoryStatisticsInfo> dataList)
    {
        for (NodeHistoryStatisticsInfo nodeStatistics : dataList)
        {
            nodeStatistics.resetSizeMb();
        }
        
    }
}
