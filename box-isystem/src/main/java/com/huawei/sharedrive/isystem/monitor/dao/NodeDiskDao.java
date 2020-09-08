package com.huawei.sharedrive.isystem.monitor.dao;

import java.util.List;

import com.huawei.sharedrive.isystem.monitor.domain.NodeDisk;

public interface NodeDiskDao
{
    void insert(NodeDisk nodeDisk);
    void update(NodeDisk nodeDisk);
    NodeDisk getOneDisk(NodeDisk nodeDisk);
    List<NodeDisk> getNodeDisks(String hostName);
}
