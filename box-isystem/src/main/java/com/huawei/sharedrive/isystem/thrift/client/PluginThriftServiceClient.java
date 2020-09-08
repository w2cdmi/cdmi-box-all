package com.huawei.sharedrive.isystem.thrift.client;

import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransport;

import com.huawei.sharedrive.thrift.pluginserver.PluginServerThriftService;
import com.huawei.sharedrive.thrift.pluginserver.PluginServerThriftService.Iface;

import pw.cdmi.common.thrift.client.AbstractThriftClient;

import com.huawei.sharedrive.thrift.pluginserver.TAccessKey;
import com.huawei.sharedrive.thrift.pluginserver.TPluginServerCluster;
import com.huawei.sharedrive.thrift.pluginserver.TPluginServerInstance;
import com.huawei.sharedrive.thrift.pluginserver.TPluginServiceRouter;
import com.huawei.sharedrive.thrift.pluginserver.TWorkerNode;

public class PluginThriftServiceClient extends AbstractThriftClient implements Iface
{
    
    private PluginServerThriftService.Client client;
    
    public PluginThriftServiceClient(TTransport transport)
    {
        super(transport, "previewService");
        client = new PluginServerThriftService.Client(getProtocol());
    }
    
    @Override
    public void delPluginServerCluster(TPluginServerCluster pluginServerCluster) throws TException
    {
        
        client.delPluginServerCluster(pluginServerCluster);
    }
    
    @Override
    public List<TPluginServiceRouter> getPluginServiceRouter(TPluginServerCluster pluginServerCluster)
        throws TException
    {
        return client.getPluginServiceRouter(pluginServerCluster);
    }
    
    @Override
    public List<TPluginServerCluster> listPluginServerCluster(TPluginServerCluster pluginServerCluster)
        throws TException
    {
        return client.listPluginServerCluster(pluginServerCluster);
    }
    
    @Override
    public void setPluginServerCluster(TPluginServerCluster pluginServerCluster,
        List<TPluginServiceRouter> addRouter, List<TPluginServiceRouter> delRouter) throws TException
    {
        client.setPluginServerCluster(pluginServerCluster, addRouter, delRouter);
        
    }
    
    @Override
    public List<TPluginServerInstance> getListPluginServerInstance(long clusterId) throws TException
    {
        // TODO Auto-generated method stub
        return client.getListPluginServerInstance(clusterId);
    }
    
    @Override
    public List<TWorkerNode> getWrokerList(TPluginServerCluster tPluginServerCluster) throws TException
    {
        // TODO Auto-generated method stub
        return client.getWrokerList(tPluginServerCluster);
    }
    
    @Override
    public void setAccessKey(TAccessKey accessKey, TPluginServerCluster pluginServerCluster) throws TException
    {
        // TODO Auto-generated method stub
        client.setAccessKey(accessKey, pluginServerCluster);
    }
    
}
