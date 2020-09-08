package com.huawei.sharedrive.app.openapi.domain.share.test;

import org.junit.Test;

import com.huawei.sharedrive.app.core.domain.OrderV1;
import com.huawei.sharedrive.app.openapi.domain.share.RestSharePageRequest;
import pw.cdmi.box.domain.Order;

public class RestSharePageRequestTest
{
    @Test
    public void paramterTest()
    {
        RestSharePageRequest rp = new RestSharePageRequest();
        rp.getOffset();
        rp.getOrder();
        rp.getOrderField();
        rp.getPageSize();
        rp.isDesc();
        rp.setDesc(true);
        rp.equals(new RestSharePageRequest());
        rp.getLimit();
        rp.setLimit(123);
        rp.getLimit();
        rp.setOffset(123);
        rp.equals(new RestSharePageRequest());
        rp.setOrder(new Order());
        rp.setOrderField("orderField");
        try
        {
            rp.getPageNumber();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        rp.hashCode();
        rp.equals(new RestSharePageRequest());
    }
}
