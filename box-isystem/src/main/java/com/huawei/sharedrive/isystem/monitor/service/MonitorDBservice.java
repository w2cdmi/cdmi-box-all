package com.huawei.sharedrive.isystem.monitor.service;

import java.util.List;

import com.huawei.sharedrive.isystem.monitor.domain.Cluster;
import com.huawei.sharedrive.isystem.monitor.domain.ClusterInstance;
import com.huawei.sharedrive.isystem.monitor.domain.NodeDisk;
import com.huawei.sharedrive.isystem.monitor.domain.NodeDiskIO;
import com.huawei.sharedrive.isystem.monitor.domain.NodeRunningInfo;
import com.huawei.sharedrive.isystem.monitor.domain.ProcessInfo;
import com.huawei.sharedrive.isystem.monitor.domain.SystemClusterInfo;

public interface MonitorDBservice
{
    
    List<Cluster> getClusterServeices(String clusterName);
    
    List<NodeRunningInfo> getClusterNodes(String clusterName);
    
    List<SystemClusterInfo> getAllClusterName();
    
    List<ProcessInfo> getNodeProcess(String hostName);
    
    List<NodeDiskIO> getDiskIOs(String hostName);
    
    List<NodeDisk> getDisks(String hostName);
    
    NodeRunningInfo getOneNodeInfo(String hostName);
    
    List<ClusterInstance> getServiceNodes(String cluserName, String clusterServiceName);

    SystemClusterInfo getSystemName(String clusterName);
    
}
