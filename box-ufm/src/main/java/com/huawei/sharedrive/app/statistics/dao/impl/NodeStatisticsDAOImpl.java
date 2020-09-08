package com.huawei.sharedrive.app.statistics.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.statistics.dao.NodeStatisticsDAO;
import com.huawei.sharedrive.app.statistics.domain.NodeStatisticsDay;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Service("nodeStatisticsDAO")
@SuppressWarnings("deprecation")
public class NodeStatisticsDAOImpl extends AbstractDAOImpl implements NodeStatisticsDAO
{
    
    @SuppressWarnings("unchecked")
    @Override
    public List<NodeStatisticsDay> getList(long day)
    {
        return sqlMapClientTemplate.queryForList("NodeStatistics.getByDay", day);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<NodeStatisticsDay> getList(long day, String appId, Integer regionId)
    {
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("day", day);
        map.put("regionId", regionId);
        map.put("appId", appId);
        return sqlMapClientTemplate.queryForList("NodeStatistics.getGroupByRegionAndApp", map);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<NodeStatisticsDay> getListByApp(long day, String appId, Integer regionId)
    {
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("day", day);
        map.put("regionId", regionId);
        map.put("appId", appId);
        return sqlMapClientTemplate.queryForList("NodeStatistics.getGroupByApp", map);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<NodeStatisticsDay> getListByRegion(long day, String appId, Integer regionId)
    {
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("day", day);
        map.put("regionId", regionId);
        map.put("appId", appId);
        return sqlMapClientTemplate.queryForList("NodeStatistics.getGroupByRegion", map);
    }
    
    @Override
    public void insert(NodeStatisticsDay nodeStatistics)
    {
        if (null == nodeStatistics.getAppId())
        {
            nodeStatistics.setAppId("Unknown");
        }
        if (nodeStatistics.getAddedDeletedFileCount() == null)
        {
            nodeStatistics.setAddedDeletedFileCount(0L);
        }
        if (nodeStatistics.getAddedDeletedSpaceUsed() == null)
        {
            nodeStatistics.setAddedDeletedSpaceUsed(0L);
        }
        if (nodeStatistics.getAddedFileCount() == null)
        {
            nodeStatistics.setAddedFileCount(0L);
        }
        if (nodeStatistics.getAddedSpaceUsed() == null)
        {
            nodeStatistics.setAddedSpaceUsed(0L);
        }
        if (nodeStatistics.getAddedTrashFileCount() == null)
        {
            nodeStatistics.setAddedTrashFileCount(0L);
        }
        if (nodeStatistics.getAddedTrashSpaceUsed() == null)
        {
            nodeStatistics.setAddedTrashSpaceUsed(0L);
        }
        sqlMapClientTemplate.insert("NodeStatistics.insert", nodeStatistics);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<NodeStatisticsDay> getListByRange(Integer beginDay, Integer endDay, String appId, Integer regionId)
    {
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("beginDay", beginDay);
        map.put("endDay", endDay);
        map.put("regionId", regionId);
        map.put("appId", appId);
        return sqlMapClientTemplate.queryForList("NodeStatistics.getByRange", map);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<NodeStatisticsDay> getFileAndSizeSummayListByRegion(long day)
    {
        return sqlMapClientTemplate.queryForList("NodeStatistics.getRegionFilesAndSize", day);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<NodeStatisticsDay> getListByRange(Integer beginDay, Integer endDay)
    {
        
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("beginDay", beginDay);
        map.put("endDay", endDay);
        
        return sqlMapClientTemplate.queryForList("NodeStatistics.getListByRange", map);
    }
}
