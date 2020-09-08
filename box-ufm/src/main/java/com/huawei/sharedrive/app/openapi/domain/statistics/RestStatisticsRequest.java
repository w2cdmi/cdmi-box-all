package com.huawei.sharedrive.app.openapi.domain.statistics;

import com.huawei.sharedrive.app.exception.InvalidParamException;

public class RestStatisticsRequest
{
    private String type;
    
    public RestStatisticsRequest()
    {
        
    }
    
    public RestStatisticsRequest(String type)
    {
        this.type = type;
    }
    
    public void checkParameter()
    {
        if (type == null)
        {
            throw new InvalidParamException("Order field invalid: " + type);
        }
        if (!isTypeVaild())
        {
            throw new InvalidParamException("Order field invalid: " + type);
        }
    }
    
    public String getType()
    {
        return type;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
    private boolean isTypeVaild()
    {
        RestStatisticsRequestType[] statisTypes = RestStatisticsRequestType.values();
        for (RestStatisticsRequestType statisType : statisTypes)
        {
            if (statisType.getType().equalsIgnoreCase(type))
            {
                return true;
            }
        }
        return false;
    }
}
