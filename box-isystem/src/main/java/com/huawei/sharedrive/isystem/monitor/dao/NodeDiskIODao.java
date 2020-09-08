package com.huawei.sharedrive.isystem.monitor.dao;

import java.util.List;

import com.huawei.sharedrive.isystem.monitor.domain.NodeDiskIO;

public interface NodeDiskIODao
{
    void insert(NodeDiskIO nodeDiskIO);
    
    void update(NodeDiskIO nodeDiskIO);
    
    NodeDiskIO getOneDiskIO(NodeDiskIO nodeDiskIO);
    
    List<NodeDiskIO> getNodeDiskIOs(String hostName);
}
