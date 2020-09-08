package com.huawei.sharedrive.app.utils;

import org.apache.commons.lang.exception.ExceptionUtils;

public final class ShareLinkExceptionUtil
{
    private ShareLinkExceptionUtil()
    {
        
    }
    
    public static String getClassName(Exception e)
    {
        Throwable[] throwables = ExceptionUtils.getThrowables(e);
        if (throwables.length == 0)
        {
            return "";
        }
        
        return throwables[0].getClass().getSimpleName();
    }
}
