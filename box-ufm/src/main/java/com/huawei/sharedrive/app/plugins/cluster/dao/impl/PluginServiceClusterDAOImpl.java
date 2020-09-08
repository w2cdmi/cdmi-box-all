package com.huawei.sharedrive.app.plugins.cluster.dao.impl;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.huawei.sharedrive.app.plugins.cluster.dao.PluginServiceClusterDAO;
import com.huawei.sharedrive.app.plugins.cluster.domain.PluginServiceCluster;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Repository
@SuppressWarnings("deprecation")
public class PluginServiceClusterDAOImpl extends AbstractDAOImpl implements PluginServiceClusterDAO
{
    
    @Override
    public int create(PluginServiceCluster cluster)
    {
       return (int) sqlMapClientTemplate.insert("PluginServiceCluster.create", cluster);
    }
    
    @Override
    public int delete(int clusterId)
    {
        PluginServiceCluster cluster = new PluginServiceCluster();
        cluster.setClusterId(clusterId);
        return sqlMapClientTemplate.delete("PluginServiceCluster.delete", cluster);
    }
    
    @Override
    public PluginServiceCluster get(int clusterId)
    {
        return (PluginServiceCluster) sqlMapClientTemplate.queryForObject("PluginServiceCluster.get",
            clusterId);
    }
    
    @Override
    public PluginServiceCluster getByAppIdAndRouteInfo(int dssId, String appId)
    {
        PluginServiceCluster cluster = new PluginServiceCluster();
        cluster.setDssId(dssId);
        cluster.setAppId(appId);
        return (PluginServiceCluster) sqlMapClientTemplate.queryForObject("PluginServiceCluster.getByAppIdAndRouteInfo",
            cluster);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<PluginServiceCluster> listByAppId(String appId)
    {
        PluginServiceCluster cluster = new PluginServiceCluster();
        cluster.setAppId(appId);
        return sqlMapClientTemplate.queryForList("PluginServiceCluster.listByAppId", cluster);
    }
    
    @Override
    public int update(PluginServiceCluster cluster)
    {
        return sqlMapClientTemplate.update("PluginServiceCluster.update", cluster);
    }

    @Override
    public int updateStatusAndLastMonitorTime(byte status, Date lastMonitorTime, int clusterId)
    {
        PluginServiceCluster cluster = new PluginServiceCluster();
        cluster.setClusterId(clusterId);
        cluster.setStatus(status);
        cluster.setLastMonitorTime(lastMonitorTime);
        return sqlMapClientTemplate.update("PluginServiceCluster.updateStatusAndLastMonitorTime", cluster);
    }
    
}
