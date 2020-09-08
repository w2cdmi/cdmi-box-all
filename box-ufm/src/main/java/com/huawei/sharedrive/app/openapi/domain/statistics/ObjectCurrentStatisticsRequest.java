package com.huawei.sharedrive.app.openapi.domain.statistics;

import com.huawei.sharedrive.app.exception.InvalidParamException;


public class ObjectCurrentStatisticsRequest
{

    private Integer regionId;
    
    
    public static final String GROUPBY_REGION = "region";
    
    private String groupBy;

    public Integer getRegionId()
    {
        return regionId;
    }
    
    public void checkGroupBy() throws InvalidParamException
    {
        if(this.getGroupBy() == null)
        {
            return;
        }
        if(GROUPBY_REGION.equals(this.getGroupBy()))
        {
            return;
        }
        throw new InvalidParamException("Error groupBy " + this.getGroupBy());
    }
    
    public void setRegionId(Integer regionId)
    {
        this.regionId = regionId;
    }

    public String getGroupBy()
    {
        return groupBy;
    }

    public void setGroupBy(String groupBy)
    {
        this.groupBy = groupBy;
    }
    
}
