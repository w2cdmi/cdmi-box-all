package com.huawei.sharedrive.app.statistics.dao;

import com.huawei.sharedrive.app.statistics.domain.TempConcStatistics;

/**
 * @author l90003768
 *
 */
public interface TempConcStatisticsDAO
{
    TempConcStatistics get(int day, String host, int timeUnit);
    /**
     * 写入每日统计数据
     * @param nodeStatistics
     */
    void insert(TempConcStatistics itemStatisticsDay);
    
    void update(TempConcStatistics itemStatisticsDay);
    int getMaxUpload(int day);
    int getMaxDownload(int day);
    
}
