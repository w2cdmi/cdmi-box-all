package com.huawei.sharedrive.app.statistics.service;

import java.util.List;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.openapi.domain.statistics.MilestioneInfo;
import com.huawei.sharedrive.app.openapi.domain.statistics.RestUserCurrentStatisticsRequest;
import com.huawei.sharedrive.app.openapi.domain.statistics.RestUserHistoryStatisticsRequest;
import com.huawei.sharedrive.app.openapi.domain.statistics.UserClusterStatisticsInfo;
import com.huawei.sharedrive.app.openapi.domain.statistics.UserCurrentStatisticsInfo;
import com.huawei.sharedrive.app.openapi.domain.statistics.UserHistoryStatisticsInfo;
import com.huawei.sharedrive.app.statistics.domain.UserStatisticsDay;

public interface UsersStatisticsService
{
    /**
     * @param groupBy
     * @param regionId
     * @param appId
     * @return
     * @throws BaseRunException
     */
    List<UserCurrentStatisticsInfo> getUserCurrentStatistics(String groupBy, Integer regionId, String appId)
        throws BaseRunException;
    
    /**
     * @param restStatistiscRequest
     * @return
     * @throws BaseRunException
     */
    List<UserCurrentStatisticsInfo> getUserCurrentStatistics(
        RestUserCurrentStatisticsRequest restStatistiscRequest) throws BaseRunException;
    
    /**
     * @param restStatistiscRequest
     * @return
     * @throws BaseRunException
     */
    List<UserHistoryStatisticsInfo> getUserHistoryStatistics(
        RestUserHistoryStatisticsRequest restStatistiscRequest) throws BaseRunException;
    
    
    List<UserClusterStatisticsInfo> getUserClusterStatistics(List<MilestioneInfo> milestiones)
        throws BaseRunException;
    
    /**
     * Get UserStatisticsDay statistics by time periods
     * @param beginTime
     * @param endTime
     * @return
     */
    List<UserStatisticsDay> getHistoryList(Integer beginTime, Integer endTime);
}
