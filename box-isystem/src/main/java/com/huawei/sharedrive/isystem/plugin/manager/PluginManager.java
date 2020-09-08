package com.huawei.sharedrive.isystem.plugin.manager;

import java.util.List;

import org.apache.thrift.TException;

import com.huawei.sharedrive.isystem.plugin.domain.DCTreeNode;
import com.huawei.sharedrive.isystem.plugin.domain.PluginServerInstance;
import com.huawei.sharedrive.isystem.plugin.domain.PluginServerView;
import com.huawei.sharedrive.thrift.pluginserver.TPluginServerCluster;
import com.huawei.sharedrive.thrift.pluginserver.TPluginServiceRouter;
import com.huawei.sharedrive.thrift.pluginserver.TWorkerNode;

import pw.cdmi.uam.domain.AuthApp;

public interface PluginManager
{
    
    List<AuthApp> listAuthApp();
    
    List<PluginServerView> getListClusterView(String appId) throws TException;
    
    List<TPluginServiceRouter> getListRouter(List<TPluginServerCluster> clusters) throws TException;
    
    List<DCTreeNode> getListRegionDao();
    
    List<DCTreeNode> getListDCRouter(int regionId, TPluginServerCluster cluster) throws TException;
    
    void addPreview(TPluginServerCluster pluginServerCluster, List<TPluginServiceRouter> addRouter,
        List<TPluginServiceRouter> delRouter) throws TException;
    
    void deletePluginServcie(long clusterId) throws TException;
    
    List<PluginServerInstance> getListPluginServerInstance(long clusterId) throws TException;
    
    PluginServerView getPluginServerView(TPluginServerCluster tPluginServerCluster) throws TException;
    
    List<TWorkerNode> getWrokerList(Long clusterId, String appId) throws TException;
    
    AuthApp getAuthApp(String appId);
}
