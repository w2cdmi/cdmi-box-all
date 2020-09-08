package com.huawei.sharedrive.app.statistics.dao;

import java.util.List;

import com.huawei.sharedrive.app.openapi.domain.statistics.RestUserCurrentStatisticsRequest;
import com.huawei.sharedrive.app.openapi.domain.statistics.UserClusterStatisticsInfo;
import com.huawei.sharedrive.app.openapi.domain.statistics.UserCurrentStatisticsInfo;
import com.huawei.sharedrive.app.statistics.domain.UserStatisticsDay;

public interface UserStatisticsDao
{
    String TYPE_GROUPBY_REGION = "region";
    
    String TYPE_GROUPBY_APP = "application";
    
    String TYPE_GROUPBY_ALL = "all";
    
    String USER_STAT_ID_PATTERN = "yyyyMMdd";
    
    List<UserCurrentStatisticsInfo> getUserCurrentStatistics(String groupBy, Integer regionId, String appId);
    
    List<UserCurrentStatisticsInfo> getUserCurrentStatistics(
        RestUserCurrentStatisticsRequest restStatistiscRequest);
    
    List<UserStatisticsDay> getHistoryDaysByRange(
        Integer beginDay, Integer endDay, Integer regionId, String appId);
    
    List<UserStatisticsDay> getFilterHistoryDays(Integer day, Integer regionId, String appId);
    
    void addHistoryDay(UserStatisticsDay historyDay);
    

    UserClusterStatisticsInfo getClusterStatisticsInfo(Long begin, Long end);
}
