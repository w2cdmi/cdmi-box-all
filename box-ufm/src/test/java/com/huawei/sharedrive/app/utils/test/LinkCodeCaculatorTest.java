package com.huawei.sharedrive.app.utils.test;

import junit.framework.Assert;

import org.junit.Test;

import com.huawei.sharedrive.app.utils.LinkCodeCaculator;

public class LinkCodeCaculatorTest
{
    @Test
    public void buildAccessCodeTest()
    {
        String accessCode = LinkCodeCaculator.buildAccessCode();
        System.out.println(accessCode);
        Assert.assertEquals(false, "360242".equals(accessCode));
    }
    @Test
    public void buildLinkIDTest()
    {
        String accessCode = LinkCodeCaculator.buildLinkID();
        System.out.println(accessCode);
        Assert.assertEquals(false, "360242".equals(accessCode));
    }
    @Test
    public void getINodeIdTest()
    {
        long nodeId = LinkCodeCaculator.getINodeId("linkCode");
        System.out.println(nodeId);
        Assert.assertEquals(3054594, nodeId);
    }
    @Test
    public void getOwnerIdTest()
    {
        long nodeId = LinkCodeCaculator.getOwnerId("linkCode");
        System.out.println(nodeId);
        Assert.assertEquals(11373636, nodeId);
    }
    
    @Test
    public void getOwnerIdTest1()
    {
        long nodeId = LinkCodeCaculator.getOwnerId("1234567");
        System.out.println(nodeId);
        Assert.assertEquals(246206, nodeId);
    }
}
