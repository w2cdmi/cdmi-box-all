package com.huawei.sharedrive.app.test.domain.user;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class RestUserloginRequest implements Serializable
{
    private static final long serialVersionUID = 1547850900957125484L;
    
    // 客户端IP
    private String deviceAddress;
    
    // 客户端版本
    @NotNull
    private String deviceAgent;
    
    // 客户端名称,PC机器名,手机型号
    @NotNull
    private String deviceName;
    
    // 客户端 操作系统
    @NotNull
    private String deviceOS;
    
    // 客户端SN序列号
    @NotNull
    private String deviceSN;
    
    // 客户端类型 1:PC 2:andriod
    @Pattern(regexp = "^[12]{1}$")
    private int deviceType;
    
    @NotNull
    private String loginName;
    
    @NotNull
    private String password;
    
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
    
    public String getDeviceSN()
    {
        return deviceSN;
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
    
    public void setDeviceSN(String deviceSN)
    {
        this.deviceSN = deviceSN;
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
    
}
