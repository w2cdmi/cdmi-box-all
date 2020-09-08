package com.huawei.sharedrive.isystem.test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;

@ContextConfiguration(locations = {"classpath:applicationContext*.xml"})
@TransactionConfiguration(defaultRollback = false)
public abstract class AbstractSpringTest extends AbstractJUnit4SpringContextTests
{
    
    protected void setSimpleProperties(Object o)
    {
        Field[] fields = o.getClass().getDeclaredFields();
        for (Field field : fields)
        {
            String methodName = "set" + StringUtils.capitalize(field.getName());
            try
            {
                Method method = o.getClass().getMethod(methodName, field.getType());
                Random r = new Random();
                int value = r.nextInt(100);
                if ("String".equals(field.getType().getSimpleName()))
                {
                    method.invoke(o, String.valueOf(value / 10));
                }
                else if ("Long".equals(field.getType().getSimpleName()) && !("id".equals(field.getName())))
                {
                    method.invoke(o, Long.valueOf(value));
                }
                else if ("Integer".equals(field.getType().getSimpleName()) && !("id".equals(field.getName())))
                {
                    method.invoke(o, Integer.valueOf(value));
                }
                else if ("Calendar".equals(field.getType().getSimpleName()))
                {
                    method.invoke(o, Calendar.getInstance());
                }
            }
            catch (SecurityException e)
            {
                logger.warn("SecurityException: " + methodName + " method");
            }
            catch (NoSuchMethodException e)
            {
                logger.warn("NoSuchMethodException: " + methodName + " method");
            }
            catch (IllegalArgumentException e)
            {
                logger.warn("IllegalArgumentException: invoke method " + methodName + " ");
            }
            catch (IllegalAccessException e)
            {
                logger.warn("IllegalAccessException: invoke method " + methodName + " ");
            }
            catch (InvocationTargetException e)
            {
                logger.warn("InvocationTargetException: invoke method " + methodName + " ");
            }
        }
    }
    
}
