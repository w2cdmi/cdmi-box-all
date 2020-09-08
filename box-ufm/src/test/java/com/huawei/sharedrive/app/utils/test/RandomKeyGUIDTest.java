package com.huawei.sharedrive.app.utils.test;

import junit.framework.Assert;

import org.junit.Test;

import com.huawei.sharedrive.app.utils.RandomKeyGUID;

public class RandomKeyGUIDTest
{
    @Test
    public void getSecureRandomGUIDTest()
    {
        String string = RandomKeyGUID.getSecureRandomGUID();
        System.out.println(string);
        Assert.assertEquals(false, "6D2E91B731F049E058EF83A824CF21588862B0BF2A5D40E41C5CA7F".equals(string));
    }
}
