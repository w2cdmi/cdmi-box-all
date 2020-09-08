package com.huawei.sharedrive.uam.statistics.domain;

public class UserClusterStatisticsInfo
{
    private Long begin;
    
    private Long end;
    
    private Long userCount;
    
    public Long getBegin()
    {
        return begin;
    }
    
    public void setBegin(Long begin)
    {
        this.begin = begin;
    }
    
    public Long getEnd()
    {
        return end;
    }
    
    public void setEnd(Long end)
    {
        this.end = end;
    }
    
    public Long getUserCount()
    {
        return userCount;
    }
    
    public void setUserCount(Long userCount)
    {
        this.userCount = userCount;
    }
}
