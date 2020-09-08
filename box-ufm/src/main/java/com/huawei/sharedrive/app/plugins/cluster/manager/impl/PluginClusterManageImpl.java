package com.huawei.sharedrive.app.plugins.cluster.manager.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.app.dataserver.domain.ResourceGroup;
import com.huawei.sharedrive.app.dataserver.service.ResourceGroupService;
import com.huawei.sharedrive.app.dataserver.thrift.client.PluginServiceClusterThriftServiceClient;
import com.huawei.sharedrive.app.dns.service.DssDomainService;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.plugins.cluster.domain.PluginServiceCluster;
import com.huawei.sharedrive.app.plugins.cluster.domain.PluginServiceInstance;
import com.huawei.sharedrive.app.plugins.cluster.domain.PluginServiceRouter;
import com.huawei.sharedrive.app.plugins.cluster.manager.PluginClusterManage;
import com.huawei.sharedrive.app.plugins.cluster.manager.PluginServerJobManager;
import com.huawei.sharedrive.app.plugins.cluster.service.PluginClusterService;
import com.huawei.sharedrive.thrift.plugins.agent.TWorkerNode;
import com.huawei.sharedrive.thrift.pluginserver.TPluginServerCluster;
import com.huawei.sharedrive.thrift.pluginserver.TPluginServerInstance;
import com.huawei.sharedrive.thrift.pluginserver.TPluginServiceRouter;

import pw.cdmi.common.job.exception.JobException;
import pw.cdmi.core.exception.InnerException;
import pw.cdmi.core.utils.JsonUtils;

@Service("pluginClusterManage")
@Lazy(false)
public class PluginClusterManageImpl implements PluginClusterManage
{
    private static final Logger LOGGER = LoggerFactory.getLogger(PluginClusterManageImpl.class);
    
    @Autowired
    private PluginClusterService pluginClusterService;
    
    @Autowired
    private PluginServerJobManager pluginServerJobManager;
    
    @Autowired
    private ResourceGroupService resourceGroupService;
    
    @Autowired
    private DssDomainService dssDomainService;
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delPluginServerCluster(TPluginServerCluster tCluster) throws JobException, BaseRunException
    {
        PluginServiceCluster cluster = (PluginServiceCluster) classCovent(PluginServiceCluster.class,
            tCluster);
        cluster = pluginClusterService.getClusterbyId(cluster.getClusterId());
        pluginClusterService.deletePluginServiceCluster(cluster);
        pluginServerJobManager.deleteJobManager(cluster);
        
    }
    
    @Override
    public List<TPluginServerInstance> getListPluginServerInstance(long clusterId) throws BaseRunException
    {
        List<PluginServiceInstance> list = pluginClusterService.listPluginServiceInstance((int) clusterId);
        List<TPluginServerInstance> instances = coventInstanceToThift(list);
        return instances;
    }
    
    @Override
    public List<TPluginServiceRouter> getPluginServiceRouter(TPluginServerCluster tCluster)
        throws BaseRunException
    {
        PluginServiceCluster temp = pluginClusterService.getByAppIdAndRouteInfo(tCluster.getDssId(),
            tCluster.getAppId());
        List<PluginServiceRouter> routers = null;
        if (null == temp)
        {
            if (tCluster.getClusterId() < 0)
            {
                return new ArrayList<TPluginServiceRouter>(0);
            }
            else if (tCluster.getClusterId() == 0)
            {
                routers = pluginClusterService.getByAppID(tCluster.getAppId());
            }
        }
        else
        {
            routers = pluginClusterService.getListConvertDss((int) tCluster.getClusterId());
        }
        
        List<TPluginServiceRouter> trouters = coventRouterToThift(routers);
        return trouters;
    }
    
    @Override
    public List<TPluginServerCluster> listPluginServerCluster(TPluginServerCluster tPluginServerCluster)
        throws BaseRunException
    {
        
        List<PluginServiceCluster> list = pluginClusterService.getListPluginServiceCluster(tPluginServerCluster.getAppId());
        List<TPluginServerCluster> clusters = coventClusterToThift(list);
        return clusters;
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveOrUpdatePluginServerCluster(TPluginServerCluster cluster,
        List<TPluginServiceRouter> cRouters, List<TPluginServiceRouter> dRouters) throws BaseRunException,
        TException, JobException
    {
        PluginServiceCluster serviceCluster = (PluginServiceCluster) classCovent(PluginServiceCluster.class,
            cluster);
        
        List<TWorkerNode> list = getListWorker(serviceCluster);
        
        if (list.isEmpty())
        {
            LOGGER.debug("WrokerList is null");
            throw new InnerException("master dc is not keep alive");
            
        }
        List<PluginServiceRouter> addrouters = coventRouterTodomain(cRouters);
        List<PluginServiceRouter> delrouters = coventRouterTodomain(dRouters);
        
        pluginClusterService.saveOrupdatePluginServiceCluster(serviceCluster, addrouters, delrouters, list);
        PluginServiceCluster temp = pluginClusterService.getByAppIdAndRouteInfo(serviceCluster.getDssId(),
            serviceCluster.getAppId());
        pluginServerJobManager.addJobManager(temp);
    }
    
    @SuppressWarnings("rawtypes")
    private Object classCovent(Class clz, Object objct)
    {
        String json = JsonUtils.toJson(objct);
        LOGGER.debug(clz + json);
        @SuppressWarnings("unchecked")
        Object obj = JsonUtils.stringToObject(json, clz);
        return obj;
    }
    
    private List<TPluginServerCluster> coventClusterToThift(List<PluginServiceCluster> list)
    {
        List<TPluginServerCluster> clusters = new ArrayList<TPluginServerCluster>(list.size());
        TPluginServerCluster tPluginServerCluster = null;
        for (PluginServiceCluster cluster : list)
        {
            tPluginServerCluster = (TPluginServerCluster) classCovent(TPluginServerCluster.class, cluster);
            clusters.add(tPluginServerCluster);
        }
        return clusters;
    }
    
    private List<TPluginServerInstance> coventInstanceToThift(List<PluginServiceInstance> instances)
    {
        List<TPluginServerInstance> coventInstances = new ArrayList<TPluginServerInstance>(instances.size());
        TPluginServerInstance r = null;
        for (PluginServiceInstance instance : instances)
        {
            r = (TPluginServerInstance) classCovent(TPluginServerInstance.class, instance);
            coventInstances.add(r);
        }
        return coventInstances;
    }
    
    private List<PluginServiceRouter> coventRouterTodomain(List<TPluginServiceRouter> routers)
    {
        if (null != routers)
        {
            List<PluginServiceRouter> coventrouters = new ArrayList<PluginServiceRouter>(routers.size());
            PluginServiceRouter r = null;
            for (TPluginServiceRouter router : routers)
            {
                r = (PluginServiceRouter) classCovent(PluginServiceRouter.class, router);
                coventrouters.add(r);
            }
            return coventrouters;
        }
        return null;
    }
    
    private List<TPluginServiceRouter> coventRouterToThift(List<PluginServiceRouter> routers)
    {
        List<TPluginServiceRouter> coventrouters = new ArrayList<TPluginServiceRouter>(0);
        if (null != routers)
        {
            TPluginServiceRouter r = null;
            for (PluginServiceRouter router : routers)
            {
                r = (TPluginServiceRouter) classCovent(TPluginServiceRouter.class, router);
                coventrouters.add(r);
            }
        }
        return coventrouters;
    }
    
    private List<TWorkerNode> getListWorker(PluginServiceCluster serviceCluster) throws TException
    {
        ResourceGroup resourceGroup = resourceGroupService.getResourceGroup(serviceCluster.getDssId());
        
        PluginServiceClusterThriftServiceClient client = null;
        try
        {
            String domain = dssDomainService.getDomainByDssId(resourceGroup);
            LOGGER.debug("[{}:{}] entry key:{}",
                resourceGroup.getManageIp(),
                resourceGroup.getManagePort(),
                PluginServiceClusterThriftServiceClient.isKiA(serviceCluster));
            client = new PluginServiceClusterThriftServiceClient(domain, resourceGroup.getManagePort(),
                PluginServiceClusterThriftServiceClient.isKiA(serviceCluster));
            List<TWorkerNode> list = client.getWrokerList();
            for (TWorkerNode node : list)
            {
                LOGGER.debug(" ip[" + node.getIp() + "] name[" + node.getName() + "] status["
                    + node.getStatus() + ']');
            }
            return list;
        }
        finally
        {
            if (null != client)
            {
                client.close();
            }
        }
    }
    
}
