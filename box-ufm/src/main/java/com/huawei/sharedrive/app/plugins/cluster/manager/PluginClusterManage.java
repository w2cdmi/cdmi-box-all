package com.huawei.sharedrive.app.plugins.cluster.manager;

import java.util.List;

import org.apache.thrift.TException;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.thrift.pluginserver.TPluginServerCluster;
import com.huawei.sharedrive.thrift.pluginserver.TPluginServerInstance;
import com.huawei.sharedrive.thrift.pluginserver.TPluginServiceRouter;

import pw.cdmi.common.job.exception.JobException;

public interface PluginClusterManage
{
    
    void saveOrUpdatePluginServerCluster(TPluginServerCluster cluster, List<TPluginServiceRouter> cRouters,
        List<TPluginServiceRouter> dRouters) throws TException, BaseRunException, JobException;
    
    List<TPluginServerCluster> listPluginServerCluster(TPluginServerCluster tPluginServerCluster)
        throws BaseRunException;
    
    List<TPluginServiceRouter> getPluginServiceRouter(TPluginServerCluster tCluster) throws BaseRunException;
    
    List<TPluginServerInstance> getListPluginServerInstance(long clusterId) throws BaseRunException;
    
    void delPluginServerCluster(TPluginServerCluster tCluster) throws TException, JobException,
        BaseRunException;
    
}
