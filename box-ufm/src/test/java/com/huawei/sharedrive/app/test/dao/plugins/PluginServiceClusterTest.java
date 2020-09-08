package com.huawei.sharedrive.app.test.dao.plugins;

import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.huawei.sharedrive.app.plugins.cluster.dao.PluginServiceClusterDAO;
import com.huawei.sharedrive.app.plugins.cluster.domain.PluginServiceCluster;
import com.huawei.sharedrive.app.test.other.AbstractSpringTest;

public class PluginServiceClusterTest extends AbstractSpringTest
{
    private static final String APP_ID = "appId";
    
    @Autowired
    private PluginServiceClusterDAO pluginServiceClusterDAO;
    
    @Test
    public void testCreate()
    {
        PluginServiceCluster cluster = new PluginServiceCluster();
        cluster.setAppId(APP_ID);
        cluster.setDescription("des");
        cluster.setDssId(1);
        cluster.setLastMonitorTime(new Date());
        cluster.setMonitorPeriod(60);
        cluster.setName("cluster1");
        cluster.setStatus(PluginServiceCluster.STATUS_NORMAL);
        pluginServiceClusterDAO.create(cluster);
    }
    
    @Test
    public void testGetById()
    {
        PluginServiceCluster result = pluginServiceClusterDAO.get(1);
        Assert.assertTrue(result != null);
    }
    
    @Test
    public void testUpdate()
    {
        PluginServiceCluster cluster = new PluginServiceCluster();
        cluster.setClusterId(1);
        cluster.setDescription("new_des");
        cluster.setDssId(2);
        cluster.setLastMonitorTime(new Date());
        cluster.setMonitorPeriod(160);
        cluster.setName("newCluster");
        cluster.setStatus(PluginServiceCluster.STATUS_ABNORMAL);
        int result = pluginServiceClusterDAO.update(cluster);
        Assert.assertTrue(result == 1);
    }
    
    @Test
    public void testUpdateLastMonitorTime()
    {
        int result = pluginServiceClusterDAO.updateStatusAndLastMonitorTime(PluginServiceCluster.STATUS_OFFLINE, new Date(), 2);
        Assert.assertTrue(result == 1);
    }
    
    @Test
    public void testListByAppId()
    {
        List<PluginServiceCluster> list = pluginServiceClusterDAO.listByAppId(APP_ID);
        Assert.assertTrue(list.size() > 0);
    }
    
    @Test
    public void testDelete()
    {
        int result = pluginServiceClusterDAO.delete(1);
        Assert.assertTrue(result == 1);
    }
    
    @Test
    public void testGetByAppIdAndRouteInfo()
    {
        PluginServiceCluster cluster = pluginServiceClusterDAO.getByAppIdAndRouteInfo(10, APP_ID);
        Assert.assertNotNull(cluster);
    }
}
