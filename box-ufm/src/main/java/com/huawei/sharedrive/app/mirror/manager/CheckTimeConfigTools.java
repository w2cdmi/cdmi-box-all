package com.huawei.sharedrive.app.mirror.manager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.app.mirror.domain.TimeConfig;

public final class CheckTimeConfigTools
{
    private CheckTimeConfigTools()
    {
        
    }
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CheckTimeConfigTools.class);
    
    private static final String ZERO_STR = "0";
    
    private static final int DATE_LENGTH = 16;
    
    public static boolean isCurrentTimeInSetting(boolean isTimeEnable, List<TimeConfig> lstTimeConfig)
    {
        // 时间配置是否生效关闭，默认时间判断设为执行
        if (!isTimeEnable)
        {
            LOGGER.info("the Time Setting is enabel");
            return true;
        }
        
        // 没有配置时间段,所有时间都不执行
        if (null == lstTimeConfig || lstTimeConfig.isEmpty())
        {
            LOGGER.info("these is no Timeconfig");
            return false;
        }
        
        // 循环查看所有时间配置，看当前时间是否在时间配置范围内
        boolean isInCopyRunTime = false;
        for (TimeConfig timeConfig : lstTimeConfig)
        {
            if (isInCopyTime(timeConfig.getExeStartAt(), timeConfig.getExeEndAt()))
            {
                isInCopyRunTime = true;
                break;
            }
        }
        
        return isInCopyRunTime;
    }
    
    @SuppressWarnings("deprecation")
    private static boolean isInCopyTime(String exebeginTime, String exeEndTime)
    {
        
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        boolean flag = false;
        if (null == exebeginTime || null == exeEndTime || "".equals(exebeginTime) || "".equals(exeEndTime))
        {
            LOGGER.error("time setting is null ");
            return flag;
        }
        
        Date date = new Date();
        String date1 = null;
        String date2 = null;
        String beginStr = "";
        String endStr = "";
        // 配置时间跨天
        if (isOverDayTimeConfig(exebeginTime, exeEndTime))
        {
            // 当前时间小时和配置开始时间小时相等，比较分钟
            if (date.getHours() == convertHourString2Int(exebeginTime.split(":")[0]))
            {
                if (date.getMinutes() >= convertHourString2Int(exebeginTime.split(":")[1]))
                {
                    // 开始任务 当前执行开始时间为 今天+开始时间时分秒 至 明天 + 结束时间时分秒
                    date1 = sdf1.format(date);
                    date2 = sdf1.format(getNewDate(date, 1));
                    beginStr = date1 + " " + exebeginTime;
                    endStr = date2 + " " + exeEndTime;
                }
                else
                {
                    // 开始任务 当前执行开始时间为 昨天+开始时间时分秒 至 今天 + 结束时间时分秒
                    date1 = sdf1.format(getNewDate(date, -1));
                    date2 = sdf1.format(date);
                    beginStr = date1 + " " + exebeginTime;
                    endStr = date2 + " " + exeEndTime;
                }
            }
            // 当前时间小于开始时间
            else if (date.getHours() < convertHourString2Int(exebeginTime.split(":")[0]))
            {
                // 开始任务 当前执行开始时间为 昨天+开始时间时分秒 至 今天 + 结束时间时分秒
                date1 = sdf1.format(getNewDate(date, -1));
                date2 = sdf1.format(date);
                beginStr = date1 + " " + exebeginTime;
                endStr = date2 + " " + exeEndTime;
            }
            else if (date.getHours() > convertHourString2Int(exebeginTime.split(":")[0]))
            {
                // 开始任务 当前执行开始时间为 今天+开始时间时分秒 至 明天 + 结束时间时分秒
                date1 = sdf1.format(date);
                date2 = sdf1.format(getNewDate(date, 1));
                beginStr = date1 + " " + exebeginTime;
                endStr = date2 + " " + exeEndTime;
            }
            
        }
        else
        {
            // 不跨天，即当天
            String day = sdf1.format(date);
            beginStr = day + " " + exebeginTime;
            endStr = day + " " + exeEndTime;
        }
        
        try
        {
            if (isCurrentTimeinSetting(beginStr, endStr, date))
            {
                // 当前时间在配置范围内，任务该执行
                flag = true;
            }
            else
            {
                // 当前时间不在配置范围内，任务不该执行
                flag = false;
            }
            
        }
        catch (ParseException e)
        {
            LOGGER.error("Parse Date to String fault");
            flag = false;
        }
        
        return flag;
    }
    
    private static boolean isOverDayTimeConfig(String begin, String end)
    {
        boolean flag = false;
        SimpleDateFormat format = new SimpleDateFormat("HH:mm"); 
        Date begintime=null;
        Date endtime=null;
        if(StringUtils.isBlank(begin) || StringUtils.isBlank(end)){
            LOGGER.error("time setting is null ");
            return flag;
        }
        try{
            
            begintime=format.parse(begin);
            endtime=format.parse(end);
            if(begintime.after(endtime))
            {
                flag=true;
            }
                
        }
        catch(ParseException e){
            
            LOGGER.error("Parse Date to String fault");
            
        }

        /*if (null == hour1 || null == hour2 || "".equals(hour1) || "".equals(hour2))
        {
            LOGGER.error("time setting is null ");
            return flag;
        }
        
        if (hour1.length() != 2 || hour2.length() != 2)
        {
            LOGGER.error("time  setting formate  is unRight h1:" + hour1 + " ;hour2 :" + hour2);
            return flag;
        }
        
        int h1 = convertHourString2Int(hour1);
        int h2 = convertHourString2Int(hour2);
        
        if (h1 > h2)
        {
            flag = true;
        }
        */
        return flag;
    }
    
    private static int convertHourString2Int(String str)
    {
        int re = -1;
        if (null == str || "".equals(str))
        {
            return re;
        }
        
        if (str.substring(0, 1).equalsIgnoreCase(ZERO_STR))
        {
            re = Integer.parseInt(str.substring(1, 2));
        }
        else
        {
            re = Integer.parseInt(str);
        }
        
        return re;
    }
    
    private static boolean isCurrentTimeinSetting(String beginStr, String endStr, Date nowDate)
        throws ParseException
    {
        String beginStr1 = beginStr;
        String endStr1 = endStr;
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        boolean flag = false;
        if (null == beginStr || null == endStr)
        {
            return flag;
        }
        
        if (beginStr1.length() == DATE_LENGTH)
        {
            beginStr1 = beginStr1 + ":00";
        }
        
        if (endStr1.length() == DATE_LENGTH)
        {
            endStr1 = endStr1 + ":00";
        }
        
        Date beginTime = null;
        Date endTime = null;
        beginTime = sdf.parse(beginStr1);
        endTime = sdf.parse(endStr1);
        if (nowDate.after(beginTime) && nowDate.before(endTime))
        {
            flag = true;
        }
        
        return flag;
    }
    
    /**
     * 获取指定日期的前后n天的日期 k
     * 
     * @param date
     * @param a 正数为后 n天 负数 为前 n 天
     * @return
     */
    private static Date getNewDate(Date date, int a)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, a);
        return calendar.getTime();
    }
    
}
