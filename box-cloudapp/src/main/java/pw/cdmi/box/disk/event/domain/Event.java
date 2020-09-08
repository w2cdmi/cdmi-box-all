package pw.cdmi.box.disk.event.domain;

import java.io.Serializable;
import java.util.Date;

import pw.cdmi.box.disk.client.domain.node.INode;

public class Event implements Serializable
{
    private static final long serialVersionUID = 3516121656058173516L;
    
    /**
     * Operation time
     */
    private Date createdAt;
    
    /**
     * The operation of the user ID
     */
    private long createdBy;
    
    /**
     * The operation of the target object
     */
    private INode dest;
    
    /**
     * The client IP address
     */
    private String deviceAddress;
    
    /**
     * The client software information
     */
    private String deviceAgent;
    
    /**
     * Equipment area
     */
    private String deviceArea;
    
    /**
     * device identification
     */
    private String deviceSn;
    
    /**
     * device type
     */
    private int deviceType;
    
    /**
     * The operation of the source object
     */
    private INode source;
    
    /**
     * The event type
     */
    private EventType type;
    
    public Event()
    {
        
    }
    
    public Event(String deviceAddress, int deviceType, String deviceAgent, String deviceSn, String deviceArea)
    {
        this.deviceAddress = deviceAddress;
        this.deviceType = deviceType;
        this.deviceAgent = deviceAgent;
        this.deviceSn = deviceSn;
        this.deviceArea = deviceArea;
    }
    
    public Date getCreatedAt()
    {
        if (createdAt == null)
        {
            return null;
        }
        return (Date) createdAt.clone();
    }
    
    public long getCreatedBy()
    {
        return createdBy;
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
    
    public String getDeviceSn()
    {
        return deviceSn;
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
        if (createdAt == null)
        {
            this.createdAt = null;
        }
        else
        {
            this.createdAt = (Date) createdAt.clone();
        }
    }
    
    public void setCreatedBy(long createdBy)
    {
        this.createdBy = createdBy;
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
    
    public void setDeviceSn(String deviceSn)
    {
        this.deviceSn = deviceSn;
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
}
