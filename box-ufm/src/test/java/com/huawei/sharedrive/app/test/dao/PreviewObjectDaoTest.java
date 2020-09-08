package com.huawei.sharedrive.app.test.dao;

import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.ConcurrencyFailureException;

import com.huawei.sharedrive.app.plugins.preview.dao.PreviewObjectDao;
import com.huawei.sharedrive.app.plugins.preview.domain.PreviewObject;
import com.huawei.sharedrive.app.test.other.AbstractSpringTest;

public class PreviewObjectDaoTest extends AbstractSpringTest
{
    
    @Autowired
    private PreviewObjectDao previewObjectDAO;
    
    @Test
    public void testCreate()
    {
        PreviewObject previewObject = new PreviewObject();
        previewObject.setSourceObjectId("aaaa");
        previewObject.setAccountId(0);
        previewObject.setConvertStartTime(new Date());
        previewObject.setStatus(PreviewObject.STATUS_CREATING);
        try
        {
            previewObjectDAO.create(previewObject);
            System.out.println(previewObject.getTableSuffix());
        }
        catch (ConcurrencyFailureException e)
        {
            System.out.println(e.getRootCause().getMessage());
            // TODO Auto-generated catch block
            // e.printStackTrace();
        }
    }
    
    @Test
    public void testSelectForUpdate()
    {
        PreviewObject previewObject = previewObjectDAO.selectForUpdate("432306ae2dd3f921d9a9bd79c5e0613b", 1);
        System.out.println(previewObject);
    }
    
    @Test
    public void testGet()
    {
        PreviewObject previewObject = previewObjectDAO.get("432306ae2dd3f921d9a9bd79c5e0613b", 1);
        System.out.println(previewObject);
    }
}
