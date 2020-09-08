package com.huawei.sharedrive.app.share.service;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.Collator;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pw.cdmi.box.domain.Order;

public class OrderComparatorUtil implements Comparator<Object>, Serializable
{
    private static final long serialVersionUID = -4249545430369031267L;
    
    private static final String DIRECT_ASC = "ASC";
    
    private static final String DIRECT_DESC = "DESC";
    
    private static Logger logger = LoggerFactory.getLogger(OrderComparatorUtil.class);
    
    private String direct;
    
    private String field;
    
    private List<Order> orders;
    
    public OrderComparatorUtil(List<Order> orders)
    {
        super();
        this.setOrders(orders);
    }
    
    public OrderComparatorUtil(String field, String direct)
    {
        super();
        this.field = field;
        this.direct = direct;
    }
    
    @Override
    public int compare(Object obj0, Object obj1)
    {
        Class<? extends Object> clazz0 = obj0.getClass();
        int result = 0;
        try
        {
            Field objectField = null;
            for(; clazz0 != Object.class; clazz0 = clazz0.getSuperclass())
            {
                try
                {
                    objectField = clazz0.getDeclaredField(field);
                    break;
                }
                catch(NoSuchFieldException e)
                {
                    logger.warn("No such field: " + field + " in class: " + clazz0.getName());
                }
            }
            if(null ==  objectField)
            {
                return result;
            }
            String fieldType = objectField.getType().getSimpleName();
            Method method = clazz0.getMethod("get" + field.substring(0, 1).toUpperCase(Locale.getDefault())
                + field.substring(1), new Class[]{});
            if ("String".equals(fieldType))
            {
                result = orderString(obj0, obj1, method);
            }
            else if ("Double".equalsIgnoreCase(fieldType))
            {
                result = orderDouble(obj0, obj1, method);
            }
            else if ("Integer".equals(fieldType) || "int".equals(fieldType))
            {
                result = orderInteger(obj0, obj1, method);
            }
            else if ("Long".equalsIgnoreCase(fieldType) || "long".equals(fieldType))
            {
                result = orderLong(obj0, obj1, method);
            }
            else if ("Date".equals(fieldType))
            {
                result = orderDate(obj0, obj1, method);
            }
        }
        catch (RuntimeException e)
        {
            logger.error("[orderLog]", e);
        }
        catch (Exception e)
        {
            logger.error("[orderLog]", e);
        }
        return result;
    }
    
    public String getDirect()
    {
        return direct;
    }
    
    public String getField()
    {
        return field;
    }
    
    public List<Order> getOrders()
    {
        return orders;
    }
    
    public void setDirect(String direct)
    {
        this.direct = direct;
    }
    
    public void setField(String field)
    {
        this.field = field;
    }
    
    public void setOrders(List<Order> orders)
    {
        this.orders = orders;
    }
    
    /**
     * @param obj0
     * @param obj1
     * @param method
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private int orderDate(Object obj0, Object obj1, Method method) throws IllegalAccessException,
        InvocationTargetException
    {
        int result = 0;
        if (StringUtils.equalsIgnoreCase(DIRECT_ASC, direct))
        {
            result = Long.compare(Long.valueOf(((Date) method.invoke(obj0)).getTime()),
                Long.valueOf(((Date) method.invoke(obj1)).getTime()));
        }
        else if (StringUtils.equalsIgnoreCase(DIRECT_DESC, direct))
        {
            result = Long.compare(Long.valueOf(((Date) method.invoke(obj1)).getTime()),
                Long.valueOf(((Date) method.invoke(obj0)).getTime()));
        }
        return result;
    }
    
    /**
     * @param obj0
     * @param obj1
     * @param method
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private int orderDouble(Object obj0, Object obj1, Method method) throws IllegalAccessException,
        InvocationTargetException
    {
        int result = 0;
        if (StringUtils.equalsIgnoreCase(DIRECT_ASC, direct))
        {
            result = (Double.valueOf((String) method.invoke(obj0))).compareTo(Double.valueOf((String) method.invoke(obj1)));
        }
        else if (StringUtils.equalsIgnoreCase(DIRECT_DESC, direct))
        {
            result = (Double.valueOf((String) method.invoke(obj1))).compareTo(Double.valueOf((String) method.invoke(obj0)));
        }
        return result;
    }
    
    /**
     * @param obj0
     * @param obj1
     * @param method
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private int orderInteger(Object obj0, Object obj1, Method method) throws IllegalAccessException,
        InvocationTargetException
    {
        int result = 0;
        if (StringUtils.equalsIgnoreCase(DIRECT_ASC, direct))
        {
            result = (Integer.valueOf((String) method.invoke(obj0))).compareTo(Integer.valueOf((String) method.invoke(obj1)));
        }
        else if (StringUtils.equalsIgnoreCase(DIRECT_DESC, direct))
        {
            result = (Integer.valueOf((String) method.invoke(obj1))).compareTo(Integer.valueOf((String) method.invoke(obj0)));
        }
        return result;
    }
    
    /**
     * @param obj0
     * @param obj1
     * @param method
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private int orderLong(Object obj0, Object obj1, Method method) throws IllegalAccessException,
        InvocationTargetException
    {
        int result = 0;
        if (StringUtils.equalsIgnoreCase(DIRECT_ASC, direct))
        {
            result = ((Long) method.invoke(obj0)).compareTo((Long) method.invoke(obj1));
        }
        else if (StringUtils.equalsIgnoreCase(DIRECT_DESC, direct))
        {
            result = ((Long) method.invoke(obj1)).compareTo((Long) method.invoke(obj0));
        }
        return result;
    }
    
    /**
     * @param obj0
     * @param obj1
     * @param method
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private int orderString(Object obj0, Object obj1, Method method) throws IllegalAccessException,
        InvocationTargetException
    {
        Collator collator = Collator.getInstance(Locale.CHINA);
        int result = 0;
        if (StringUtils.equalsIgnoreCase(DIRECT_ASC, direct))
        {
            result = collator.compare((String) method.invoke(obj0), (String) method.invoke(obj1));
        }
        else if (StringUtils.equalsIgnoreCase(DIRECT_DESC, direct))
        {
            result = collator.compare((String) method.invoke(obj1), (String) method.invoke(obj0));
        }
        return result;
    }
}
