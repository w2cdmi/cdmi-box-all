package com.huawei.sharedrive.app.statistics.dao;

import java.util.List;

import com.huawei.sharedrive.app.statistics.domain.ObjectStatisticsDay;

/**
 * @author l90003768
 *
 */
public interface ObjectStatisticsDAO
{
    
    /**
     * 根据日期范围获取所有节点统计数据
     * @param begin
     * @param end
     * @return
     */
    List<ObjectStatisticsDay> getList(long day);
    
    /**
     * 获取某一天的汇总数据，可指定区域
     * @param day
     * @param regionId
     * @return
     */
    List<ObjectStatisticsDay> getSummay(long day, Integer regionId);
    
    /**
     * 根据日期范围获取指定区域指定应用的节点统计数据
     * @param beginDay
     * @param end
     * @param appId
     * @param regionId
     * @return
     */
    List<ObjectStatisticsDay> getListByRange(Integer beginDay, Integer endDay, Integer regionId);
    
    /**
     * Get statistics by time periods
     * @param beginDay
     * @param endDay
     * @return
     */
    List<ObjectStatisticsDay> getListByRange(Integer beginDay, Integer endDay);
    
    /**
     * 写入每日统计数据
     * @param nodeStatistics
     */
    void insert(ObjectStatisticsDay nodeStatistics);

    /**
     * 获取上一天的列表
     * <br/>如果连续30天无数据，视为第一天的数据
     * @param day
     */
    List<ObjectStatisticsDay> getLastDayList(int day);

    List<ObjectStatisticsDay> getListGroupByRegion(long day, Integer regionId);
    
}
