package com.huawei.sharedrive.isystem.core.domain.test;

import org.junit.Test;

import junit.framework.Assert;
import pw.cdmi.box.domain.Limit;

public class LimitTest
{
    @Test
    public void limit()
    {
        Limit l = new Limit();
        
        Assert.assertEquals(100, Limit.DEFAULT_LENGTH.intValue());
        
        Assert.assertEquals(1000, Limit.MAX_LENGTH.intValue());        
        
        l.setOffset(6l);
        
        l.getOffset();  
        
        l.setLength(6);
        
        l.getLength();
    }
}
