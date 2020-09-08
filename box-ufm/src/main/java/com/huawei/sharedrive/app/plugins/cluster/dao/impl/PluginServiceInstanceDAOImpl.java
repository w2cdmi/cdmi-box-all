package com.huawei.sharedrive.app.plugins.cluster.dao.impl;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.huawei.sharedrive.app.plugins.cluster.dao.PluginServiceInstanceDAO;
import com.huawei.sharedrive.app.plugins.cluster.domain.PluginServiceInstance;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Repository
@SuppressWarnings("deprecation")
public class PluginServiceInstanceDAOImpl extends AbstractDAOImpl implements PluginServiceInstanceDAO
{
    
    @Override
    public void create(PluginServiceInstance instance)
    {
        sqlMapClientTemplate.insert("PluginServiceInstance.create", instance);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<PluginServiceInstance> listByClusterId(int clusterId, byte type)
    {
        PluginServiceInstance instance = new PluginServiceInstance();
        instance.setClusterId(clusterId);
        instance.setType(type);
        return sqlMapClientTemplate.queryForList("PluginServiceInstance.listByClusterId", instance);
    }
    
    @Override
    public int updateStatusAndLastMonitorTime(byte status, Date lastMonitorTime, String ip, int clusterId)
    {
        PluginServiceInstance instance = new PluginServiceInstance();
        instance.setStatus(status);
        instance.setLastMonitorTime(lastMonitorTime);
        instance.setIp(ip);
        instance.setClusterId(clusterId);
        return sqlMapClientTemplate.update("PluginServiceInstance.updateStatusAndLastMonitorTime", instance);
    }
    @Override
    public int updateInstance(PluginServiceInstance instance)
    {
        return sqlMapClientTemplate.update("PluginServiceInstance.updateStatusAndLastMonitorTime", instance);
    }
    @Override
    public int delete(PluginServiceInstance instance)
    {
        return sqlMapClientTemplate.delete("PluginServiceInstance.delete", instance);
    }
    
    
}
