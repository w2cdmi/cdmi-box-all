package com.huawei.sharedrive.app.plugins.cluster.manager;

import com.huawei.sharedrive.app.plugins.cluster.domain.PluginServiceCluster;

import pw.cdmi.common.job.exception.JobException;

public interface PluginServerJobManager
{
    String JOB_NAME = "pluginServerJobManager_sync_";
    
    String JOB_CLUSTER = "_";
    
    String SYNC_BEAN_NAME = "syncPluginServiceClusterJob";
    
    String MODEL = "ufm";
    
    void deleteJobManager(PluginServiceCluster cluster) throws JobException;

    void addJobManager(PluginServiceCluster cluster) throws JobException;
    
}
