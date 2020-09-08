package com.huawei.sharedrive.uam.statistics.domain;

import java.util.List;

public class TerminalCurrentVersionView
{
    private String deviceName;
    
    private int deviceType;
    
    private List<TerminalCurrentVersionDay> data;
    
    public String getDeviceName()
    {
        return deviceName;
    }
    
    public void setDeviceName(String deviceName)
    {
        this.deviceName = deviceName;
    }
    
    public int getDeviceType()
    {
        return deviceType;
    }
    
    public void setDeviceType(int deviceType)
    {
        this.deviceType = deviceType;
    }
    
    public List<TerminalCurrentVersionDay> getData()
    {
        return data;
    }
    
    public void setData(List<TerminalCurrentVersionDay> data)
    {
        this.data = data;
    }
}
