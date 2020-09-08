package com.huawei.sharedrive.app.statistics.dao;

import java.util.List;

import com.huawei.sharedrive.app.statistics.domain.TempObjectStatisticsDay;

/**
 * @author l90003768
 *
 */
public interface TempObjectStatisticsDAO
{
    
    void clearData();
    
    
    /**
     * 根据日期范围获取所有节点统计数据
     * @param begin
     * @param end
     * @return
     */
    List<TempObjectStatisticsDay> getSumList(long day);


    /**
     * 写入每日统计数据
     * @param nodeStatistics
     */
    void insert(TempObjectStatisticsDay nodeStatistics);
    
}
