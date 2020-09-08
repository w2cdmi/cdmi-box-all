package com.huawei.sharedrive.app.openapi.domain.user.test;

import java.util.ArrayList;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.openapi.domain.user.ListUserRequest;
import com.huawei.sharedrive.app.openapi.domain.user.SetUserAttributeRequest;
import com.huawei.sharedrive.app.openapi.domain.user.UserAttribute;
import pw.cdmi.box.domain.Order;

public class OrderTest
{
    @Test
    public void paramterTest()
    {
        try
        {
            Order o = new Order();
            o.setField("field");
            o.getField();
            o.setDirection("direction");
            o.getDirection();
            o.isDesc();
            o.hashCode();
            o.equals(new Order());
            new Order("field", null);
            o.checkParameter();
        }
        catch (InvalidParamException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    public void paramterTest1()
    {
        try
        {
            ListUserRequest userRequest = new ListUserRequest();
            userRequest.getLimit();
            userRequest.getOffset();
            userRequest.getOrder();
            userRequest.setLimit(123);
            userRequest.setOffset(123L);
            new ListUserRequest(123, 123L);
            userRequest.addOrder(null);
            userRequest.addOrder(new Order());
            userRequest.setOrder(new ArrayList<Order>());
            userRequest.addOrder(new Order());
            userRequest.checkParameter();
        }
        catch (InvalidParamException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    public void paramterTest2()
    {
        SetUserAttributeRequest userRequest = new SetUserAttributeRequest();
        userRequest.getName();
        userRequest.getValue();
        try
        {
            userRequest.checkParameter();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        userRequest.setName("name");
        try
        {
            userRequest.checkParameter();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        userRequest.setValue("value");
        try
        {
            userRequest.checkParameter();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        userRequest.setName("messageNotice");
        try
        {
            userRequest.checkParameter();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        UserAttribute.getUserAttribute("messageNotice");
    }
}
