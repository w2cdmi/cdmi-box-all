package com.huawei.sharedrive.app.statistics.manager;

import java.text.ParseException;
import java.util.List;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.openapi.domain.statistics.NodeCurrentStatisticsRequest;
import com.huawei.sharedrive.app.openapi.domain.statistics.NodeCurrentStatisticsResponse;
import com.huawei.sharedrive.app.openapi.domain.statistics.NodeHistoryStatisticsRequest;
import com.huawei.sharedrive.app.openapi.domain.statistics.NodeHistoryStatisticsResponse;
import com.huawei.sharedrive.app.openapi.domain.statistics.ObjectCurrentStatisticsRequest;
import com.huawei.sharedrive.app.openapi.domain.statistics.ObjectCurrentStatisticsResponse;
import com.huawei.sharedrive.app.openapi.domain.statistics.ObjectHistoryStatisticsRequest;
import com.huawei.sharedrive.app.openapi.domain.statistics.ObjectHistoryStatisticsResponse;
import com.huawei.sharedrive.app.openapi.domain.statistics.RestUserClusterStatisticsRequest;
import com.huawei.sharedrive.app.openapi.domain.statistics.RestUserCurrentStatisticsRequest;
import com.huawei.sharedrive.app.openapi.domain.statistics.RestUserHistoryStatisticsRequest;
import com.huawei.sharedrive.app.openapi.domain.statistics.UserClusterStatisticsList;
import com.huawei.sharedrive.app.openapi.domain.statistics.UserCurrentStatisticsList;
import com.huawei.sharedrive.app.openapi.domain.statistics.UserHistoryStatisticsList;
import com.huawei.sharedrive.app.openapi.domain.statistics.concurrence.ConcStatisticsRequest;
import com.huawei.sharedrive.app.openapi.domain.statistics.concurrence.ConcStatisticsResponse;
import com.huawei.sharedrive.app.statistics.domain.NodeStatisticsDay;
import com.huawei.sharedrive.app.statistics.domain.ObjectStatisticsDay;
import com.huawei.sharedrive.app.statistics.domain.SysConcStatisticsDay;
import com.huawei.sharedrive.app.statistics.domain.UserStatisticsDay;

public interface StatisticsManager
{
    UserCurrentStatisticsList getUserCurrentStatistics(RestUserCurrentStatisticsRequest restStatistiscRequest)
        throws BaseRunException;
    
    UserHistoryStatisticsList getUserHistoryStatistics(RestUserHistoryStatisticsRequest restStatistiscRequest)
        throws BaseRunException;
    
    NodeCurrentStatisticsResponse getCurrentNodeStatistics(NodeCurrentStatisticsRequest currentRequest) throws ParseException;
    
    NodeHistoryStatisticsResponse getHistoryNodeStatistics(NodeHistoryStatisticsRequest historyRequest) throws ParseException;

    ObjectCurrentStatisticsResponse getCurrentObjectStatistics(ObjectCurrentStatisticsRequest statisticsRequest) throws ParseException;

    ObjectHistoryStatisticsResponse getHistoryObjectStatistics(ObjectHistoryStatisticsRequest restStatistiscRequest) throws ParseException;

    ConcStatisticsResponse getHistoryConcStatistics(ConcStatisticsRequest restStatistiscRequest) throws ParseException;

    UserClusterStatisticsList getUserClusterStatistics(RestUserClusterStatisticsRequest restStatistiscRequest)
        throws BaseRunException;
    
    List<NodeStatisticsDay> getNodeStatisticsDayHistoryList(Integer beginTime, Integer endTime);
    
    List<ObjectStatisticsDay> getObjectStatisticsDayHistoryList(Integer beginTime, Integer endTime);
    
    List<UserStatisticsDay> getUserStatisticsDayHistoryList(Integer beginTime, Integer endTime);
    
    List<SysConcStatisticsDay> getSysConcStatisticsDayHistoryList(Integer beginTime, Integer endTime);
}
