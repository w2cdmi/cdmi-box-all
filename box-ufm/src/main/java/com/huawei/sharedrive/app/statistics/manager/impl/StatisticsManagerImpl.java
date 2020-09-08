package com.huawei.sharedrive.app.statistics.manager.impl;

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.dataserver.domain.Region;
import com.huawei.sharedrive.app.dataserver.service.RegionService;
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
import com.huawei.sharedrive.app.openapi.domain.statistics.UserClusterStatisticsInfo;
import com.huawei.sharedrive.app.openapi.domain.statistics.UserClusterStatisticsList;
import com.huawei.sharedrive.app.openapi.domain.statistics.UserCurrentStatisticsInfo;
import com.huawei.sharedrive.app.openapi.domain.statistics.UserCurrentStatisticsList;
import com.huawei.sharedrive.app.openapi.domain.statistics.UserHistoryStatisticsInfo;
import com.huawei.sharedrive.app.openapi.domain.statistics.UserHistoryStatisticsList;
import com.huawei.sharedrive.app.openapi.domain.statistics.concurrence.ConcStatisticsRequest;
import com.huawei.sharedrive.app.openapi.domain.statistics.concurrence.ConcStatisticsResponse;
import com.huawei.sharedrive.app.statistics.dao.UserStatisticsDao;
import com.huawei.sharedrive.app.statistics.domain.NodeStatisticsDay;
import com.huawei.sharedrive.app.statistics.domain.ObjectStatisticsDay;
import com.huawei.sharedrive.app.statistics.domain.SysConcStatisticsDay;
import com.huawei.sharedrive.app.statistics.domain.UserStatisticsDay;
import com.huawei.sharedrive.app.statistics.manager.StatisticsManager;
import com.huawei.sharedrive.app.statistics.service.ConcStatisticsService;
import com.huawei.sharedrive.app.statistics.service.NodeStatisticsService;
import com.huawei.sharedrive.app.statistics.service.ObjectStatisticsService;
import com.huawei.sharedrive.app.statistics.service.StatisticsDateUtils;
import com.huawei.sharedrive.app.statistics.service.UsersStatisticsService;
import com.huawei.sharedrive.app.statistics.service.impl.UsersStatisticsServiceImpl;

/**
 * @author l90003768
 * 
 */
@Component("statisticsManager")
public class StatisticsManagerImpl implements StatisticsManager
{
    
    @Autowired
    private ConcStatisticsService concStatisticsService;
    
    @Autowired
    private NodeStatisticsService nodeStatisticsService;
    
    @Autowired
    private ObjectStatisticsService objectStatisticsService;
    
    @Autowired
    private RegionService regionService;
    
    @Autowired
    private UsersStatisticsService usersStatisticsService;
    
    @Override
    public NodeCurrentStatisticsResponse getCurrentNodeStatistics(NodeCurrentStatisticsRequest currentRequest)
        throws ParseException
    {
        long day = StatisticsDateUtils.getDay(Calendar.getInstance());
        List<NodeStatisticsDay> list = nodeStatisticsService.getCurrentStatisticsInfo(day,
            currentRequest.getGroupBy(),
            currentRequest.getAppId(),
            currentRequest.getRegionId());
        // 当前没有统计数据，取昨天的数据
        if (CollectionUtils.isEmpty(list))
        {
            day -= 1;
            list = nodeStatisticsService.getCurrentStatisticsInfo(day,
                currentRequest.getGroupBy(),
                currentRequest.getAppId(),
                currentRequest.getRegionId());
        }
        return new NodeCurrentStatisticsResponse(list, day, "day", regionService);
    }
    
    @Override
    public ObjectCurrentStatisticsResponse getCurrentObjectStatistics(
        ObjectCurrentStatisticsRequest statisticsRequest) throws ParseException
    {
        long day = StatisticsDateUtils.getDay(Calendar.getInstance());
        List<ObjectStatisticsDay> list = objectStatisticsService.getCurrentObjectStatisticsInfo(day,
            statisticsRequest.getRegionId(),
            statisticsRequest.getGroupBy());
        // 当前没有统计数据，取昨天的数据
        if (CollectionUtils.isEmpty(list))
        {
            day -= 1;
            list = objectStatisticsService.getCurrentObjectStatisticsInfo(day,
                statisticsRequest.getRegionId(),
                statisticsRequest.getGroupBy());
        }
        return new ObjectCurrentStatisticsResponse(list, day, "day", regionService);
    }
    
    @Override
    public ConcStatisticsResponse getHistoryConcStatistics(ConcStatisticsRequest request)
        throws ParseException
    {
        Integer begin = UsersStatisticsServiceImpl.getDay(request.getBeginTime());
        Integer end = UsersStatisticsServiceImpl.getDay(request.getEndTime());
        List<SysConcStatisticsDay> list = concStatisticsService.getHistoryList(begin, end);
        return SyscConcHistoryStatisticsPacker.packHistoryList(list, request.getInterval());
    }
    
    @Override
    public NodeHistoryStatisticsResponse getHistoryNodeStatistics(NodeHistoryStatisticsRequest historyRequest)
        throws ParseException
    {
        Integer begin = UsersStatisticsServiceImpl.getDay(historyRequest.getBeginTime());
        Integer end = UsersStatisticsServiceImpl.getDay(historyRequest.getEndTime());
        List<NodeStatisticsDay> list = nodeStatisticsService.getHistoryList(begin,
            end,
            historyRequest.getAppId(),
            historyRequest.getRegionId());
        return NodeHistoryStatisticsPacker.packeNodeHistoryList(list, historyRequest.getInterval());
    }
    
    @Override
    public ObjectHistoryStatisticsResponse getHistoryObjectStatistics(ObjectHistoryStatisticsRequest request)
        throws ParseException
    {
        Integer begin = UsersStatisticsServiceImpl.getDay(request.getBeginTime());
        Integer end = UsersStatisticsServiceImpl.getDay(request.getEndTime());
        List<ObjectStatisticsDay> list = objectStatisticsService.getHistoryList(begin,
            end,
            request.getRegionId());
        return ObjectHistoryStatisticsPacker.packObjectHistoryList(list, request.getInterval());
    }
    
    @Override
    public List<NodeStatisticsDay> getNodeStatisticsDayHistoryList(Integer beginTime, Integer endTime)
    {
        
        return nodeStatisticsService.getHistoryList(beginTime, endTime);
    }
    
    @Override
    public List<ObjectStatisticsDay> getObjectStatisticsDayHistoryList(Integer beginTime, Integer endTime)
    {
        return objectStatisticsService.getHistoryList(beginTime, endTime);
    }
    
    @Override
    public List<SysConcStatisticsDay> getSysConcStatisticsDayHistoryList(Integer beginTime, Integer endTime)
    {
        
        return concStatisticsService.getHistoryList(beginTime, endTime);
    }
    
    @Override
    public UserClusterStatisticsList getUserClusterStatistics(
        RestUserClusterStatisticsRequest restStatistiscRequest) throws BaseRunException
    {
        List<UserClusterStatisticsInfo> list = usersStatisticsService.getUserClusterStatistics(restStatistiscRequest.getMilestones());
        UserClusterStatisticsList result = new UserClusterStatisticsList();
        result.setData(list);
        result.setTotalCount(list.size());
        return result;
    }
    
    @Override
    public UserCurrentStatisticsList getUserCurrentStatistics(
        RestUserCurrentStatisticsRequest restStatistiscRequest) throws BaseRunException
    {
        UserCurrentStatisticsList result = new UserCurrentStatisticsList();
        List<UserCurrentStatisticsInfo> list = usersStatisticsService.getUserCurrentStatistics(restStatistiscRequest);
        
        result.setTotalCount(list.size());
        
        // 更新区域名称
        if (UserStatisticsDao.TYPE_GROUPBY_REGION.equals(restStatistiscRequest.getGroupBy())
            || UserStatisticsDao.TYPE_GROUPBY_ALL.equals(restStatistiscRequest.getGroupBy()))
        {
            Region region = null;
            for (UserCurrentStatisticsInfo info : list)
            {
                region = regionService.getRegion(info.getRegionId());
                if (region != null)
                {
                    info.setRegionName(region.getName());
                }
            }
        }
        result.setData(list);
        return result;
    }
    
    @Override
    public UserHistoryStatisticsList getUserHistoryStatistics(
        RestUserHistoryStatisticsRequest restStatistiscRequest) throws BaseRunException
    {
        List<UserHistoryStatisticsInfo> list = usersStatisticsService.getUserHistoryStatistics(restStatistiscRequest);
        
        UserHistoryStatisticsList result = new UserHistoryStatisticsList();
        
        result.setData(list);
        result.setTotalCount(list.size());
        return result;
    }
    
    @Override
    public List<UserStatisticsDay> getUserStatisticsDayHistoryList(Integer beginTime, Integer endTime)
    {
        
        return usersStatisticsService.getHistoryList(beginTime, endTime);
    }
    
}
