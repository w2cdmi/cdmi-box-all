package com.huawei.sharedrive.app.utils.test;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.AuthFailedException;
import com.huawei.sharedrive.app.utils.ShareLinkExceptionUtil;
import pw.cdmi.core.utils.DateUtils;

public class DateUtilsTest
{
    @Test
    public void addMonthTest()
    {
        Date month = DateUtils.addMonth(new Date(), 4);
        System.out.println(month);
    }
    
    @Test
    public void addMonthTest2()
    {
        try
        {
            Date month = DateUtils.addMonth(null, 4);
            System.out.println(month);
        }
        catch (Exception e)
        {
            String className = ShareLinkExceptionUtil.getClassName(e);
            System.out.println(className);
            Assert.assertEquals("NullPointerException", className);
        }
    }
    /*
    @Test
    public void checkDateHeaderTest()
    {
        try
        {
            DateUtils.checkDateHeader("Tue, 22 Dec 2015 06:10:20 GMT");
        }
        catch (AuthFailedException e)
        {
            String className = ShareLinkExceptionUtil.getClassName(e);
            System.out.println(className);
            Assert.assertEquals("AuthFailedException", className);
        }
    }
    
    @Test
    public void checkDateHeaderTest2()
    {
        try
        {
            DateUtils.checkDateHeader(null);
        }
        catch (AuthFailedException e)
        {
            String className = ShareLinkExceptionUtil.getClassName(e);
            System.out.println(className);
            Assert.assertEquals("AuthFailedException", className);
        }
    }*/
    
    @Test
    public void convertDateHeaderTest()
    {
        Date date = DateUtils.convertDateHeader("Tue, 22 Dec 2015 02:37:46 GMT");
        System.out.println(date);
        Assert.assertEquals("Tue Dec 22 10:37:46 GMT+08:00 2015", date + "");
    }
    
    @Test
    public void convertDateHeaderTest2()
    {
        Date date = DateUtils.convertDateHeader(null);
        System.out.println(date);
        Assert.assertEquals(null, date);
    }
    
    @Test
    public void converTimeStampToStringTest()
    {
        String stampToString = DateUtils.converTimeStampToString(new Timestamp(1110000000L));
        System.out.println(stampToString);
        Assert.assertEquals("70-1-14 上午4:20", stampToString);
    }
    
    @Test
    public void converTimeStampToStringTest2()
    {
        try
        {
            DateUtils.converTimeStampToString(null);
        }
        catch (Exception e)
        {
            String className = ShareLinkExceptionUtil.getClassName(e);
            System.out.println(className);
            Assert.assertEquals("NullPointerException", className);
        }
    }
    
    @Test
    public void date2CalTest()
    {
        Calendar calendar = DateUtils.date2Cal(new Date());
        System.out.println(calendar);
    }
    
    @Test
    public void date2CalTest2()
    {
        try
        {
            DateUtils.date2Cal(null);
        }
        catch (Exception e)
        {
            String className = ShareLinkExceptionUtil.getClassName(e);
            System.out.println(className);
            Assert.assertEquals("NullPointerException", className);
        }
    }
    
    @Test
    public void dateToStringTest()
    {
        String toString = DateUtils.dateToString(new Date());
        System.out.println(toString);
    }
    
    @Test
    public void dateToStringTest0()
    {
        String toString = DateUtils.dateToString(null);
        System.out.println(toString);
    }
    
    @Test
    public void dateToStringTest1()
    {
        String toString = DateUtils.dateToString(new Date(), "MM-dd-yyyy");
        System.out.println(toString);
    }
    
    @Test
    public void dateToStringTest2()
    {
        String toString = DateUtils.dateToString("yyyy-MM-dd", new Date());
        System.out.println(toString);
    }
    
    @Test
    public void dateToStringTest3()
    {
        String toString = DateUtils.dateToString("yyyy-MM-dd", new Date(), "UTC+08:00");
        System.out.println(toString);
    }
    
    @Test
    public void dateToStringTest4()
    {
        String toString = DateUtils.dateToString(new Date(), "");
        System.out.println(toString);
    }
    
    @Test
    public void dateToStringTest5()
    {
        String toString = DateUtils.dateToString("yyyy-MM-dd", new Date(), "");
        System.out.println(toString);
    }
    
    @Test
    public void formatTest()
    {
        String format = DateUtils.format(DateUtils.date2Cal(new Date()), "MM-dd-yyyy");
        System.out.println(format);
    }
    
    @Test
    public void formatTest1()
    {
        try
        {
            DateUtils.format(DateUtils.date2Cal(null), "MM-dd-yyyy");
        }
        catch (Exception e)
        {
            String className = ShareLinkExceptionUtil.getClassName(e);
            System.out.println(className);
            Assert.assertEquals("NullPointerException", className);
        }
    }
    
    @Test
    public void formatTest2()
    {
        String format = DateUtils.format(new Date());
        System.out.println(format);
    }
    
    @Test
    public void formatTest3()
    {
        String format = DateUtils.format(null);
        System.out.println(format);
        Assert.assertEquals("", format);
    }
    
    @Test
    public void formatTest4()
    {
        String format = DateUtils.format(new Date(), "MM-dd-yyyy");
        System.out.println(format);
    }
    
    @Test
    public void formatDefaultTest()
    {
        String default1 = DateUtils.formatDefault(DateUtils.date2Cal(new Date()));
        System.out.println(default1);
    }
    
    @Test
    public void formatDefaultTest0()
    {
        String default1 = DateUtils.formatDefault(null);
        System.out.println(default1);
    }
    
    @Test
    public void formatDefaultTest1()
    {
        try
        {
            DateUtils.formatDefault(DateUtils.date2Cal(null));
        }
        catch (Exception e)
        {
            String className = ShareLinkExceptionUtil.getClassName(e);
            System.out.println(className);
            Assert.assertEquals("NullPointerException", className);
        }
    }
    
    @Test
    public void getDateTest()
    {
        String date = DateUtils.getDate();
        System.out.println(date);
    }
    
    @Test
    public void getDateTest1()
    {
        String date = DateUtils.getDatePattern();
        System.out.println(date);
    }
    
    @Test
    public void nowTest()
    {
        Date now = DateUtils.now();
        System.out.println(now);
        DateUtils.getDateTime(null);
        DateUtils.getDateTime(new Date());
    }
    
    @Test
    public void nowCalTest()
    {
        Calendar calendar = DateUtils.nowCal();
        System.out.println(calendar);
    }
    
    @Test
    public void parseTest() throws Exception
    {
        Date parse = DateUtils.parse("12-22-2015", "MM-dd-yyyy", "UTC+08:00");
        System.out.println(parse);
        Assert.assertEquals("Tue Dec 22 08:00:00 GMT+08:00 2015", parse + "");
    }
    
    @Test
    public void parseTest1() throws Exception
    {
        Date parse = DateUtils.parse("12-22-2015", "MM-dd-yyyy", "");
        System.out.println(parse);
    }
    
    @Test
    public void parseTest2() throws Exception
    {
        Date parse = DateUtils.parse("", "MM-dd-yyyy", "UTC+08:00");
        System.out.println(parse);
    }
    
    @Test
    public void stringToCalendarDefaultTest() throws Exception
    {
        Calendar calendar = DateUtils.stringToCalendarDefault("2015-12-25 12:23");
        System.out.println(calendar);
    }
    
    @Test
    public void stringToDateTest() throws Exception
    {
        Date toDate = DateUtils.stringToDate("yyyy-MM-dd HH:mm", "2015-12-25 12:23", "UTC+08:00");
        System.out.println(toDate);
        Assert.assertEquals("Fri Dec 25 20:23:00 GMT+08:00 2015", toDate + "");
    }
    
    @Test
    public void stringToDateTest1() throws Exception
    {
        Date toDate = DateUtils.stringToDate("yyyy-MM-dd HH:mm", "2015-12-25 12:23", "");
        System.out.println(toDate);
    }
    
    @Test
    public void getDateBeforeTest()
    {
        Date before = DateUtils.getDateBefore(new Date(), 23);
        System.out.println(before);
    }
    
    @Test
    public void getDateAfterTest()
    {
        Date after = DateUtils.getDateAfter(new Date(), 23);
        System.out.println(after);
    }
    
    @Test
    public void getDateBeforeMinuteTest()
    {
        Date beforeMinute = DateUtils.getDateBeforeMinute(new Date(), 1111123);
        System.out.println(beforeMinute);
    }
    
    @Test
    public void getDateAfterSecondsTest()
    {
        Date beforeMinute = DateUtils.getDateAfterSeconds(new Date(), 1111123);
        System.out.println(beforeMinute);
    }
}
