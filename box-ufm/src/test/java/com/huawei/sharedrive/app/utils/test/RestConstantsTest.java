package com.huawei.sharedrive.app.utils.test;

import org.junit.Test;

import junit.framework.Assert;

import com.huawei.sharedrive.app.utils.RestConstants;

public class RestConstantsTest
{
    @Test
    public void test()
    {
        Assert.assertEquals("Authorization", RestConstants.HEADER_AUTHORIZATION);
        Assert.assertEquals("Date", RestConstants.HEADER_DATE);
        Assert.assertEquals("/api/v2/security", RestConstants.RESOURCE_SECURITY);
    }
}
