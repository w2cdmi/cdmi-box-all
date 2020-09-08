/*
 * Copyright Notice:
 *      Copyright  1998-2009, Huawei Technologies Co., Ltd.  ALL Rights Reserved.
 *
 *      Warning: This computer software sourcecode is protected by copyright law
 *      and international treaties. Unauthorized reproduction or distribution
 *      of this sourcecode, or any portion of it, may result in severe civil and
 *      criminal penalties, and will be prosecuted to the maximum extent
 *      possible under the law.
 */
package com.huawei.sharedrive.isystem.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @author s90006125
 * 
 */
public final class DateTimeUtils
{
    private DateTimeUtils()
    {
        
    }
    
    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";
    
    public static String format(long time)
    {
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat(PATTERN);
        
        return format.format(date);
    }
    
    public static String format(Date date, String pattern)
    {
        return date == null ? "" : new SimpleDateFormat(pattern).format(date);
    }
}
