package pw.cdmi.box.disk.client.api;

public class RestTerminalRsp
{
    private String deviceType;
    
    private String deviceName;
    
    private String deviceOS;
    
    private String deviceAgent;
    
    private String lastAccessIP;
    
    private Long lastAccessAt;
    
    public String getDeviceType()
    {
        return deviceType;
    }
    
    public void setDeviceType(String deviceType)
    {
        this.deviceType = deviceType;
    }
    
    public String getDeviceName()
    {
        return deviceName;
    }
    
    public void setDeviceName(String deviceName)
    {
        this.deviceName = deviceName;
    }
    
    public String getDeviceOS()
    {
        return deviceOS;
    }
    
    public void setDeviceOS(String deviceOS)
    {
        this.deviceOS = deviceOS;
    }
    
    public String getDeviceAgent()
    {
        return deviceAgent;
    }
    
    public void setDeviceAgent(String deviceAgent)
    {
        this.deviceAgent = deviceAgent;
    }
    
    public String getLastAccessIP()
    {
        return lastAccessIP;
    }
    
    public void setLastAccessIP(String lastAccessIP)
    {
        this.lastAccessIP = lastAccessIP;
    }
    
    public Long getLastAccessAt()
    {
        return lastAccessAt;
    }
    
    public void setLastAccessAt(Long lastAccessAt)
    {
        this.lastAccessAt = lastAccessAt;
    }
    
}
