package com.huawei.sharedrive.isystem.monitor.dao;

import java.util.List;

import com.huawei.sharedrive.isystem.monitor.domain.SystemClusterInfo;

public interface SystemClusterInfoDao
{
    void insert(SystemClusterInfo systemClusterInfo);
    
    List<SystemClusterInfo> getAllSystemClusterInfos();
    
    SystemClusterInfo get(SystemClusterInfo systemClusterInfo);
    
    void update(SystemClusterInfo systemClusterInfo);
    
    SystemClusterInfo getSystemName(String clusterName);
    
    void updateStatus(SystemClusterInfo s);
    
}
