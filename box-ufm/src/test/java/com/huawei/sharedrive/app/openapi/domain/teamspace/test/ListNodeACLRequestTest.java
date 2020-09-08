package com.huawei.sharedrive.app.openapi.domain.teamspace.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import junit.framework.Assert;

import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.openapi.domain.teamspace.ListNodeACLRequest;
import pw.cdmi.box.domain.Order;

public class ListNodeACLRequestTest
{
    @Test
    public void checkParameterTest()
    {
        try
        {
            ListNodeACLRequest br = new ListNodeACLRequest();
            ListNodeACLRequest br1 = new ListNodeACLRequest(100, 1000L);
            br1.setNodeId(10000L);
            br1.getNodeId();
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
