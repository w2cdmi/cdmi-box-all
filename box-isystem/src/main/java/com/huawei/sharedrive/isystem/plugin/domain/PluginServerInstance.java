package com.huawei.sharedrive.isystem.plugin.domain;

import java.util.Date;

import com.huawei.sharedrive.thrift.pluginserver.TPluginServerInstance;

public class PluginServerInstance
{
    private String ip;
    private long  clusterId;
    private String name;
    private int state;
    private Date lastMonitorTime;
    
    public PluginServerInstance()
    {
        
    }
    public PluginServerInstance(TPluginServerInstance instance)
    {
        this.ip=instance.getIp();
        this.clusterId=instance.getClusterId();
        this.name=instance.getName();
        this.state=instance.getState();
        if(instance.getLastMonitorTime()!=0)
        {
            this.lastMonitorTime=new Date(instance.getLastMonitorTime());
        }
    }
    public String getIp()
    {
        return ip;
    }
    public void setIp(String ip)
    {
        this.ip = ip;
    }
    public long getClusterId()
    {
        return clusterId;
    }
    public void setClusterId(long clusterId)
    {
        this.clusterId = clusterId;
    }
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public int getState()
    {
        return state;
    }
    public void setState(int state)
    {
        this.state = state;
    }
    public Date getLastMonitorTime()
    {
        return lastMonitorTime != null ? new Date(lastMonitorTime.getTime()) : null;
    }
    public void setLastMonitorTime(Date lastMonitorTime)
    {
        this.lastMonitorTime = lastMonitorTime != null ? new Date(lastMonitorTime.getTime()) : null;
    }
    
}
