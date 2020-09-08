package com.huawei.sharedrive.isystem.statistics.dao;

import java.util.List;

import com.huawei.sharedrive.isystem.statistics.domain.StatisticsAccessKey;

public interface StatisticsAccessKeyDAO
{
    
    void create(StatisticsAccessKey statisticsAccessKey);
    
    List<StatisticsAccessKey> queryList();
    
    void delete(String accessKey);
    
    StatisticsAccessKey get(String id);
    
}
