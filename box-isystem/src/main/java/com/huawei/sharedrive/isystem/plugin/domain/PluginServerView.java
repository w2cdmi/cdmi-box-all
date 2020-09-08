package com.huawei.sharedrive.isystem.plugin.domain;

import java.io.Serializable;
import java.util.Date;

import com.huawei.sharedrive.thrift.pluginserver.TPluginServerCluster;

public class PluginServerView implements Serializable
{
    private static final long serialVersionUID = 1349711531857245711L;

    private long clusterId;
    
    private int dssId;
    
    private String dssName;
    
    private String name;
    
    private String description;
    
    private String appId;
    
    private int monitorCycle;
    
    private int state;
    
    private Date lastMonitorTime;
    
    public long getClusterId()
    {
        return clusterId;
    }
    
    public void setClusterId(long clusterId)
    {
        this.clusterId = clusterId;
    }
    
    public int getDssId()
    {
        return dssId;
    }
    
    public void setDssId(int dssId)
    {
        this.dssId = dssId;
    }
    
    public String getDssName()
    {
        return dssName;
    }
    
    public void setDssName(String dssName)
    {
        this.dssName = dssName;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public String getAppId()
    {
        return appId;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
    public int getMonitorCycle()
    {
        return monitorCycle;
    }
    
    public void setMonitorCycle(int monitorCycle)
    {
        this.monitorCycle = monitorCycle;
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
    
    public PluginServerView()
    {
    }
    
    public PluginServerView(TPluginServerCluster pluginServerCluster)
    {
        if (null != pluginServerCluster)
        {
            this.clusterId = pluginServerCluster.getClusterId();
            this.dssId = pluginServerCluster.getDssId();
            this.name = pluginServerCluster.getName();
            this.description = pluginServerCluster.getDescription();
            this.appId = pluginServerCluster.getAppId();
            this.monitorCycle = pluginServerCluster.getMonitorCycle();
            this.state = pluginServerCluster.getState();
            this.lastMonitorTime = new Date(pluginServerCluster.getLastMonitorTime());
        }
    }
    
}
