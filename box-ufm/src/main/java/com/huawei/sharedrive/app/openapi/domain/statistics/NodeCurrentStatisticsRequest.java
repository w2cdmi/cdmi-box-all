package com.huawei.sharedrive.app.openapi.domain.statistics;

import com.huawei.sharedrive.app.exception.InvalidParamException;

public class NodeCurrentStatisticsRequest
{
    
    public static final String GROUPBY_ALL = "all";
    
    public static final String GROUPBY_APP = "application";
    
    public static final String GROUPBY_DEFAULT = "all";
    
    public static final String GROUPBY_REGION = "region";
    

    private String appId;

    private String groupBy;

    private Integer regionId;

    public void checkGroupBy() throws InvalidParamException
    {
        if(this.getGroupBy() == null)
        {
            this.setGroupBy(GROUPBY_DEFAULT);
            return;
        }
        if(GROUPBY_ALL.equals(this.getGroupBy()))
        {
            return;
        }
        if(GROUPBY_APP.equals(this.getGroupBy()))
        {
            return;
        }
        if(GROUPBY_REGION.equals(this.getGroupBy()))
        {
            return;
        }
        throw new InvalidParamException("Error groupBy " + this.getGroupBy());
    }

    public String getAppId()
    {
        return appId;
    }
    
    public String getGroupBy()
    {
        return groupBy;
    }
    
    public Integer getRegionId()
    {
        return regionId;
    }
    
    
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
    public void setGroupBy(String groupBy)
    {
        this.groupBy = groupBy;
    }
    
    
    public void setRegionId(Integer regionId)
    {
        this.regionId = regionId;
    }
    
}
