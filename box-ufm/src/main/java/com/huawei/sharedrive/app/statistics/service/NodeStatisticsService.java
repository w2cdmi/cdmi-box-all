package com.huawei.sharedrive.app.statistics.service;

import java.util.List;

import com.huawei.sharedrive.app.statistics.domain.NodeStatisticsDay;

public interface NodeStatisticsService
{
    List<NodeStatisticsDay> getCurrentStatisticsInfo(long day, String groupBy, String appId, Integer regionId);

    List<NodeStatisticsDay> getHistoryList(Integer beginTime, Integer endTime, String appId, Integer regionId);
    
    List<NodeStatisticsDay> getHistoryList(Integer beginTime, Integer endTime);
}
