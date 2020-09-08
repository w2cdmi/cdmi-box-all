package com.huawei.sharedrive.app.openapi.domain.statistics;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import pw.cdmi.core.utils.DateUtils;


public class TimePoint
{
    
    public static final String INTERVAL_YEAR = "year";
    
    public static final String INTERVAL_MONTH = "month";
    
    public static final String INTERVAL_SEANSON = "season";
    
    public static final String INTERVAL_WEEK = "week";
    
    public static final String INTERVAL_DAY = "day";
    
    private Integer year;
    private String unit;
    private Integer number;
    
    public Integer getYear()
    {
        return year;
    }
    public void setYear(Integer year)
    {
        this.year = year;
    }
    public String getUnit()
    {
        return unit;
    }
    public void setUnit(String unit)
    {
        this.unit = unit;
    }
    public Integer getNumber()
    {
        return number;
    }
    public void setNumber(Integer number)
    {
        this.number = number;
    }
    
    public static TimePoint convert(long day, String unit) throws ParseException
    {
        return convert((int)day, unit);
    }
    
    public static TimePoint convert(int day, String unit) throws ParseException
    {
        TimePoint point = new TimePoint();
        point.setUnit(unit);
        point.setYear(day / 10000);
        Date date = DateUtils.stringToDate("yyyyMMdd", String.valueOf(day), null);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if("day".equalsIgnoreCase(unit))
        {
            point.setNumber(cal.get(Calendar.DAY_OF_YEAR));
        }
        else if("week".equalsIgnoreCase(unit))
        {
            point.setNumber(cal.get(Calendar.WEEK_OF_YEAR));
            if(cal.get(Calendar.MONTH) == 11 && cal.get(Calendar.WEEK_OF_YEAR) <= 1)
            {
                point.setNumber(53);
            }
        }
        else if("month".equalsIgnoreCase(unit))
        {
            point.setNumber(cal.get(Calendar.MONTH) + 1);
        }
        else if("season".equalsIgnoreCase(unit))
        {
            point.setNumber(cal.get(Calendar.MONTH) / 3 + 1);
        }
        else if("year".equalsIgnoreCase(unit))
        {
            point.setNumber(1);
        }
        else
        {
            point.setNumber(0);
        }
        return point;
    }
    
    public String toShowString()
    {
        return this.year + this.unit + this.number;
    }
    
}
