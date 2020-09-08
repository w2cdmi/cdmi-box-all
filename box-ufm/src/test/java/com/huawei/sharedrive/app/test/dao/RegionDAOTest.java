package com.huawei.sharedrive.app.test.dao;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.huawei.sharedrive.app.dataserver.dao.RegionDao;
import com.huawei.sharedrive.app.dataserver.domain.Region;
import com.huawei.sharedrive.app.test.other.AbstractSpringTest;

public class RegionDAOTest extends AbstractSpringTest
{
    
    private String desc = "testDesc_";
    
    private String name = "testRegion_";
    
    @Autowired
    private RegionDao regionDAO;
    
    @Test
    public void testCreate()
    {
        for (int i = 0; i < 100; i++)
        {
            int id = regionDAO.getNextAvailableRegionId();
            Region user = new Region();
            user.setId(id);
            user.setName(name + id);
            user.setDescription(desc + i);
            regionDAO.create(user);
            user = regionDAO.get(id);
            Assert.assertEquals(name + id, user.getName());
        }
    }
    
    @Test
    public void testUpdate()
    {
        List<Region> list = regionDAO.getAll();
        for (Region region : list)
        {
            int id = region.getId();
            region.setDescription("modifyDesc" + id);
            region.setName("modifyName" + id);
            regionDAO.update(region);
            region = regionDAO.get(id);
            Assert.assertEquals("modifyDesc" + id, region.getDescription());
        }
    }
    
    @Test
    public void testDelete()
    {
        List<Region> list = regionDAO.getAll();
        for (Region region : list)
        {
            int id = region.getId();
            regionDAO.delete(id);
            region = regionDAO.get(id);
            Assert.assertNull(region);
        }
    }
    
}
