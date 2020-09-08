package com.huawei.sharedrive.app.core.domain;

import org.junit.Test;

public class OrderV1Test
{
    @Test
    public void orderV1Test()
    {
        OrderV1 order = new OrderV1();
        order.generateFileField();
    }
    
    @Test
    public void orderV1Test2()
    {
        OrderV1 order = new OrderV1();
        order.generateShareField();
    }
    
    @Test
    public void orderV1Test3()
    {
        OrderV1 order = new OrderV1();
        order.getClass();
    }
    
    @Test
    public void orderV1Test4()
    {
        OrderV1 order = new OrderV1();
        order.getField();
    }
    
    @Test
    public void orderV1Test5()
    {
        OrderV1 order = new OrderV1();
        order.setDesc(false);
    }
    
    @Test
    public void orderV1Test6()
    {
        OrderV1 order = new OrderV1();
        order.setField("file");
    }
    
    @Test
    public void orderV1Test7()
    {
        OrderV1 order = new OrderV1();
        order.equals(null);
    }
    
    @Test
    public void orderV1Test8()
    {
        OrderV1 order = new OrderV1();
        order.toString();
    }
}
