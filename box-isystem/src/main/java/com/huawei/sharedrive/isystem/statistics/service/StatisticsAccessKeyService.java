package com.huawei.sharedrive.isystem.statistics.service;

import java.util.List;

import com.huawei.sharedrive.isystem.statistics.domain.StatisticsAccessKey;

public interface StatisticsAccessKeyService
{
    StatisticsAccessKey create();
    
    List<StatisticsAccessKey> getList();
    
    void deleteById(String accessKey);
    
    StatisticsAccessKey get(String id);
}
