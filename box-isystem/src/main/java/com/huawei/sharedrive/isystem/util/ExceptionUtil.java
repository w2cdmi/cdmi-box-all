/**
 * 
 */
package com.huawei.sharedrive.isystem.util;

import org.apache.commons.lang.exception.ExceptionUtils;

/**
 * @author d00199602
 * 
 */
public final class ExceptionUtil
{
    private ExceptionUtil()
    {
        
    }
    
    public static String getExceptionClassName(Exception e)
    {
        Throwable[] throwables = ExceptionUtils.getThrowables(e);
        if (throwables.length == 0)
        {
            return "";
        }
        return throwables[0].getClass().getSimpleName();
    }
}
