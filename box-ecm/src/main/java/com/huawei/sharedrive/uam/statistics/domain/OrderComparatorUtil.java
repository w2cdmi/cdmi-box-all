package com.huawei.sharedrive.uam.statistics.domain;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.uam.openapi.domain.TimePoint;

public class OrderComparatorUtil implements Comparator<Object>, Serializable
{
    
    private static final long serialVersionUID = -6641641074331227200L;
    
    private static Logger logger = LoggerFactory.getLogger(OrderComparatorUtil.class);
    
    private String direct;
    
    private String field;
    
    public OrderComparatorUtil()
    {
        
    }
    
    public OrderComparatorUtil(String direct, String field)
    {
        this.direct = direct;
        this.field = field;
    }
    
    @Override
    public int compare(Object obj0, Object obj1)
    {
        Class<? extends Object> clazz0 = obj0.getClass();
        int result = 0;
        try
        {
            Field objectField = clazz0.getDeclaredField(field);
            String fieldType = objectField.getType().getSimpleName();
            Method method = clazz0.getMethod("get" + field.substring(0, 1).toUpperCase(Locale.getDefault())
                + field.substring(1), new Class[]{});
            if ("TimePoint".equals(fieldType))
            {
                result = getTimePointResult(obj0, obj1, method);
                
            }
            else if ("String".equals(fieldType))
            {
                result = getStringResult(obj0, obj1, method);
            }
            else if ("Integer".equals(fieldType) || "int".equals(fieldType))
            {
                result = getStringResult(obj0, obj1, method);
            }
            return result;
        }
        catch (NoSuchFieldException e)
        {
            logger.error("[orderLog]", e);
        }
        catch (InvocationTargetException e)
        {
            logger.error("[orderLog]", e);
        }
        catch (NoSuchMethodException e)
        {
            logger.error("[orderLog]", e);
        }
        catch (IllegalAccessException e)
        {
            logger.error("[orderLog]", e);
        }
        
        return 0;
    }
    
    private int getStringResult(Object obj0, Object obj1, Method method) throws IllegalAccessException,
        InvocationTargetException
    {
        int result = 0;
        Collator collator = Collator.getInstance(Locale.CHINA);
        if ("ASC".equalsIgnoreCase(direct))
        {
            result = collator.compare((String) method.invoke(obj0), (String) method.invoke(obj1));
        }
        else if ("DESC".equalsIgnoreCase(direct))
        {
            result = collator.compare((String) method.invoke(obj1), (String) method.invoke(obj0));
        }
        return result;
    }
    
    private int getTimePointResult(Object obj0, Object obj1, Method method) throws IllegalAccessException,
        InvocationTargetException
    {
        int result;
        TimePoint timePoint0 = (TimePoint) method.invoke(obj0);
        TimePoint timePoint1 = (TimePoint) method.invoke(obj1);
        Integer time0 = timePoint0.getYear() * 1000 + timePoint0.getNumber();
        Integer time1 = timePoint1.getYear() * 1000 + timePoint1.getNumber();
        if ("ASC".equals(direct))
        {
            if (time0 > time1)
            {
                result = 1;
            }
            else
            {
                result = -1;
            }
        }
        else
        {
            if (time0 < time1)
            {
                result = 1;
            }
            else
            {
                result = -1;
            }
        }
        return result;
    }
    
    public String getDirect()
    {
        return direct;
    }
    
    public void setDirect(String direct)
    {
        this.direct = direct;
    }
    
    public String getField()
    {
        return field;
    }
    
    public void setField(String field)
    {
        this.field = field;
    }
    
}
