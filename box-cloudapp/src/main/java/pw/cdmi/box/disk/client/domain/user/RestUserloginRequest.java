package pw.cdmi.box.disk.client.domain.user;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RestUserloginRequest implements Serializable
{
    private static final long serialVersionUID = 1547850900957125484L;
    
    private String deviceAddress;
    
    @NotNull
    private String deviceAgent;
    
    @NotNull
    private String deviceName;
    
    @NotNull
    private String deviceOS;
    
    @NotNull
    @JsonProperty(value = "deviceSN")
    private String deviceSn;
    
    @Pattern(regexp = "^[12]{1}$")
    private int deviceType;
    
    @NotNull
    private String loginName;
    
    @NotNull
    private String password;
    
    private String appName;
    
    private String domain;
    
    public String getAppName()
    {
        return appName;
    }
    
    public void setAppName(String appName)
    {
        this.appName = appName;
    }
    
    public String getDeviceAddress()
    {
        return deviceAddress;
    }
    
    public String getDeviceAgent()
    {
        return deviceAgent;
    }
    
    public String getDeviceName()
    {
        return deviceName;
    }
    
    public String getDeviceOS()
    {
        return deviceOS;
    }
    
    public String getDeviceSn()
    {
        return deviceSn;
    }
    
    public int getDeviceType()
    {
        return deviceType;
    }
    
    public String getLoginName()
    {
        return loginName;
    }
    
    public String getPassword()
    {
        return password;
    }
    
    public void setDeviceAddress(String deviceAddress)
    {
        this.deviceAddress = deviceAddress;
    }
    
    public void setDeviceAgent(String deviceAgent)
    {
        this.deviceAgent = deviceAgent;
    }
    
    public void setDeviceName(String deviceName)
    {
        this.deviceName = deviceName;
    }
    
    public void setDeviceOS(String deviceOS)
    {
        this.deviceOS = deviceOS;
    }
    
    public void setDeviceSn(String deviceSn)
    {
        this.deviceSn = deviceSn;
    }
    
    public void setDeviceType(int deviceType)
    {
        this.deviceType = deviceType;
    }
    
    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }
    
    public void setPassword(String password)
    {
        this.password = password;
    }
    
    public String getDomain()
    {
        return domain;
    }
    
    public void setDomain(String domain)
    {
        this.domain = domain;
    }
}
