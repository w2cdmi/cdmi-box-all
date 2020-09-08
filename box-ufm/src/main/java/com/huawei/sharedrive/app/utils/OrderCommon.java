package com.huawei.sharedrive.app.utils;

import java.util.List;

import pw.cdmi.box.domain.Order;

public final class OrderCommon
{
    private OrderCommon()
    {
    }
    
    public static String getOrderByStr(List<Order> orderList)
    {
        StringBuffer orderBy = new StringBuffer();
        String field = null;
        for (Order order : orderList)
        {
            field = order.getField();
            
            // 解决中文名称排序问题
            if ("name".equalsIgnoreCase(field))
            {
                field = "convert(name using gb2312)";
            }
            orderBy.append(field).append(' ').append(order.getDirection()).append(',');
        }
        orderBy = orderBy.deleteCharAt(orderBy.length() - 1);
        return orderBy.toString();
    }
    
    public static String getOrderByStr(List<Order> orderList,String subbfix)
    {
        StringBuffer orderBy = new StringBuffer();
        String field = null;
        for (Order order : orderList)
        {
            field = order.getField();
            
            // 解决中文名称排序问题
            if ("name".equalsIgnoreCase(field))
            {
                field = "convert(name using gb2312)";
            }
            orderBy.append(subbfix).append(".").append(field).append(' ').append(order.getDirection()).append(',');
        }
        orderBy = orderBy.deleteCharAt(orderBy.length() - 1);
        return orderBy.toString();
    }
}
