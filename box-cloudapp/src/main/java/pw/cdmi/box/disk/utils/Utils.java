package pw.cdmi.box.disk.utils;

import java.io.Closeable;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class Utils
{
    private Utils()
    {
        
    }
    
    private static final Log LOGGER = LogFactory.getLog(Utils.class);
    
    public static void close(Closeable obj)
    {
        if (obj == null)
        {
            return;
        }
        try
        {
            obj.close();
        }
        catch (IOException e)
        {
            LOGGER.warn("close stream failed.", e);
        }
    }
    
    /**
     * Close Connection and avoid SQLException
     * 
     * @param conn
     */
    public static void close(Connection conn)
    {
        if (conn == null)
        {
            return;
        }
        try
        {
            conn.close();
        }
        catch (SQLException e)
        {
            LOGGER.warn("close failed");
        }
    }
    
    /**
     * Close Result Set and avoid SQLException
     * 
     * @param rs
     */
    public static void close(ResultSet rs)
    {
        if (rs == null)
        {
            return;
        }
        try
        {
            rs.close();
        }
        catch (SQLException e)
        {
            LOGGER.warn("close failed");
        }
    }
    
    /**
     * Close ResultSet;Statement and avoid SQLException
     * 
     * @param rs
     * @param stmt
     */
    public static void close(ResultSet rs, Statement stmt)
    {
        close(rs);
        close(stmt);
    }
    
    /**
     * Close ResultSet;Statement;Connection and avoid SQLException
     * 
     * @param rs
     * @param stmt
     * @param conn
     */
    public static void close(ResultSet rs, Statement stmt, Connection conn)
    {
        close(rs);
        close(stmt);
        close(conn);
    }
    
    /**
     * Close Statement and avoid SQLException
     * 
     * @param stmt
     */
    public static void close(Statement stmt)
    {
        if (stmt == null)
        {
            return;
        }
        try
        {
            stmt.close();
        }
        catch (SQLException e)
        {
            LOGGER.warn("close failed");
        }
    }
    
    public static boolean isIntegerStr(String intStr)
    {
        if (intStr == null)
        {
            return false;
        }
        if (!StringUtils.isNumeric(intStr) || intStr.length() > 10 || intStr.indexOf(".") >= 0)
        {
            return false;
        }
        Long longValue = Long.valueOf(intStr);
        if (longValue > Integer.MAX_VALUE || longValue < Integer.MIN_VALUE)
        {
            return false;
        }
        return true;
    }
    
    public static boolean notNegativeIntegerStr(String intStr)
    {
        if (!isIntegerStr(intStr))
        {
            return false;
        }
        if (Integer.parseInt(intStr) >= 0)
        {
            return true;
        }
        return false;
    }
    
    /**
     * Parse object to int.
     * 
     * @param intSource
     * @param defaultValue
     * @return <li> <code>defaultValue</code> if intSource is null or
     *         <code>intSource.toString</code> is not match <code>[0-9]*</code></li> <li>
     *         int value parsed by BigInteger</li>
     */
    public static int parseInt(Object intSource, int defaultValue)
    {
        if (intSource == null)
        {
            return defaultValue;
        }
        
        if (intSource instanceof Number)
        {
            return ((Number) intSource).intValue();
        }
        
        String intStr = intSource.toString();
        
        if (StringUtils.isBlank(intStr))
        {
            return defaultValue;
        }
        if (intStr.matches("[0-9]*"))
        {
            return new BigInteger(intStr).intValue();
        }
        return defaultValue;
    }
    
    public static long parseLong(Object aObj, long defaultValue)
    {
        if (aObj == null)
        {
            return defaultValue;
        }
        if (aObj instanceof Number)
        {
            return ((Number) aObj).longValue();
        }
        String longStr = aObj.toString();
        
        if (StringUtils.isBlank(longStr))
        {
            return defaultValue;
        }
        if (longStr.matches("[0-9]*"))
        {
            return new BigDecimal(longStr).longValue();
        }
        return defaultValue;
    }
}
