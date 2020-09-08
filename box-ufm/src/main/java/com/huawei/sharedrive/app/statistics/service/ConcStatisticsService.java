package com.huawei.sharedrive.app.statistics.service;

import java.util.List;

import com.huawei.sharedrive.app.statistics.domain.SysConcStatisticsDay;

public interface ConcStatisticsService
{

    List<SysConcStatisticsDay> getHistoryList(Integer beginTime, Integer endTime);
}
