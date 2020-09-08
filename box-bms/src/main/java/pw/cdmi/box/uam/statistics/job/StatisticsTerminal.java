package pw.cdmi.box.uam.statistics.job;

import java.util.Collections;

import org.apache.commons.lang.builder.HashCodeBuilder;

public class StatisticsTerminal
{
    private String clientAgent;
    
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
        boolean bool = false;
        if (obj == null)
        {
            bool = false;
        }
        else if (this == obj)
        {
            bool = true;
        }
        else if (!(obj instanceof StatisticsTerminal))
        {
            bool = false;
        }
        else if (obj.hashCode() == this.hashCode())
        {
            bool = true;
        }
        else
        {
            bool = false;
        }
        return bool;
    }
    
    private byte deviceType;
    
}
