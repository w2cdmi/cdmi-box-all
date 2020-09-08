package com.huawei.sharedrive.app.openapi.domain.statistics;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.app.exception.InvalidParamException;

public class RestUserCurrentStatisticsRequest
{
    private String groupBy;
    
    private Integer regionId;
    
    private String appId;
    
    
    public String getGroupBy()
    {
        return groupBy;
    }
    
    public void setGroupBy(String groupBy)
    {
        this.groupBy = groupBy;
    }
    
    public Integer getRegionId()
    {
        return regionId;
    }
    
    public void setRegionId(Integer regionId)
    {
        this.regionId = regionId;
    }
    
    public String getAppId()
    {
        return appId;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
    public void checkParameter() throws InvalidParamException
    {
        if (StringUtils.isBlank(groupBy))
        {
            this.groupBy = "all";
        }
        else
        {
            if (!"region".equals(groupBy) && !"application".equals(groupBy) && !"all".equals(groupBy))
            {
                throw new InvalidParamException("groupBy is invalid:" + groupBy);
            }
        }
    }
}
