package com.huawei.sharedrive.uam.rest.test.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.uam.exception.AuthFailedException;

public final class DateUtils
{
    /** ANSI C's asctime() format */
    public static final String DATE_FORMAT_ANSIC = "EEE MMM d HH:mm:ss yyyy";
    
    public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm";
    
    /** RFC 850 parser */
    public static final String DATE_FORMAT_RFC_850 = "EEEE, dd-MMM-yy HH:mm:ss z";
    
    public static final String GLOBAL_LOG_FORMAT = "yyyy-MM-dd_HH-mm-ss";
    
    /** RFC 822 parser */
    public static final String RFC822_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss z";
    
    private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
    
    private static final long TIME_RANGE = 15 * 60 * 1000;
    
    public static Date addMonth(Date date, int n)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, n);
        return cal.getTime();
    }
    
    public static void checkDateHeader(String dateStr) throws AuthFailedException
    {
        Date headerDate = convertDateHeader(dateStr);
        if(null == headerDate)
        {
            throw new AuthFailedException();
        }
        if(Math.abs(System.currentTimeMillis() - headerDate.getTime()) > TIME_RANGE)
        {
            throw new AuthFailedException();
        }
    }
    
    public static Date convertDateHeader(String date)
    {
        if (StringUtils.isBlank(date))
        {
            return null;
        }
        Date dateTime = null;
        try
        {
            dateTime = stringToDate(DateUtils.RFC822_DATE_FORMAT, date, "GMT");
        }
        catch (Exception e)
        {
            try
            {
                dateTime = stringToDate(DateUtils.DATE_FORMAT_RFC_850, date, "GMT");
            }
            catch (Exception e1)
            {
                try
                {
                    dateTime = stringToDate(DateUtils.DATE_FORMAT_ANSIC, date, "GMT");
                }
                catch (Exception e2)
                {
                    dateTime=null;
                }
            }
        }
        return dateTime;
    }
    
    public static String converTimeStampToString(Timestamp ts)
    {
        
        return new SimpleDateFormat().format(ts);
    }
    
    public static String dateToString(String pattern, Date d, String timeZone)
    {
        DateFormat dFormat = new SimpleDateFormat(pattern, Locale.ENGLISH);
        if (timeZone != null && timeZone.length() > 0)
        {
            Calendar calendar = Calendar.getInstance(new SimpleTimeZone(0, timeZone));
            dFormat.setCalendar(calendar);
        }
        String sDate = dFormat.format(d);
        return sDate;
    }
    
    public static Calendar date2Cal(Date date)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c;
    }
    
    public static String dateToString(Date d)
    {
        SimpleDateFormat lenientDateFormat = new SimpleDateFormat(DEFAULT_DATE_PATTERN);
        if (d == null)
        {
            return null;
        }
        return lenientDateFormat.format(d);
    }
    
    public static String format(Calendar cal, String pattern)
    {
        return cal == null ? "" : new SimpleDateFormat(pattern).format(cal.getTime());
    }
    
    public static String format(Date date)
    {
        return date == null ? "" : format(date, getDatePattern());
    }
    
    public static String format(Date date, String pattern)
    {
        return date == null ? "" : new SimpleDateFormat(pattern).format(date);
    }
    
    public static String formatDefault(Calendar cal)
    {
        return cal == null ? "" : format(cal.getTime(), DATE_FORMAT_PATTERN);
    }
    
    public static String getDatePattern()
    {
        return DEFAULT_DATE_PATTERN;
    }
    
    public static String getToday()
    {
        return format(now());
    }
    
    public static Date now()
    {
        return nowCal().getTime();
    }
    
    public static Calendar nowCal()
    {
        return Calendar.getInstance();
    }
    
    public static Date parse(String strDate) throws ParseException
    {
        return StringUtils.isEmpty(strDate) ? null : parse(strDate, getDatePattern(), null);
    }

    public static Date parse(String strDate, String pattern, String timeZone) throws ParseException
    {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        if (StringUtils.isNotEmpty(timeZone))
        {
            format.setTimeZone(TimeZone.getTimeZone(timeZone));
        }
        
        return StringUtils.isEmpty(strDate) ? null : format.parse(strDate);
    }
    
    public static Calendar stringToCalendarDefault(String inDate) throws ParseException
    {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        
        calendar.setTime(new SimpleDateFormat(DATE_FORMAT_PATTERN).parse(inDate));
        return calendar;
    }
    
    public static Date stringToDate(String pattern, String sTime, String timeZone) throws ParseException
    {
        DateFormat dFormat = new SimpleDateFormat(pattern, Locale.ENGLISH);
        
        if (timeZone != null && !StringUtils.isBlank(timeZone))
        {
            Calendar calendar = Calendar.getInstance(new SimpleTimeZone(0, timeZone));
            dFormat.setCalendar(calendar);
        }
        Date date = dFormat.parse(sTime);
        return date;
    }
    
    
    private DateUtils()
    {
        
    }
}
