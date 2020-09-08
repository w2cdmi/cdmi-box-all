package com.huawei.sharedrive.isystem.plugin.service.impl;

import java.util.List;

import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.isystem.plugin.service.PluginService;
import com.huawei.sharedrive.isystem.thrift.client.PluginThriftServiceClient;
import com.huawei.sharedrive.thrift.pluginserver.TAccessKey;
import com.huawei.sharedrive.thrift.pluginserver.TPluginServerCluster;
import com.huawei.sharedrive.thrift.pluginserver.TPluginServerInstance;
import com.huawei.sharedrive.thrift.pluginserver.TPluginServiceRouter;
import com.huawei.sharedrive.thrift.pluginserver.TWorkerNode;

import pw.cdmi.common.thrift.client.ThriftClientProxyFactory;

@Service("pluginService")
public class PluginServiceImpl implements PluginService
{
    @Autowired
    private ThriftClientProxyFactory ufmThriftClientProxyFactory;
    
    @Override
    public void delPluginServerCluster(TPluginServerCluster pluginServerCluster) throws TException
    {
        
        ufmThriftClientProxyFactory.getProxy(PluginThriftServiceClient.class)
            .delPluginServerCluster(pluginServerCluster);
    }
    
    @Override
    public List<TPluginServiceRouter> getPluginServiceRouter(TPluginServerCluster pluginServerCluster)
        throws TException
    {
        return ufmThriftClientProxyFactory.getProxy(PluginThriftServiceClient.class)
            .getPluginServiceRouter(pluginServerCluster);
    }
    
    @Override
    public List<TPluginServerCluster> listPluginServerCluster(TPluginServerCluster pluginServerCluster)
        throws TException
    {
        return ufmThriftClientProxyFactory.getProxy(PluginThriftServiceClient.class)
            .listPluginServerCluster(pluginServerCluster);
    }
    
    @Override
    public void setPluginServerCluster(TPluginServerCluster pluginServerCluster,
        List<TPluginServiceRouter> addRouter, List<TPluginServiceRouter> delRouter) throws TException
    {
        ufmThriftClientProxyFactory.getProxy(PluginThriftServiceClient.class)
            .setPluginServerCluster(pluginServerCluster, addRouter, delRouter);
            
    }
    
    @Override
    public List<TPluginServerInstance> getListPluginServerInstance(long clusterId) throws TException
    {
        return ufmThriftClientProxyFactory.getProxy(PluginThriftServiceClient.class)
            .getListPluginServerInstance(clusterId);
            
    }
    
    @Override
    public List<TWorkerNode> getWrokerList(TPluginServerCluster tPluginServerCluster) throws TException
    {
        return ufmThriftClientProxyFactory.getProxy(PluginThriftServiceClient.class)
            .getWrokerList(tPluginServerCluster);
    }
    
    @Override
    public void setAccessKey(TAccessKey accessKey, TPluginServerCluster pluginServerCluster) throws TException
    {
        ufmThriftClientProxyFactory.getProxy(PluginThriftServiceClient.class).setAccessKey(accessKey,
            pluginServerCluster);
    }
    
}
