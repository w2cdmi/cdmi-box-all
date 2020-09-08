package com.huawei.sharedrive.isystem.core.domain.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;

public class PageImplTest
{
    @Test
    public void test()
    {
        try
        {
            List<String> ll = new ArrayList<String>();
            ll.add("test");
            new PageImpl<String>(ll, new PageRequest(), 67);
            new PageImpl<String>(ll);
            PageImpl<String> p = new PageImpl<String>(ll);
            p.getNumber();
            p.getSize();
            p.getTotalPages();
            p.getTotalElements();
            p.getNumberOfElements();
            p.hasPreviousPage();
            p.isFirstPage();
            p.hashCode();
            p.isLastPage();
            p.iterator();
            p.getContent();
            p.hasContent();
            p.getOrder();
            p.toString();
            p.equals(new PageImpl<String>(ll));
            p.hashCode();
            new PageImpl<String>(null, new PageRequest(), 67);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            try
            {
                new PageImpl<String>(null);
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
}
