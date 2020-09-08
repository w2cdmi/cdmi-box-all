package com.huawei.sharedrive.app.statistics.manager.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.huawei.sharedrive.app.openapi.domain.statistics.TimePoint;
import com.huawei.sharedrive.app.openapi.domain.statistics.concurrence.ConcStatisticsInfo;
import com.huawei.sharedrive.app.openapi.domain.statistics.concurrence.ConcStatisticsResponse;
import com.huawei.sharedrive.app.statistics.domain.SysConcStatisticsDay;

/**
 * 历史节点统计数据封装器
 * 
 * @author l90003768
 * 
 */
public final class SyscConcHistoryStatisticsPacker
{
    private SyscConcHistoryStatisticsPacker()
    {
    }
    
    public static ConcStatisticsResponse packHistoryList(List<SysConcStatisticsDay> itemList, String interval)
        throws ParseException
    {
        ConcStatisticsResponse response = new ConcStatisticsResponse();
        Map<String, ConcStatisticsInfo> map = new HashMap<String, ConcStatisticsInfo>(10);
        for (SysConcStatisticsDay itemStatistics : itemList)
        {
            putItemIntoMap(map, itemStatistics, interval);
        }
        Collection<ConcStatisticsInfo> valueList = map.values();
        List<ConcStatisticsInfo> dataList = new ArrayList<ConcStatisticsInfo>(valueList);
        
        response.setData(dataList);
        response.setTotalCount(dataList.size());
        return response;
    }
    
    private static void putItemIntoMap(Map<String, ConcStatisticsInfo> map,
        SysConcStatisticsDay itemStatistics, String unit) throws ParseException
    {
        String timePointStr = TimePoint.convert(itemStatistics.getDay(), unit).toShowString();
        ConcStatisticsInfo concStatistics = map.get(timePointStr);
        if (null == concStatistics)
        {
            concStatistics = ConcStatisticsInfo.convert(itemStatistics, unit);
            map.put(timePointStr, concStatistics);
        }
        else
        {
            updateMax(concStatistics, itemStatistics);
        }
    }
    
    /**
     * 更新最大值
     * 
     * @param concStatistics
     * @param itemStatistics
     */
    private static void updateMax(ConcStatisticsInfo concStatistics, SysConcStatisticsDay itemStatistics)
    {
        if (itemStatistics.getMaxUpload() > concStatistics.getMaxUpload())
        {
            concStatistics.setMaxUpload(itemStatistics.getMaxUpload());
        }
        if (itemStatistics.getMaxDownload() > concStatistics.getMaxDownload())
        {
            concStatistics.setMaxDownload(itemStatistics.getMaxDownload());
        }
    }
    
}
