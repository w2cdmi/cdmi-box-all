package com.huawei.sharedrive.app.statistics.dao;

import java.util.List;

import com.huawei.sharedrive.app.statistics.domain.TempUserNodeStatistics;

public interface TempUserNodeStatisticsDAO
{
    
    void save(TempUserNodeStatistics tempUserNodeStatistics);
    
    void saveList(List<TempUserNodeStatistics> tempList);
    
    /**
     * 获取根据应用和区域汇总后的数据
     * @param date
     * @return
     */
    List<TempUserNodeStatistics> getGatherList(long date);
    
    /**
     * 删除所有数据
     */
    void deleteAll();
    
}
