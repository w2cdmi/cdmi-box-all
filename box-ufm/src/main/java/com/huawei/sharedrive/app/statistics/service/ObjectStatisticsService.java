package com.huawei.sharedrive.app.statistics.service;

import java.util.List;

import com.huawei.sharedrive.app.statistics.domain.ObjectStatisticsDay;

public interface ObjectStatisticsService
{
    List<ObjectStatisticsDay> getCurrentObjectStatisticsInfo(long day, Integer regionId, String groupBy);

    List<ObjectStatisticsDay> getHistoryList(Integer begin, Integer end, Integer regionId);
    
    List<ObjectStatisticsDay> getHistoryList(Integer begin, Integer end);
}
