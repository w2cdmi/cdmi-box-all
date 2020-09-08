package com.huawei.sharedrive.app.statistics.dao;

import java.util.List;

import com.huawei.sharedrive.app.statistics.domain.NodeStatisticsDay;

/**
 * @author l90003768
 *
 */
public interface NodeStatisticsDAO
{
    
    /**
     * 根据日期范围获取所有节点统计数据
     * @param begin
     * @param end
     * @return
     */
    List<NodeStatisticsDay> getList(long day);
    
    /**
     * 获取各区域的文件和容量统计数据
     * @param day
     * @return
     */
    List<NodeStatisticsDay> getFileAndSizeSummayListByRegion(long day);
    
    /**
     * 根据日期范围获取所有节点统计数据
     * @param begin
     * @param end
     * @return
     */
    List<NodeStatisticsDay> getList(long day, String appId, Integer regionId);
    
    /**
     * 根据日期范围获取所有节点统计数据
     * @param begin
     * @param end
     * @return
     */
    List<NodeStatisticsDay> getListByRegion(long day, String appId, Integer regionId);
    
    /**
     * 根据日期范围获取所有节点统计数据
     * @param begin
     * @param end
     * @return
     */
    List<NodeStatisticsDay> getListByApp(long day, String appId, Integer regionId);
    
    
    /**
     * 根据日期范围获取指定区域指定应用的节点统计数据
     * @param beginDay
     * @param end
     * @param appId
     * @param regionId
     * @return
     */
    List<NodeStatisticsDay> getListByRange(Integer beginDay, Integer endDay, String appId, Integer regionId);
    
    /**
     * 恩局日期范围获取节点数据
     * @param beginDay
     * @param endDay
     * @return
     */
    List<NodeStatisticsDay> getListByRange(Integer beginDay, Integer endDay);
    
    /**
     * 写入每日统计数据
     * @param nodeStatistics
     */
    void insert(NodeStatisticsDay nodeStatistics);
    
}
