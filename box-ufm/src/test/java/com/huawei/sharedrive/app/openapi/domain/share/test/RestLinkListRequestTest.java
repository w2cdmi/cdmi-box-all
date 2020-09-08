package com.huawei.sharedrive.app.openapi.domain.share.test;

import java.util.ArrayList;

import org.junit.Test;

import com.huawei.sharedrive.app.core.domain.Thumbnail;
import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.openapi.domain.share.RestLinkListRequest;
import pw.cdmi.box.domain.Order;

public class RestLinkListRequestTest
{
    @Test
    public void paramterTest()
    {
        RestLinkListRequest rr = new RestLinkListRequest();
        rr.addThumbnail(null);
        rr.addThumbnail(new Thumbnail());
        rr.addOrder(null);
        rr.addOrder(new Order());
        rr.getLimit();
        rr.getOffset();
        rr.setLimit(123);
        rr.setOffset(123L);
        ArrayList<Order> list = new ArrayList<Order>();
        list.add(new Order());
        rr.setOrder(list);
        rr.getOrder();
        rr.getThumbnail();
        ArrayList<Thumbnail> list2 = new ArrayList<Thumbnail>();
        list2.add(new Thumbnail());
        rr.setThumbnail(list2);
        rr.getOwnedBy();
        rr.setOwnedBy(123L);
        rr.getKeyword();
        rr.setKeyword("keyword");
        rr.addOrder(new Order());
        rr.addThumbnail(new Thumbnail());
        new RestLinkListRequest(123, 123L);
        try
        {
            rr.checkParameter();
        }
        catch (InvalidParamException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try
        {
            rr.setOrder(null);
            rr.checkParameter();
        }
        catch (InvalidParamException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
