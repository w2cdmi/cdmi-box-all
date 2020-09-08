package com.huawei.sharedrive.uam.statistics.dao;

import java.util.Date;

import com.huawei.sharedrive.uam.statistics.domain.StatisticsTempChart;

public interface StatisticsChartDao
{
    void deleteEndToCreatedAt(Date date);
    
    StatisticsTempChart get(String id);
    
    void insert(StatisticsTempChart statisticsTempChart);
}
