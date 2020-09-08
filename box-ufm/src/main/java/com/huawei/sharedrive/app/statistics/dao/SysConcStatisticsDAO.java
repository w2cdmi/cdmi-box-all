package com.huawei.sharedrive.app.statistics.dao;

import java.util.List;

import com.huawei.sharedrive.app.statistics.domain.SysConcStatisticsDay;

/**
 * @author l90003768
 *
 */
public interface SysConcStatisticsDAO
{
    
    SysConcStatisticsDay get(int day);
    
    
    /**
     * 写入每日统计数据
     * @param nodeStatistics
     */
    void insert(SysConcStatisticsDay itemStatisticsDay);
    
    void update(SysConcStatisticsDay itemStatisticsDay);

    List<SysConcStatisticsDay> getSumListByRange(Integer beginTime, Integer endTime);
    
}
