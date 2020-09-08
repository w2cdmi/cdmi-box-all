package com.huawei.sharedrive.isystem.core.domain.test;

import org.junit.Test;

import pw.cdmi.box.domain.Order;
import pw.cdmi.box.domain.PageRequest;

public class PageRequestTest
{
    @Test
    public void pageTest()
    {
        new PageRequest(9, 9);
        PageRequest p = new PageRequest(9, 9, new Order());
        
        p.getPageSize();
        
        p.getPageNumber();
        
        p.getLimit();
        
        p.getOrder();
        
        p.setPage(45);
        
        p.setSize(45);
        
        p.setOrder(new Order("field", true));
        
        new PageRequest();
        try
        {
            new PageRequest(-9, 9, new Order());
            new PageRequest(9, -9, new Order());
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
