package com.huawei.sharedrive.app.plugins.cluster.service;

import java.util.List;

import org.apache.thrift.TException;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.plugins.cluster.domain.PluginServiceCluster;
import com.huawei.sharedrive.app.plugins.cluster.domain.PluginServiceInstance;
import com.huawei.sharedrive.app.plugins.cluster.domain.PluginServiceRouter;
import com.huawei.sharedrive.thrift.plugins.agent.TWorkerNode;

public interface PluginClusterService
{
    
    List<PluginServiceRouter> getListConvertDss(int clusterId) throws BaseRunException;
    
    List<PluginServiceRouter> getByAppID(String appId) throws BaseRunException;
    
    void deletePluginServiceRouter(List<PluginServiceRouter> routers) throws BaseRunException;
    
    List<PluginServiceCluster> getListPluginServiceCluster(String appId) throws BaseRunException;
    
    PluginServiceCluster getByAppIdAndRouteInfo(int dssId, String appId) throws BaseRunException;
    
    void ceateRouters(List<PluginServiceRouter> routers) throws BaseRunException;

    void createPluginServiceCluster(PluginServiceCluster cluster, List<PluginServiceRouter> routers,List<TWorkerNode> list)
        throws BaseRunException;

    void deletePluginServiceCluster(PluginServiceCluster cluster) throws BaseRunException;

    void deleltePluginServiceInstance(List<PluginServiceInstance> instances);

    void updatePluginServiceCluster(PluginServiceCluster cluster, List<PluginServiceRouter> crouters,
        List<PluginServiceRouter> drouters) throws BaseRunException;

    List<PluginServiceInstance> listPluginServiceInstance(int clusterId);

    void saveOrupdatePluginServiceCluster(PluginServiceCluster cluster, List<PluginServiceRouter> crouters,
        List<PluginServiceRouter> drouters, List<TWorkerNode> list) throws BaseRunException, TException;

    PluginServiceCluster getClusterbyId(int clusterId);

    int updatePluginServiceInstance(PluginServiceInstance instance);

    void createPluginServiceInstance(PluginServiceInstance instance);
    
}
