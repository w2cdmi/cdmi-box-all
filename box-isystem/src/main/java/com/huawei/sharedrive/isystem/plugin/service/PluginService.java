package com.huawei.sharedrive.isystem.plugin.service;

import java.util.List;

import org.apache.thrift.TException;

import com.huawei.sharedrive.thrift.pluginserver.TAccessKey;
import com.huawei.sharedrive.thrift.pluginserver.TPluginServerCluster;
import com.huawei.sharedrive.thrift.pluginserver.TPluginServerInstance;
import com.huawei.sharedrive.thrift.pluginserver.TPluginServiceRouter;
import com.huawei.sharedrive.thrift.pluginserver.TWorkerNode;

public interface PluginService
{

    void delPluginServerCluster(TPluginServerCluster pluginServerCluster) throws TException;

    List<TPluginServiceRouter> getPluginServiceRouter(TPluginServerCluster pluginServerCluster)
        throws TException;

    List<TPluginServerCluster> listPluginServerCluster(TPluginServerCluster pluginServerCluster)
        throws TException;

    void setPluginServerCluster(TPluginServerCluster pluginServerCluster, List<TPluginServiceRouter> addRouter,
        List<TPluginServiceRouter> delRouter) throws TException;

    List<TPluginServerInstance> getListPluginServerInstance(long clusterId) throws TException;

    void setAccessKey(TAccessKey accessKey, TPluginServerCluster pluginServerCluster) throws TException;

    List<TWorkerNode> getWrokerList(TPluginServerCluster tPluginServerCluster) throws TException;

}
