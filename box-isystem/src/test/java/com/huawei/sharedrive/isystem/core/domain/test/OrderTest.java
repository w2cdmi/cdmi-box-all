package com.huawei.sharedrive.isystem.core.domain.test;

import org.junit.Test;

import junit.framework.Assert;
import pw.cdmi.box.domain.Order;

public class OrderTest
{
    @Test
    public void junitTest()
    {
        Order o = new Order();
        
        new Order("delete", true);
        
        Assert.assertEquals(null, o.getField());
        
        o.setField("field");
        
        o.setField(null);
        
        Assert.assertEquals(false, o.isDesc());
        
        o.setDesc(true);
        
        o.equals(new Order());
        
        o.hashCode();
    }
}
