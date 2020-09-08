package com.huawei.sharedrive.app.statistics.service.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.statistics.dao.ObjectStatisticsDAO;
import com.huawei.sharedrive.app.statistics.domain.ObjectStatisticsDay;
import com.huawei.sharedrive.app.statistics.service.ObjectStatisticsService;

@Service("objectStatisticsService")
public class ObjectStatisticsServiceImpl implements ObjectStatisticsService
{
    
    @Autowired
    private ObjectStatisticsDAO objectStatisticsDAO;
    
    @Override
    public List<ObjectStatisticsDay> getCurrentObjectStatisticsInfo(long day, Integer regionId, String groupBy)
    {
        List<ObjectStatisticsDay> list = null;
        if (null == groupBy)
        {
            list = objectStatisticsDAO.getSummay(day, regionId);
        }
        else
        {
            list = objectStatisticsDAO.getListGroupByRegion(day, regionId);
        }
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
    public List<ObjectStatisticsDay> getHistoryList(Integer begin, Integer end, Integer regionId)
    {
        List<ObjectStatisticsDay> list = objectStatisticsDAO.getListByRange(begin, end, regionId);
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
    public List<ObjectStatisticsDay> getHistoryList(Integer begin, Integer end)
    {
        List<ObjectStatisticsDay> list = objectStatisticsDAO.getListByRange(begin, end);
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
