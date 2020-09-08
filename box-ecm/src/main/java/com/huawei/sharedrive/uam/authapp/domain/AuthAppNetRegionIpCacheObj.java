package com.huawei.sharedrive.uam.authapp.domain;

public class AuthAppNetRegionIpCacheObj
{
    private long ipStartValue;
    
    private long ipEndValue;
    
    private Integer regionId;
    
    public AuthAppNetRegionIpCacheObj(long ipStartValue, long ipEndValue, Integer regionId)
    {
        this.ipStartValue = ipStartValue;
        this.ipEndValue = ipEndValue;
        this.regionId = regionId;
    }
    
    public long getIpStartValue()
    {
        return ipStartValue;
    }
    
    public void setIpStartValue(long ipStartValue)
    {
        this.ipStartValue = ipStartValue;
    }
    
    public long getIpEndValue()
    {
        return ipEndValue;
    }
    
    public void setIpEndValue(long ipEndValue)
    {
        this.ipEndValue = ipEndValue;
    }
    
    public Integer getRegionId()
    {
        return regionId;
    }
    
    public void setRegionId(Integer regionId)
    {
        this.regionId = regionId;
    }
    
    @Override
    public String toString()
    {
        return "AuthAppNetRegionIp [ipStartValue=" + ipStartValue + ", ipEndValue=" + ipEndValue
            + ", regionId=" + regionId + "]";
    }
}
