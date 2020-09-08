package com.huawei.sharedrive.app.core.domain;

import org.junit.Test;
import pw.cdmi.box.domain.Order;

public class OrderTest
{
    @Test
    public void orderTest()
    {
        Order order = new Order();
        String direction = order.getDirection();
        System.out.println(direction);
    }
    
    @Test
    public void orderTest2()
    {
        Order order = new Order();
        String direction = order.getField();
        System.out.println(direction);
    }
    
    @Test
    public void orderTest3()
    {
        Order order = new Order();
        Class<? extends Order> class1 = order.getClass();
        System.out.println(class1);
    }
    
    @Test
    public void orderTest4()
    {
        Order order = new Order();
        order.setDirection("file");
    }
    
    @Test
    public void orderTest5()
    {
        Order order = new Order();
        order.setField("file");
    }
    
    @Test
    public void orderTest6()
    {
        Order order = new Order();
        boolean desc = order.isDesc();
        System.out.println(desc);
    }
}
