package com.huawei.sharedrive.uam.event.domain;

import java.io.Serializable;
import java.util.Date;

import org.aspectj.weaver.Dump.INode;

public class Event implements Serializable
{
    private static final long serialVersionUID = 3516121656058173516L;
    
    private Date createdAt;
    
    private String loginName;
    
    private INode dest;
    
    private String deviceAddress;
    
    private String deviceAgent;
    
    private String deviceArea;
    
    private String deviceSN;
    
    private int deviceType;
    
    private INode source;
    
    private EventType type;
    
    public Event()
    {
        
    }
    
    public Event(String deviceAddress, int deviceType, String deviceAgent, String deviceSN, String deviceArea)
    {
        this.deviceAddress = deviceAddress;
        this.deviceType = deviceType;
        this.deviceAgent = deviceAgent;
        this.deviceSN = deviceSN;
        this.deviceArea = deviceArea;
    }
    
    public Date getCreatedAt()
    {
        if (null != createdAt)
        {
            return (Date) createdAt.clone();
        }
        return null;
    }
    
    public INode getDest()
    {
        return dest;
    }
    
    public String getDeviceAddress()
    {
        return deviceAddress;
    }
    
    public String getDeviceAgent()
    {
        return deviceAgent;
    }
    
    public String getDeviceArea()
    {
        return deviceArea;
    }
    
    public String getDeviceSN()
    {
        return deviceSN;
    }
    
    public int getDeviceType()
    {
        return deviceType;
    }
    
    public INode getSource()
    {
        return source;
    }
    
    public EventType getType()
    {
        return type;
    }
    
    public void setCreatedAt(Date createdAt)
    {
        if (null != createdAt)
        {
            this.createdAt = (Date) createdAt.clone();
        }
        else
        {
            this.createdAt = null;
        }
    }
    
    public void setDest(INode dest)
    {
        this.dest = dest;
    }
    
    public void setDeviceAddress(String deviceAddress)
    {
        this.deviceAddress = deviceAddress;
    }
    
    public void setDeviceAgent(String deviceAgent)
    {
        this.deviceAgent = deviceAgent;
    }
    
    public void setDeviceArea(String deviceArea)
    {
        this.deviceArea = deviceArea;
    }
    
    public void setDeviceSN(String deviceSN)
    {
        this.deviceSN = deviceSN;
    }
    
    public void setDeviceType(int deviceType)
    {
        this.deviceType = deviceType;
    }
    
    public void setSource(INode source)
    {
        this.source = source;
    }
    
    public void setType(EventType type)
    {
        this.type = type;
    }
    
    public String getLoginName()
    {
        return loginName;
    }
    
    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }
}
