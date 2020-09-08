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
package com.huawei.sharedrive.isystem.environment;

/**
 * 
 * @author s90006125
 * 
 */
public final class NetworkStatusCache
{
    private NetworkStatusCache()
    {
    }
    
    private static boolean reachable = true;
    
    public static boolean isReachable()
    {
        return NetworkStatusCache.reachable;
    }
    
    public static void setReachable(boolean reachable)
    {
        NetworkStatusCache.reachable = reachable;
    }
}
