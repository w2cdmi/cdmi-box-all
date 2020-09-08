package pw.cdmi.file.engine.mirro.printer;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MirrorPrinter
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MirrorPrinter.class);
    
    public static void info(final String msg)
    {
        if (!LOGGER.isInfoEnabled())
        {
            return;
        }
        
        try
        {
            String logStr = null;
            if (StringUtils.isNotBlank(msg))
            {
                logStr = msg;
            }
            
            if (StringUtils.isBlank(logStr))
            {
                if (LOGGER.isDebugEnabled())
                {
                    LOGGER.debug("log string is null.");
                }
                return;
            }
            
            LOGGER.info(logStr);
        }
        catch (RuntimeException e1)
        {
            LOGGER.warn("print mirror failed." + e1.getMessage());
        }
    }
    
    public static void warn(final String msg, Throwable e)
    {
        if (!LOGGER.isInfoEnabled())
        {
            return;
        }
        
        try
        {
            String logStr = null;
            if (StringUtils.isNotBlank(msg))
            {
                logStr = msg;
            }
            
            if (StringUtils.isBlank(logStr))
            {
                if (LOGGER.isDebugEnabled())
                {
                    LOGGER.debug("log string is null.");
                }
                return;
            }
            
            LOGGER.warn(logStr, e);
        }
        catch (RuntimeException e1)
        {
            LOGGER.warn("print mirror failed." + e1.getMessage());
        }
    }
    
    public static void warn(final String msg)
    {
        if (!LOGGER.isInfoEnabled())
        {
            return;
        }
        
        try
        {
            String logStr = null;
            if (StringUtils.isNotBlank(msg))
            {
                logStr = msg;
            }
            
            if (StringUtils.isBlank(logStr))
            {
                if (LOGGER.isDebugEnabled())
                {
                    LOGGER.debug("log string is null.");
                }
                return;
            }
            
            LOGGER.warn(logStr);
        }
        catch (RuntimeException e1)
        {
            LOGGER.warn("print mirror failed." + e1.getMessage());
        }
    }
    
    public static void error(final String msg)
    {
        if (!LOGGER.isInfoEnabled())
        {
            return;
        }
        
        try
        {
            String logStr = null;
            if (StringUtils.isNotBlank(msg))
            {
                logStr = msg;
            }
            
            if (StringUtils.isBlank(logStr))
            {
                if (LOGGER.isDebugEnabled())
                {
                    LOGGER.debug("log string is null.");
                }
                return;
            }
            
            LOGGER.error(logStr);
        }
        catch (RuntimeException e1)
        {
            LOGGER.warn("print mirror failed." + e1.getMessage());
        }
    }
    
    public static void error(final String msg, Throwable e)
    {
        if (!LOGGER.isInfoEnabled())
        {
            return;
        }
        
        try
        {
            String logStr = null;
            if (StringUtils.isNotBlank(msg))
            {
                logStr = msg;
            }
            
            if (StringUtils.isBlank(logStr))
            {
                if (LOGGER.isDebugEnabled())
                {
                    LOGGER.debug("log string is null.");
                }
                return;
            }
            
            LOGGER.error(logStr, e);
        }
        catch (RuntimeException e1)
        {
            LOGGER.warn("print mirror failed." + e1.getMessage());
        }
    }
    
    public boolean isDebugEnabled()
    {
        return LOGGER.isDebugEnabled();
    }
}
