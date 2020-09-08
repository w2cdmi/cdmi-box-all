package com.huawei.sharedrive.app.test.dao.plugins;

import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.huawei.sharedrive.app.plugins.cluster.dao.PluginServiceInstanceDAO;
import com.huawei.sharedrive.app.plugins.cluster.domain.PluginServiceInstance;
import com.huawei.sharedrive.app.test.other.AbstractSpringTest;

public class PluginServiceInstanceTest extends AbstractSpringTest
{
    
    @Autowired
    private PluginServiceInstanceDAO pluginServiceInstanceDAO;
    
    @Test
    public void testCreate()
    {
        PluginServiceInstance instance = new PluginServiceInstance();
        instance.setIp("1.1.1.1");
        instance.setLastMonitorTime(new Date());
        instance.setClusterId(2);
        instance.setName("hostname");
        instance.setStatus((byte) 0);
        instance.setType(PluginServiceInstance.TYPE_WORKER);
        pluginServiceInstanceDAO.create(instance);
    }
    
    @Test
    public void testUpdateLastMonitorTime()
    {
        int result = pluginServiceInstanceDAO.updateStatusAndLastMonitorTime((byte) 1, new Date(), "1.1.1.1", 2);
        Assert.assertTrue(result == 1);
    }
    
    @Test
    public void testListByAppId()
    {
        List<PluginServiceInstance> list = pluginServiceInstanceDAO.listByClusterId(2, PluginServiceInstance.TYPE_WORKER);
        Assert.assertTrue(list.size() > 0);
    }
    
}
