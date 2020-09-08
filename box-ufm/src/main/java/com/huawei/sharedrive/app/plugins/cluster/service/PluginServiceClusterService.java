package com.huawei.sharedrive.app.plugins.cluster.service;

import com.huawei.sharedrive.app.plugins.cluster.domain.PluginServiceCluster;

public interface PluginServiceClusterService
{
    PluginServiceCluster getClusterByObjectId(String objectId, String appId);
}
