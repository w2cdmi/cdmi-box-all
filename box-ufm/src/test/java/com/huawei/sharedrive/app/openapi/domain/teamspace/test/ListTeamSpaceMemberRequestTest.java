package com.huawei.sharedrive.app.openapi.domain.teamspace.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.openapi.domain.teamspace.ListTeamSpaceMemberRequest;
import pw.cdmi.box.domain.Order;

public class ListTeamSpaceMemberRequestTest
{
    @Test
    public void checkParameterTest()
    {
        try
        {
            ListTeamSpaceMemberRequest br = new ListTeamSpaceMemberRequest();
            ListTeamSpaceMemberRequest br1 = new ListTeamSpaceMemberRequest(100, 1000L);
            br1.setKeyword("key");
            br1.getKeyword();
            br1.setTeamRole("admin");
            br1.getTeamRole();
            br.setLimit(100);
            Assert.assertEquals((Integer) 100, br.getLimit());
            br.setOffset(1000L);
            Assert.assertEquals((Long) 1000L, br.getOffset());
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
