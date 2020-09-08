package com.huawei.sharedrive.isystem.environment.test;

import junit.framework.Assert;

import org.junit.Test;

import com.huawei.sharedrive.isystem.environment.NetworkStatusCache;

public class NetworkStatusCacheTest
{
    @Test
    public void isReachableTest()
    {
        boolean reachable = NetworkStatusCache.isReachable();
        System.out.println(reachable);
    }
    
    @Test
    public void setReachableTest()
    {
        NetworkStatusCache.setReachable(false);
        Assert.assertEquals(false, NetworkStatusCache.isReachable());
    }
}
