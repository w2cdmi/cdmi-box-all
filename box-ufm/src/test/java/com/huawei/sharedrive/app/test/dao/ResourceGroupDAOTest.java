package com.huawei.sharedrive.app.test.dao;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.huawei.sharedrive.app.dataserver.dao.ResourceGroupDao;
import com.huawei.sharedrive.app.dataserver.domain.ResourceGroup;
import com.huawei.sharedrive.app.test.other.AbstractSpringTest;

public class ResourceGroupDAOTest extends AbstractSpringTest
{
    
    @Autowired
    private ResourceGroupDao dataCenterDAO;
    
    @Test
    public void testCreate()
    {
        for (int i = 0; i < 100; i++)
        {
            int id = dataCenterDAO.getNextAvailableDataCenterId();
            ResourceGroup dataCenter = new ResourceGroup();
            dataCenter.setId(id);
            dataCenter.setRegionId(i);
            //dataCenter.setName(name + id);
            //dataCenter.setDescription(desc + i);
            dataCenterDAO.create(dataCenter);
            dataCenter = dataCenterDAO.get(id);
            //Assert.assertEquals(name + id, dataCenter.getName());
        }
    }
    
    @Test
    public void testUpdate()
    {
        List<ResourceGroup> list = dataCenterDAO.getAll();
        for (ResourceGroup dataCenter : list)
        {
            int id = dataCenter.getId();
            //dataCenter.setDescription("modifyDesc" + id);
            //dataCenter.setName("modifyName" + id);
            dataCenterDAO.update(dataCenter);
            dataCenter = dataCenterDAO.get(id);
            //Assert.assertEquals("modifyDesc" + id, dataCenter.getDescription());
        }
    }
    
    @Test
    public void testDelete()
    {
        List<ResourceGroup> list = dataCenterDAO.getAll();
        for (ResourceGroup dataCenter : list)
        {
            int id = dataCenter.getId();
            dataCenterDAO.delete(id);
            dataCenter = dataCenterDAO.get(id);
            Assert.assertNull(dataCenter);
        }
    }
    
    @Test
    public void testList()
    {
        List<ResourceGroup> list = dataCenterDAO.getAllByRegion(0);
        for (ResourceGroup dataCenter : list)
        {
            System.out.println(ToStringBuilder.reflectionToString(dataCenter));
        }
    }
}
