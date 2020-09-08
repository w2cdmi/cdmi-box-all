package com.huawei.sharedrive.app.isystem.thrift;

import java.util.ArrayList;
import java.util.List;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.huawei.sharedrive.app.dataserver.domain.ResourceGroup;
import com.huawei.sharedrive.app.dataserver.service.ResourceGroupService;
import com.huawei.sharedrive.app.dataserver.thrift.client.PluginServiceClusterThriftServiceClient;
import com.huawei.sharedrive.app.dns.service.DssDomainService;
import com.huawei.sharedrive.app.plugins.cluster.domain.PluginServiceCluster;
import com.huawei.sharedrive.app.plugins.cluster.manager.PluginClusterManage;
import com.huawei.sharedrive.app.plugins.cluster.service.PluginClusterService;
import com.huawei.sharedrive.thrift.pluginserver.PluginServerThriftService;
import com.huawei.sharedrive.thrift.pluginserver.TAccessKey;
import com.huawei.sharedrive.thrift.pluginserver.TPluginServerCluster;
import com.huawei.sharedrive.thrift.pluginserver.TPluginServerInstance;
import com.huawei.sharedrive.thrift.pluginserver.TPluginServiceRouter;
import com.huawei.sharedrive.thrift.pluginserver.TWorkerNode;

import pw.cdmi.core.utils.JsonUtils;

public class PluginThriftServiceImpl implements PluginServerThriftService.Iface
{
    private static final Logger LOGGER = LoggerFactory.getLogger(PluginThriftServiceImpl.class);
    
    @Autowired
    private PluginClusterManage pluginClusterManage;
    
    @Autowired
    private ResourceGroupService resourceGroupService;
    
    @Autowired
    private PluginClusterService pluginClusterService;
    
    @Autowired
    private DssDomainService dssDomainService;
    
    @Override
    public void delPluginServerCluster(TPluginServerCluster tcluster) throws TException
    {
        try
        {
            pluginClusterManage.delPluginServerCluster(tcluster);
        }
        catch (Exception e)
        {
            LOGGER.info("Excepiton", e);
            throw new TException(e.getMessage(), e);
        }
        
    }
    
    @Override
    public List<TPluginServerInstance> getListPluginServerInstance(long clusterId) throws TException
    {
        try
        {
            return pluginClusterManage.getListPluginServerInstance(clusterId);
        }
        catch (Exception e)
        {
            LOGGER.info("Excepiton", e);
            throw new TException(e.getMessage(), e);
        }
    }
    
    @Override
    public List<TPluginServiceRouter> getPluginServiceRouter(TPluginServerCluster tCluster) throws TException
    {
        try
        {
            return pluginClusterManage.getPluginServiceRouter(tCluster);
        }
        catch (Exception e)
        {
            LOGGER.info("Excepiton", e);
            throw new TException(e.getMessage(), e);
        }
    }
    
    @Override
    public List<TPluginServerCluster> listPluginServerCluster(TPluginServerCluster tPluginServerCluster)
        throws TException
    {
        try
        {
            return pluginClusterManage.listPluginServerCluster(tPluginServerCluster);
        }
        catch (Exception e)
        {
            LOGGER.info("Excepiton", e);
            throw new TException(e.getMessage(), e);
        }
    }
    
    @Override
    public void setPluginServerCluster(TPluginServerCluster cluster, List<TPluginServiceRouter> cRouters,
        List<TPluginServiceRouter> dRouters) throws TException
    {
        try
        {
            pluginClusterManage.saveOrUpdatePluginServerCluster(cluster, cRouters, dRouters);
        }
        catch (Exception e)
        {
            LOGGER.info("Excepiton", e);
            throw new TException(e.getMessage(), e);
        }
        
    }
    
    @Override
    public List<TWorkerNode> getWrokerList(TPluginServerCluster tPluginServerCluster) throws TException
    {
        PluginServiceClusterThriftServiceClient pclient = null;
        try
        {
            pclient = getPluginServiceClusterThriftServiceClient(tPluginServerCluster);
            return coventWorkerNodes(pclient.getWrokerList());
        }
        finally
        {
            if (null != pclient)
            {
                pclient.close();
            }
        }
    }
    
    @Override
    public void setAccessKey(TAccessKey accessKey, TPluginServerCluster tPluginServerCluster)
        throws TException
    {
        PluginServiceClusterThriftServiceClient pclient = null;
        try
        {
            pclient = getPluginServiceClusterThriftServiceClient(tPluginServerCluster);
            pclient.setAccessKey((com.huawei.sharedrive.thrift.plugins.agent.TAccessKey) classCovent(com.huawei.sharedrive.thrift.plugins.agent.TAccessKey.class,
                accessKey));
        }
        finally
        {
            if (null != pclient)
            {
                pclient.close();
            }
        }
        
    }
    
    private PluginServiceClusterThriftServiceClient getPluginServiceClusterThriftServiceClient(
        TPluginServerCluster tPluginServerCluster) throws TException
    {
        PluginServiceCluster pluginServiceCluster = pluginClusterService.getClusterbyId((int) tPluginServerCluster.getClusterId());
        int dcId = 0;
        if (null == pluginServiceCluster)
        {
            LOGGER.info("PluginServiceCluster is null ,clusterId =[{}]", tPluginServerCluster.getClusterId());
            pluginServiceCluster = new PluginServiceCluster();
            pluginServiceCluster.setAppId(tPluginServerCluster.getAppId());
            dcId = tPluginServerCluster.getDssId();
        }
        else
        {
            dcId = pluginServiceCluster.getDssId();
        }
        ResourceGroup resourceGroup = resourceGroupService.getResourceGroup(dcId);
        String domain = dssDomainService.getDomainByDssId(resourceGroup);
        LOGGER.info("[{}:{}] entry key: {}",
            domain,
            resourceGroup.getManagePort(),
            PluginServiceClusterThriftServiceClient.isKiA(pluginServiceCluster));
        PluginServiceClusterThriftServiceClient pclient = new PluginServiceClusterThriftServiceClient(domain,
            resourceGroup.getManagePort(),
            PluginServiceClusterThriftServiceClient.isKiA(pluginServiceCluster));
        return pclient;
    }
    
    private List<TWorkerNode> coventWorkerNodes(
        List<com.huawei.sharedrive.thrift.plugins.agent.TWorkerNode> list)
    {
        List<TWorkerNode> nodes = new ArrayList<TWorkerNode>(0);
        if (list == null)
        {
            return nodes;
        }
        TWorkerNode node = null;
        for (com.huawei.sharedrive.thrift.plugins.agent.TWorkerNode l : list)
        {
            node = (TWorkerNode) classCovent(TWorkerNode.class, l);
            nodes.add(node);
        }
        return nodes;
    }
    
    @SuppressWarnings("rawtypes")
    private Object classCovent(Class clz, Object objct)
    {
        String json = JsonUtils.toJson(objct);
        LOGGER.info(clz + json);
        @SuppressWarnings("unchecked")
        Object obj = JsonUtils.stringToObject(json, clz);
        return obj;
    }
}
