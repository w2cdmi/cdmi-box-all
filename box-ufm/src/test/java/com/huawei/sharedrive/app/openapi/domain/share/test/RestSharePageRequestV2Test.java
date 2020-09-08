package com.huawei.sharedrive.app.openapi.domain.share.test;

import java.util.ArrayList;

import org.junit.Test;

import com.huawei.sharedrive.app.core.domain.Thumbnail;
import com.huawei.sharedrive.app.openapi.domain.share.RestSharePageRequestV2;
import pw.cdmi.box.domain.Order;

public class RestSharePageRequestV2Test
{
    @Test
    public void paramterTest()
    {
        RestSharePageRequestV2 v2 = new RestSharePageRequestV2();
        v2.getKeyword();
        v2.getLimit();
        v2.getOffset();
        v2.getOrder();
        v2.getOrderField();
        v2.getOrderList();
        try
        {
            v2.getPageNumber();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        v2.getPageSize();
        v2.getThumbnail();
        v2.isDesc();
        v2.setDesc(true);
        v2.setKeyword("keyword");
        v2.setLimit(123);
        v2.setOffset(123L);
        v2.setOrderField("orderField");
        v2.setOrderList(new ArrayList<Order>());
        v2.setThumbnail(new ArrayList<Thumbnail>());
        v2.hashCode();
        v2.equals(new RestSharePageRequestV2());
    }
}
