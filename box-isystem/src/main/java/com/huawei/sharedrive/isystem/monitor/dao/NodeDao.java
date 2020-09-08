package com.huawei.sharedrive.isystem.monitor.dao;

import java.util.List;

import com.huawei.sharedrive.isystem.monitor.domain.NodeRunningInfo;

public interface NodeDao
{
    void insert(NodeRunningInfo nodeRunningInfo);

    NodeRunningInfo getOneNode(String hostName);

    void update(NodeRunningInfo nodeRunningInfo);

    List<NodeRunningInfo> getClusterNodes(String cluserName);
}
