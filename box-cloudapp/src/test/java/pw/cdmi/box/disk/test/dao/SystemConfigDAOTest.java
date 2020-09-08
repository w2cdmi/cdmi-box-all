package pw.cdmi.box.disk.test.dao;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import pw.cdmi.box.disk.system.dao.SystemConfigDAO;
import pw.cdmi.box.disk.test.AbstractSpringTest;
import pw.cdmi.box.disk.utils.PropertiesUtils;
import pw.cdmi.common.domain.SystemConfig;

public class SystemConfigDAOTest extends AbstractSpringTest
{
    
    private String id = "testid.";
    
    private String value = "value";
    
    @Autowired
    private SystemConfigDAO systemConfigDAO;
    
    @Test
    public void testCreate()
    {
        for (int i = 0; i < 100; i++)
        {
            SystemConfig systemConfig = new SystemConfig();
            systemConfig.setId(id + i);
            systemConfig.setValue(value + i);
            systemConfigDAO.create(systemConfig);
            systemConfig = systemConfigDAO.get(id + i);
            Assert.assertEquals(value + i, systemConfig.getValue());
        }
    }
    
    @Test
    public void testUpdate()
    {
        for (int i = 0; i < 100; i++)
        {
            SystemConfig systemConfig = systemConfigDAO.get(id + i);
            systemConfig.setValue("modify" + i);
            systemConfigDAO.update(systemConfig);
            systemConfig = systemConfigDAO.get(id + i);
            Assert.assertEquals("modify" + i, systemConfig.getValue());
        }
    }
    
    @Test
    public void testGetByPrefix()
    {
        List<SystemConfig> list = systemConfigDAO.getByPrefix(PropertiesUtils.getProperty("defaultAppId"),
            null,
            id);
        for (SystemConfig systemConfig : list)
        {
            System.out.println(ToStringBuilder.reflectionToString(systemConfig));
        }
        
    }
    
    @Test
    public void testDelete()
    {
        for (int i = 0; i < 100; i++)
        {
            systemConfigDAO.delete(id + i);
            SystemConfig systemConfig = systemConfigDAO.get(id + i);
            Assert.assertNull(systemConfig);
        }
    }
    
}
