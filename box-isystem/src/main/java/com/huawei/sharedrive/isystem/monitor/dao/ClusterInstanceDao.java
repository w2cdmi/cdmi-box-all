package com.huawei.sharedrive.isystem.monitor.dao;

import java.util.List;

import com.huawei.sharedrive.isystem.monitor.domain.ClusterInstance;

public interface ClusterInstanceDao
{
    void insert(ClusterInstance clusterInstanceInfo);
    ClusterInstance get(ClusterInstance filter);
    void update(ClusterInstance clusterInstance);
    List<ClusterInstance> getMysqlNodes(ClusterInstance clusterInstance);
    void deleteClusterInstances(ClusterInstance clusterInstance);
}
