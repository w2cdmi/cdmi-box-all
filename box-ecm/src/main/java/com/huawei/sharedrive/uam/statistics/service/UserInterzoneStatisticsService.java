package com.huawei.sharedrive.uam.statistics.service;

import com.huawei.sharedrive.uam.statistics.domain.UserInterzone;

public interface UserInterzoneStatisticsService
{
    void save(UserInterzone interzone);
    
    void delete();
    
    UserInterzone query();
}
