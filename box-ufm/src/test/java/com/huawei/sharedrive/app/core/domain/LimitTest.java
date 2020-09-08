package com.huawei.sharedrive.app.core.domain;

import org.junit.Test;
import pw.cdmi.box.domain.Limit;

public class LimitTest
{
    @Test
    public void limitTest()
    {
        Limit limit = new Limit();
        limit.checkInnerParameter();
    }
    
    @Test
    public void limitTest2()
    {
        Limit limit = new Limit();
        limit.checkParameter();
    }
    
    @Test
    public void limitTest3()
    {
        Limit limit = new Limit();
        limit.equals(null);
    }
    
    @Test
    public void limitTest4()
    {
        Limit limit = new Limit();
        limit.getClass();
    }
    
    @Test
    public void limitTest5()
    {
        Limit limit = new Limit();
        limit.checkInnerParameter();
    }
    
    @Test
    public void limitTest6()
    {
        Limit limit = new Limit();
        limit.checkParameter();
    }
    
    @Test
    public void limitTest7()
    {
        Limit limit = new Limit();
        Integer length = limit.getLength();
        System.out.println(length);
    }
    
    @Test
    public void limitTest8()
    {
        Limit limit = new Limit();
        Long length = limit.getOffset();
        System.out.println(length);
    }
    
    @Test
    public void limitTest9()
    {
        Limit limit = new Limit();
        limit.setLength(null);
    }
    
    @Test
    public void limitTest10()
    {
        Limit limit = new Limit();
        limit.setOffset(0l);
    }
}
