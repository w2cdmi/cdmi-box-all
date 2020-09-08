package com.huawei.sharedrive.uam.statistics.job;

import java.util.Collections;

import org.apache.commons.lang.builder.HashCodeBuilder;

public class StatisticsTerminal
{
    private String clientAgent;
    
    private byte deviceType;
    
    public String getClientAgent()
    {
        return clientAgent;
    }
    
    public void setClientAgent(String clientAgent)
    {
        this.clientAgent = clientAgent;
    }
    
    public byte getDeviceType()
    {
        return deviceType;
    }
    
    public void setDeviceType(byte deviceType)
    {
        this.deviceType = deviceType;
    }
    
    @Override
    public String toString()
    {
        return this.getClass().getName() + hashCode();
    }
    
    @Override
    public int hashCode()
    {
        return HashCodeBuilder.reflectionHashCode(this, Collections.EMPTY_LIST);
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (this == obj)
        {
            return true;
        }
        if (!(obj instanceof StatisticsTerminal))
        {
            return false;
        }
        if (obj.hashCode() == this.hashCode())
        {
            return true;
        }
        return false;
    }
}
