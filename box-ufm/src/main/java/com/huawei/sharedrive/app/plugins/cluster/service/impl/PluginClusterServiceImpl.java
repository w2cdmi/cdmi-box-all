package com.huawei.sharedrive.app.plugins.cluster.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.plugins.cluster.dao.PluginServiceClusterDAO;
import com.huawei.sharedrive.app.plugins.cluster.dao.PluginServiceInstanceDAO;
import com.huawei.sharedrive.app.plugins.cluster.dao.PluginServiceRouterDAO;
import com.huawei.sharedrive.app.plugins.cluster.domain.PluginServiceCluster;
import com.huawei.sharedrive.app.plugins.cluster.domain.PluginServiceInstance;
import com.huawei.sharedrive.app.plugins.cluster.domain.PluginServiceRouter;
import com.huawei.sharedrive.app.plugins.cluster.service.PluginClusterService;
import com.huawei.sharedrive.thrift.plugins.agent.TWorkerNode;

import pw.cdmi.core.utils.DateUtils;

@Service("pluginClusterService")
public class PluginClusterServiceImpl implements PluginClusterService
{
    public static final Logger LOGGER = LoggerFactory.getLogger(PluginClusterServiceImpl.class);
    
    @Autowired
    private PluginServiceClusterDAO pluginServiceClusterDAO;
    
    @Autowired
    private PluginServiceRouterDAO pluginServiceRouterDAO;
    
    @Autowired
    private PluginServiceInstanceDAO pluginServiceInstanceDAO;
    
    @Override
    public List<PluginServiceRouter> getListConvertDss(int clusterId) throws BaseRunException
    {
        return pluginServiceRouterDAO.listByClusterId(clusterId);
    }
    
    @Override
    public List<PluginServiceRouter> getByAppID(String appId) throws BaseRunException
    {
        List<PluginServiceCluster> clusters = pluginServiceClusterDAO.listByAppId(appId);
        List<PluginServiceRouter> list = new ArrayList<PluginServiceRouter>(0);
        List<PluginServiceRouter> routers = null;
        for (PluginServiceCluster cluster : clusters)
        {
            routers = pluginServiceRouterDAO.listByClusterId(cluster.getClusterId());
            list.addAll(routers);
        }
        return list;
    }
    
    @Override
    public void ceateRouters(List<PluginServiceRouter> routers)
    {
        for (PluginServiceRouter router : routers)
        {
            pluginServiceRouterDAO.create(router);
        }
    }
    
    @Override
    public void deletePluginServiceRouter(List<PluginServiceRouter> routers)
    {
        for (PluginServiceRouter router : routers)
        {
            pluginServiceRouterDAO.delete(router.getDssId(), router.getClusterId());
        }
    }
    
    @Override
    public List<PluginServiceCluster> getListPluginServiceCluster(String appId) throws BaseRunException
    {
        return pluginServiceClusterDAO.listByAppId(appId);
    }
    
    @Override
    public PluginServiceCluster getByAppIdAndRouteInfo(int dssId, String appId) throws BaseRunException
    {
        return pluginServiceClusterDAO.getByAppIdAndRouteInfo(dssId, appId);
    }
    
    @Override
    public void createPluginServiceCluster(PluginServiceCluster cluster, List<PluginServiceRouter> routers,
        List<TWorkerNode> list) throws BaseRunException
    {
        Date lastMonitorTime = new Date();
        cluster.setLastMonitorTime(lastMonitorTime);
        int clusterId = pluginServiceClusterDAO.create(cluster);
        PluginServiceRouter dssRouter = new PluginServiceRouter();
        dssRouter.setClusterId(clusterId);
        dssRouter.setDssId(cluster.getDssId());
        if (null != routers)
        {
            
            for (PluginServiceRouter router : routers)
            {
                router.setClusterId(clusterId);
            }
            
        }
        else
        {
            routers = new ArrayList<PluginServiceRouter>(1);
        }
        PluginServiceInstance pluginServiceInstance = null;
        for (TWorkerNode node : list)
        {
            pluginServiceInstance = new PluginServiceInstance(node.getIp(), clusterId, node.getName());
            pluginServiceInstance.setStatus(node.getStatus());
            pluginServiceInstance.setLastMonitorTime(lastMonitorTime);
            createPluginServiceInstance(pluginServiceInstance);
        }
        routers.add(dssRouter);
        ceateRouters(routers);
        
    }
    
    @Override
    public void deletePluginServiceCluster(PluginServiceCluster cluster) throws BaseRunException
    {
        List<PluginServiceRouter> routers = pluginServiceRouterDAO.listByClusterId(cluster.getClusterId());
        List<PluginServiceInstance> instances = listPluginServiceInstance(cluster.getClusterId());
        deletePluginServiceRouter(routers);
        deleltePluginServiceInstance(instances);
        pluginServiceClusterDAO.delete(cluster.getClusterId());
        
    }
    
    @Override
    public void deleltePluginServiceInstance(List<PluginServiceInstance> instances)
    {
        for (PluginServiceInstance instance : instances)
        {
            pluginServiceInstanceDAO.delete(instance);
        }
    }
    
    @Override
    public void updatePluginServiceCluster(PluginServiceCluster cluster, List<PluginServiceRouter> crouters,
        List<PluginServiceRouter> drouters)
    {
        
        PluginServiceCluster temp = pluginServiceClusterDAO.getByAppIdAndRouteInfo(cluster.getDssId(),
            cluster.getAppId());
        cluster.setClusterId(temp.getClusterId());
        pluginServiceClusterDAO.update(cluster);
        if (null != drouters)
        {
            for (PluginServiceRouter router : drouters)
            {
                router.setClusterId(temp.getClusterId());
            }
        }
        if (null != crouters)
        {
            for (PluginServiceRouter router : crouters)
            {
                router.setClusterId(temp.getClusterId());
            }
        }
        if (null != drouters)
        {
            deletePluginServiceRouter(drouters);
        }
        if (null != crouters)
        {
            ceateRouters(crouters);
        }
    }
    
    @Override
    public void saveOrupdatePluginServiceCluster(PluginServiceCluster cluster,
        List<PluginServiceRouter> crouters, List<PluginServiceRouter> drouters, List<TWorkerNode> list)
        throws BaseRunException, TException
    {
        LOGGER.info("dcId:" + cluster.getDssId() + "AppID:" + cluster.getAppId());
        PluginServiceCluster temp = pluginServiceClusterDAO.getByAppIdAndRouteInfo(cluster.getDssId(),
            cluster.getAppId());
        
        if (null == temp)
        {
            if (drouters == null || drouters.isEmpty())
            {
                createPluginServiceCluster(cluster, crouters, list);
            }
            else
            {
                LOGGER.info("create cluster  The drouters sise must be 0 [{}]", drouters.size());
                for (PluginServiceRouter r : drouters)
                {
                    LOGGER.info("drouters dcID:" + r.getDssId());
                }
                throw new TException("Create cluster failed");
            }
        }
        else
        {
            temp.setName(cluster.getName());
            temp.setDescription(cluster.getDescription());
            temp.setMonitorPeriod(cluster.getMonitorPeriod());
            if (DateUtils.getDateTime(cluster.getLastMonitorTime()) != 0)
            {
                temp.setLastMonitorTime(cluster.getLastMonitorTime());
            }
            updatePluginServiceCluster(temp, crouters, drouters);
            
        }
        
    }
    
    @Override
    public List<PluginServiceInstance> listPluginServiceInstance(int clusterId)
    {
        List<PluginServiceInstance> instances = pluginServiceInstanceDAO.listByClusterId(clusterId, (byte) 0);
        return instances;
    }
    
    @Override
    public PluginServiceCluster getClusterbyId(int clusterId)
    {
        return pluginServiceClusterDAO.get(clusterId);
    }
    
    @Override
    public int updatePluginServiceInstance(PluginServiceInstance instance)
    {
        return pluginServiceInstanceDAO.updateInstance(instance);
    }
    
    @Override
    public void createPluginServiceInstance(PluginServiceInstance instance)
    {
        pluginServiceInstanceDAO.create(instance);
    }
    
}
