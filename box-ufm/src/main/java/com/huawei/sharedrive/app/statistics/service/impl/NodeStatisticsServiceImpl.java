package com.huawei.sharedrive.app.statistics.service.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.openapi.domain.statistics.NodeCurrentStatisticsRequest;
import com.huawei.sharedrive.app.statistics.dao.NodeStatisticsDAO;
import com.huawei.sharedrive.app.statistics.domain.NodeStatisticsDay;
import com.huawei.sharedrive.app.statistics.service.NodeStatisticsService;

@Service("nodeStatisticsService")
public class NodeStatisticsServiceImpl implements NodeStatisticsService
{
    
    @Autowired
    private NodeStatisticsDAO nodeStatisticsDAO;
    
    @SuppressWarnings("unchecked")
    @Override
    public List<NodeStatisticsDay> getCurrentStatisticsInfo(long day, String groupBy, String appId, Integer regionId)
    {
        List<NodeStatisticsDay> res = null;
        if (NodeCurrentStatisticsRequest.GROUPBY_APP.equalsIgnoreCase(groupBy))
        {
            res = this.nodeStatisticsDAO.getListByApp(day, appId, regionId);
        }
        else if (NodeCurrentStatisticsRequest.GROUPBY_REGION.equalsIgnoreCase(groupBy))
        {
            res = this.nodeStatisticsDAO.getListByRegion(day, appId, regionId);
        }
        else
        {
            res = this.nodeStatisticsDAO.getList(day, appId, regionId);
        }
        if (!res.isEmpty())
        {
            if (res.get(0).getFileCount() == null)
            {
                return Collections.EMPTY_LIST;
            }
        }
        return res;
    }
    
    @Override
    public List<NodeStatisticsDay> getHistoryList(Integer beginTime, Integer endTime, String appId, Integer regionId)
    {
        List<NodeStatisticsDay> list = nodeStatisticsDAO.getListByRange(beginTime, endTime, appId, regionId);
        if (!list.isEmpty())
        {
            if (list.get(0).getFileCount() == null)
            {
                return Collections.emptyList();
            }
        }
        return list;
    }
    
    @Override
    public List<NodeStatisticsDay> getHistoryList(Integer beginTime, Integer endTime)
    {
        
        List<NodeStatisticsDay> list = nodeStatisticsDAO.getListByRange(beginTime, endTime);
        if (!list.isEmpty())
        {
            if (list.get(0).getFileCount() == null)
            {
                return Collections.emptyList();
            }
        }
        return list;
    }
}
