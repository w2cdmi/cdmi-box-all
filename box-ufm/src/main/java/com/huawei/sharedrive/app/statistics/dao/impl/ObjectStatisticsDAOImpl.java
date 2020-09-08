package com.huawei.sharedrive.app.statistics.dao.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.statistics.dao.ObjectStatisticsDAO;
import com.huawei.sharedrive.app.statistics.domain.ObjectStatisticsDay;
import com.huawei.sharedrive.app.statistics.service.StatisticsDateUtils;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Service("objectStatisticsDAO")
@SuppressWarnings("deprecation")
public class ObjectStatisticsDAOImpl extends AbstractDAOImpl implements ObjectStatisticsDAO
{
    
    @SuppressWarnings("unchecked")
    @Override
    public List<ObjectStatisticsDay> getLastDayList(int day)
    {
        List<ObjectStatisticsDay> list = null;
        int beforDay = StatisticsDateUtils.getLastDay(day);
        for (int i = 0; i < 30; i++)
        {
            list = this.getList(beforDay);
            if (list.isEmpty())
            {
                beforDay = StatisticsDateUtils.getLastDay(beforDay);
                continue;
            }
            else
            {
                return list;
            }
        }
        return Collections.EMPTY_LIST;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<ObjectStatisticsDay> getList(long day)
    {
        return sqlMapClientTemplate.queryForList("ObjectStatistics.getDayList", day);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<ObjectStatisticsDay> getListByRange(Integer beginDay, Integer endDay, Integer regionId)
    {
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("beginDay", beginDay);
        map.put("endDay", endDay);
        map.put("regionId", regionId);
        return sqlMapClientTemplate.queryForList("ObjectStatistics.getByRange", map);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<ObjectStatisticsDay> getListGroupByRegion(long day, Integer regionId)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("day", day);
        map.put("regionId", regionId);
        return (List<ObjectStatisticsDay>) sqlMapClientTemplate.queryForList("ObjectStatistics.getDaySumGroupByRegion",
            map);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<ObjectStatisticsDay> getSummay(long day, Integer regionId)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("day", day);
        map.put("regionId", regionId);
        return (List<ObjectStatisticsDay>) sqlMapClientTemplate.queryForList("ObjectStatistics.getDaySum",
            map);
    }
    
    @Override
    public void insert(ObjectStatisticsDay objectStatistics)
    {
        if (objectStatistics.getAddedActualFileCount() == null)
        {
            objectStatistics.setAddedActualFileCount(0L);
        }
        if (objectStatistics.getAddedActualSpaceUsed() == null)
        {
            objectStatistics.setAddedActualSpaceUsed(0L);
        }
        if (objectStatistics.getAddedFileCount() == null)
        {
            objectStatistics.setAddedFileCount(0L);
        }
        if (objectStatistics.getAddedSpaceUsed() == null)
        {
            objectStatistics.setAddedSpaceUsed(0L);
        }
        sqlMapClientTemplate.insert("ObjectStatistics.insert", objectStatistics);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<ObjectStatisticsDay> getListByRange(Integer beginDay, Integer endDay)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("beginDay", beginDay);
        map.put("endDay", endDay);
        return sqlMapClientTemplate.queryForList("ObjectStatistics.getListByRange", map);
    }
    
}
