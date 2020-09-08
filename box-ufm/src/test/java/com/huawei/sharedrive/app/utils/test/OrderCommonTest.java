package com.huawei.sharedrive.app.utils.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.huawei.sharedrive.app.utils.OrderCommon;
import pw.cdmi.box.domain.Order;

public class OrderCommonTest
{
    @Test
    public void getOrderByStrTest()
    {
        List<Order> list = new ArrayList<Order>();
        Order order = new Order();
        order.setField("name");
        order.setDirection("strict");
        list.add(order);
        OrderCommon.getOrderByStr(list);
    }
}
