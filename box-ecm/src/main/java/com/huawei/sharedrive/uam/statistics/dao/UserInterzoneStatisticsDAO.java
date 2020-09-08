package com.huawei.sharedrive.uam.statistics.dao;

import com.huawei.sharedrive.uam.statistics.domain.UserInterzone;

public interface UserInterzoneStatisticsDAO
{
    void save(UserInterzone interzone);
    
    void delete();
    
    UserInterzone query();
}
