/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
package pw.cdmi.file.engine.realtime.printer;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RealTimePrinter
{
    private static final Logger LOGGER = LoggerFactory.getLogger(RealTimePrinter.class);
    
    private RealTimePrinter()
    {
        
    }
    
    public static void info(String msg)
    {
        if (!LOGGER.isInfoEnabled())
        {
            return;
        }
        
        try
        {
            if (StringUtils.isBlank(msg))
            {
                if (isDebugEnabled())
                {
                    LOGGER.debug("Real Time Copy Msg is null or empty.");
                }
                return;
            }
            
            LOGGER.info(msg);
        }
        catch (RuntimeException e)
        {
            LOGGER.warn("Real Time Copy Msg log info failed.");
        }
    }
    
    public static void warn(String msg)
    {
        if (!LOGGER.isWarnEnabled())
        {
            return;
        }
        
        try
        {
            if (StringUtils.isBlank(msg))
            {
                if (isDebugEnabled())
                {
                    LOGGER.debug("Real Time Copy Msg is null or empty.");
                }
                return;
            }
            
            LOGGER.warn(msg);
        }
        catch (RuntimeException e)
        {
            LOGGER.warn("Real Time Copy Msg log warn failed.");
        }
    }
    
    public static void warn(String msg, Throwable e)
    {
        if (!LOGGER.isWarnEnabled())
        {
            return;
        }
        
        try
        {
            if (StringUtils.isBlank(msg))
            {
                if (isDebugEnabled())
                {
                    LOGGER.debug("Real Time Copy Msg is null or empty.");
                }
                return;
            }
            
            LOGGER.warn(msg, e);
        }
        catch (RuntimeException e1)
        {
            LOGGER.warn("Real Time Copy Msg log warn failed.");
        }
    }
    
    public static void error(String msg)
    {
        if (!LOGGER.isWarnEnabled())
        {
            return;
        }
        
        try
        {
            if (StringUtils.isBlank(msg))
            {
                if (isDebugEnabled())
                {
                    LOGGER.debug("Real Time Copy Msg is null or empty.");
                }
                return;
            }
            
            LOGGER.error(msg);
        }
        catch (RuntimeException e)
        {
            LOGGER.warn("Real Time Copy Msg log error failed.");
        }
    }
    
    public static void error(String msg, Throwable e)
    {
        if (!LOGGER.isWarnEnabled())
        {
            return;
        }
        
        try
        {
            if (StringUtils.isBlank(msg))
            {
                if (isDebugEnabled())
                {
                    LOGGER.debug("Real Time Copy Msg is null or empty.");
                }
                return;
            }
            
            LOGGER.error(msg, e);
        }
        catch (RuntimeException e1)
        {
            LOGGER.warn("Real Time Copy Msg log error failed.");
        }
    }
    
    public static boolean isDebugEnabled()
    {
        return LOGGER.isDebugEnabled();
    }
}
