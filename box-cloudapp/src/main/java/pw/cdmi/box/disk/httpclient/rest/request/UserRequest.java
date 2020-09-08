package pw.cdmi.box.disk.httpclient.rest.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import pw.cdmi.box.disk.utils.BasicConstants;

public class UserRequest
{
    
    private String loginName;
    
    private String password;
    
    private int deviceType = BasicConstants.CLIENT_TYPE;
    
    @JsonProperty(value = "deviceSN")
    private String deviceSn;
    
    private String deviceOS;
    
    private String deviceName;
    
    private String deviceAddress;
    
    private String deviceAgent;
    
    public UserRequest()
    {
    }
    
    public UserRequest(String loginName, String password)
    {
        this.loginName = loginName;
        this.password = password;
    }
    
    public String getLoginName()
    {
        return loginName;
    }
    
    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }
    
    public String getPassword()
    {
        return password;
    }
    
    public void setPassword(String password)
    {
        this.password = password;
    }
    
    public int getDeviceType()
    {
        return deviceType;
    }
    
    public void setDeviceType(int deviceType)
    {
        this.deviceType = deviceType;
    }
    
    public String getDeviceSn()
    {
        return deviceSn;
    }
    
    public void setDeviceSn(String deviceSn)
    {
        this.deviceSn = deviceSn;
    }
    
    public String getDeviceOS()
    {
        return deviceOS;
    }
    
    public void setDeviceOS(String deviceOS)
    {
        this.deviceOS = deviceOS;
    }
    
    public String getDeviceName()
    {
        return deviceName;
    }
    
    public void setDeviceName(String deviceName)
    {
        this.deviceName = deviceName;
    }
    
    public String getDeviceAddress()
    {
        return deviceAddress;
    }
    
    public void setDeviceAddress(String deviceAddress)
    {
        this.deviceAddress = deviceAddress;
    }
    
    public String getDeviceAgent()
    {
        return deviceAgent;
    }
    
    public void setDeviceAgent(String deviceAgent)
    {
        this.deviceAgent = deviceAgent;
    }
    
}
