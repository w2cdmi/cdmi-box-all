package com.huawei.sharedrive.app.openapi.domain.statistics;

public enum RestStatisticsRequestType
{
    TEAMSPACE("teamspace"), TOTAL("total"), USER("user");
    
    private String type;
    
    private RestStatisticsRequestType(String type)
    {
        this.type = type;
    }
    
    public String getType()
    {
        return type;
    }
}
