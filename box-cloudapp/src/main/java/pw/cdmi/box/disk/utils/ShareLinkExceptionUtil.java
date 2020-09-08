package pw.cdmi.box.disk.utils;

import org.apache.commons.lang.exception.ExceptionUtils;

import pw.cdmi.core.exception.RestException;


public final class ShareLinkExceptionUtil
{
    private ShareLinkExceptionUtil()
    {
        
    }
    
    public static String getClassName(Exception e)
    {
        if (e instanceof RestException)
        {
            return ((RestException) e).getCode();
        }
        
        Throwable[] throwables = ExceptionUtils.getThrowables(e);
        if (throwables.length == 0)
        {
            return "";
        }
        
        return throwables[0].getClass().getSimpleName();
    }
}
