package com.huawei.sharedrive.app.core.domain;

import org.junit.Test;
import pw.cdmi.box.domain.PageRequest;

public class PageRequestTest
{
    @Test
    public void pageRequestTest()
    {
        PageRequest obj = new PageRequest();
        obj.equals(null);
    }
    
    @Test
    public void pageRequestTest2()
    {
        PageRequest obj = new PageRequest();
        obj.getClass();
    }
    
    @Test
    public void pageRequestTest3()
    {
        PageRequest obj = new PageRequest();
        obj.getInnerLimit();
    }
    
    @Test
    public void pageRequestTest4()
    {
        PageRequest obj = new PageRequest();
        obj.getInnerLimit();
    }
    
    @Test
    public void pageRequestTest5()
    {
        PageRequest obj = new PageRequest();
        obj.getLimit();
    }
    
    @Test
    public void pageRequestTest6()
    {
        PageRequest obj = new PageRequest();
        obj.getOrder();
    }
    
    @Test
    public void pageRequestTest7()
    {
        PageRequest obj = new PageRequest();
        obj.getPageNumber();
    }
    
    @Test
    public void pageRequestTest8()
    {
        PageRequest obj = new PageRequest();
        obj.getPageSize();
    }

    @Test
    public void pageRequestTest10()
    {
        PageRequest obj = new PageRequest();
        obj.setPage(1);
    }
    
    @Test
    public void pageRequestTest11()
    {
        PageRequest obj = new PageRequest();
        obj.setSize(1);
    }
    
    @Test
    public void pageRequestTest12()
    {
        PageRequest obj = new PageRequest();
        obj.setOrder(null);
    }
}
