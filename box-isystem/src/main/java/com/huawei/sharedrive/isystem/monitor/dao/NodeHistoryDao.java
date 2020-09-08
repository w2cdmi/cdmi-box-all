package com.huawei.sharedrive.isystem.monitor.dao;

import com.huawei.sharedrive.isystem.monitor.domain.NodeRunningInfo;

public interface NodeHistoryDao
{
    void insert(NodeRunningInfo nodeRunningInfo);
}
