package com.huawei.sharedrive.app.utils.test;

import org.junit.Test;

import junit.framework.Assert;

import com.huawei.sharedrive.app.utils.GroupInfoCommon;

public class GroupInfoCommonTest
{
    @Test
    public void junitTest()
    {
        GroupInfoCommon g = new GroupInfoCommon();
        g.getId();
        
        g.setId(123123214L);
        
        Assert.assertEquals(123123214L, g.getId());
        
        g.setName("tom");
        
        Assert.assertEquals("tom", g.getName());
    }
}
