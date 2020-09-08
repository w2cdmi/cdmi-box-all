package com.huawei.sharedrive.uam.statistics.domain;

import java.util.List;

public class TerminalDeviceTypeView
{
    
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
    
    public List<TerminalHistoryNode> getDataList()
    {
        return dataList;
    }
    
    public void setDataList(List<TerminalHistoryNode> dataList)
    {
        this.dataList = dataList;
    }
    
    private String deviceName;
    
    private int deviceType;
    
    private List<TerminalHistoryNode> dataList;
    
}
