package com.huawei.sharedrive.app.utils.test;

import org.junit.Test;

import com.huawei.sharedrive.app.utils.BusinessConstants;

import junit.framework.Assert;

public class BusinessConstantsTest
{
    @Test
    public void junitTest()
    {
        Assert.assertEquals(10, BusinessConstants.INITIAL_CAPACITIES);
          
        Assert.assertEquals("1", BusinessConstants.ID_TEAM_PUBLIC);
    }
}
