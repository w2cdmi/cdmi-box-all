package com.huawei.sharedrive.app.test.dao.plugins;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.huawei.sharedrive.app.plugins.cluster.dao.PluginServiceRouterDAO;
import com.huawei.sharedrive.app.plugins.cluster.domain.PluginServiceRouter;
import com.huawei.sharedrive.app.test.other.AbstractSpringTest;

public class PluginServiceRouterTest extends AbstractSpringTest
{
    
    @Autowired
    private PluginServiceRouterDAO pluginServiceRouterDAO;
    
    @Test
    public void testCreate()
    {
        PluginServiceRouter router = new PluginServiceRouter();
        router.setDssId(10);
        router.setClusterId(2);
        pluginServiceRouterDAO.create(router);
    }
    
    
    @Test
    public void testListByClusterId()
    {
        List<PluginServiceRouter> list = pluginServiceRouterDAO.listByClusterId(2);
        System.out.println(list.size());
        Assert.assertTrue(list.size() > 0);
    }
    
    @Test
    public void testDelete()
    {
        int result = pluginServiceRouterDAO.delete(10, 2);
        Assert.assertTrue(result == 1);
    }
}
