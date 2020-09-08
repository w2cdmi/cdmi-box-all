package com.huawei.sharedrive.app.openapi.domain.statistics;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.huawei.sharedrive.app.dataserver.service.RegionService;
import com.huawei.sharedrive.app.statistics.domain.ObjectStatisticsDay;
import com.huawei.sharedrive.app.utils.BusinessConstants;

public class ObjectCurrentStatisticsResponse
{
    
    public ObjectCurrentStatisticsResponse()
    {
    }
    
    public ObjectCurrentStatisticsResponse(List<ObjectStatisticsDay> list, long day, String timeUnit, 
        RegionService regionService) throws ParseException
    {
        this.timePoint = TimePoint.convert((int)day, timeUnit);
        if(CollectionUtils.isEmpty(list))
        {
            this.totalCount = 0;
        }
        else
        {
            this.totalCount = list.size();
            List<ObjectCurrentStatisticsInfo> dataList = new ArrayList<ObjectCurrentStatisticsInfo>(BusinessConstants.INITIAL_CAPACITIES);
            for(ObjectStatisticsDay nodeStatistics: list)
            {
                dataList.add(ObjectCurrentStatisticsInfo.convertInto(nodeStatistics, regionService));
            }
            this.data = dataList;
        }
    }
    
    
    
    private List<ObjectCurrentStatisticsInfo> data;
    
    private TimePoint timePoint;
    
    private int totalCount;

    public List<ObjectCurrentStatisticsInfo> getData()
    {
        return data;
    }

    public TimePoint getTimePoint()
    {
        return timePoint;
    }

    public int getTotalCount()
    {
        return totalCount;
    }

    public void setData(List<ObjectCurrentStatisticsInfo> data)
    {
        this.data = data;
    }

    public void setTimePoint(TimePoint timePoint)
    {
        this.timePoint = timePoint;
    }

    public void setTotalCount(int totalCount)
    {
        this.totalCount = totalCount;
    }
    
}
