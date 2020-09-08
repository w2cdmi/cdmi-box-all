package com.huawei.sharedrive.isystem.monitor.dao;

import java.util.List;

import com.huawei.sharedrive.isystem.monitor.domain.Cluster;

public interface ClusterDao
{
    void insert(Cluster clusterInfo);
    List<Cluster> getFilterd(Cluster filter);
    Cluster getOne(Cluster filter);
    void update(Cluster cluster);
    //如，获取某集群的 Mysql集群信息
    List<Cluster> getClusterServices(String clusterName);
    
}
