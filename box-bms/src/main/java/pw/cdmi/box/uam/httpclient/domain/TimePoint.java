package pw.cdmi.box.uam.httpclient.domain;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import pw.cdmi.core.utils.DateUtils;

public class TimePoint implements Serializable
{
    
    private static final long serialVersionUID = 6746780440495671754L;
    
    public static TimePoint convert(int day, String unit) throws ParseException
    {
        TimePoint point = new TimePoint();
        point.setUnit(unit);
        point.setYear(day / 10000);
        Date date = DateUtils.stringToDate("yyyyMMdd", String.valueOf(day), null);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if ("day".equalsIgnoreCase(unit))
        {
            point.setNumber(cal.get(Calendar.DAY_OF_YEAR));
        }
        else if ("week".equalsIgnoreCase(unit))
        {
            point.setNumber(cal.get(Calendar.WEEK_OF_YEAR));
            if (cal.get(Calendar.MONTH) == 11 && cal.get(Calendar.WEEK_OF_YEAR) <= 1)
            {
                point.setNumber(53);
            }
        }
        else if ("month".equalsIgnoreCase(unit))
        {
            point.setNumber(cal.get(Calendar.MONTH) + 1);
        }
        else if ("season".equalsIgnoreCase(unit))
        {
            point.setNumber(cal.get(Calendar.MONTH) / 3 + 1);
        }
        else if ("year".equalsIgnoreCase(unit))
        {
            point.setNumber(1);
        }
        else
        {
            point.setNumber(0);
        }
        return point;
    }
    
    public static TimePoint convert(long day, String unit) throws ParseException
    {
        return convert((int) day, unit);
    }
    
    private Integer number;
    
    private String unit;
    
    private Integer year;
    
    public Integer getNumber()
    {
        return number;
    }
    
    public String getUnit()
    {
        return unit;
    }
    
    public Integer getYear()
    {
        return year;
    }
    
    public void setNumber(Integer number)
    {
        this.number = number;
    }
    
    public void setUnit(String unit)
    {
        this.unit = unit;
    }
    
    public void setYear(Integer year)
    {
        this.year = year;
    }
    
    public String toShowString()
    {
        return this.year + this.unit + this.number;
    }
}
