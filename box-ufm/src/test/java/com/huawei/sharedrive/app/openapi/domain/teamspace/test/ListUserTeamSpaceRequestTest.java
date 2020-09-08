package com.huawei.sharedrive.app.openapi.domain.teamspace.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.openapi.domain.teamspace.ListUserTeamSpaceRequest;
import pw.cdmi.box.domain.Order;

public class ListUserTeamSpaceRequestTest
{
    @Test
    public void checkParameterTest()
    {
        try
        {
            ListUserTeamSpaceRequest br = new ListUserTeamSpaceRequest();
            br.setUserId(10000L);
            br.getUserId();
            br.setLimit(100);
            Assert.assertEquals((Integer)100, br.getLimit());
            br.setOffset(1000L);
            Assert.assertEquals((Long)1000L, br.getOffset());
            List<Order> order = new ArrayList<Order>();
            order.add(new Order());
            br.setOrder(order);
            br.checkParameter();
        }
        catch (InvalidParamException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
