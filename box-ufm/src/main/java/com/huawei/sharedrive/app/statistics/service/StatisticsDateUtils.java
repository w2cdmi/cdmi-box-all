package com.huawei.sharedrive.app.statistics.service;

import java.util.Calendar;

public final class StatisticsDateUtils
{
    private StatisticsDateUtils()
    {
    }
    
    public static int getDay()
    {
        Calendar now = Calendar.getInstance();
        int day = now.get(Calendar.YEAR) * 10000;
        day += (now.get(Calendar.MONTH) + 1) * 100;
        if (now.get(Calendar.HOUR_OF_DAY) < 12)
        {
            day += now.get(Calendar.DAY_OF_MONTH) - 1;
        }
        else
        {
            day += now.get(Calendar.DAY_OF_MONTH);
        }
        return day;
    }
    
    public static int getDay(Calendar calendar)
    {
        int day = calendar.get(Calendar.YEAR) * 10000;
        day += (calendar.get(Calendar.MONTH) + 1) * 100;
        day += calendar.get(Calendar.DAY_OF_MONTH);
        return day;
    }
    
    /**
     * 获取上一个日期
     * 
     * @param day
     * @return
     */
    public static int getLastDay(int day)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, day / 10000);
        calendar.set(Calendar.MONTH, day % 10000 / 100 - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day % 100);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        return getDay(calendar);
    }
    
    /**
     * 获取上一个日期
     * 
     * @param day
     * @return
     */
    public static int getLastDay(Calendar calendar)
    {
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        return getDay(calendar);
    }
    
    /**
     * 获取上一个日期
     * 
     * @param day
     * @return
     */
    public static int getLastDay(long day)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, (int) day / 10000);
        calendar.set(Calendar.MONTH, (int) day % 10000 / 100 - 1);
        calendar.set(Calendar.DAY_OF_MONTH, (int) day % 100);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        return getDay(calendar);
    }
    
    /**
     * 以5分钟为单位，获取时间点
     * 
     * @param calendar
     * @return
     */
    public static int getTimeUnitBy5Min(Calendar calendar)
    {
        return calendar.get(Calendar.HOUR_OF_DAY) * 12 + calendar.get(Calendar.MINUTE) / 5;
    }
}
