package com.huawei.sharedrive.app.plugins.cluster.domain;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 服务集群实例域对象
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2015-4-10
 * @see
 * @since
 */
public class PluginServiceInstance implements Serializable
{
    /** 服务实例类型: 0 - Worker */
    public static final byte TYPE_WORKER = 0;
    
    /** 服务实例类型: 1 - Agent */
    public static final byte TYPE_AGENT = 1;
    
    private static final long serialVersionUID = -7380064241235721620L;
    
    // 服务实例ip地址
    private String ip;
    
    // 该服务实例所属的服务集群id
    private int clusterId;
    
    // 实例类型 0.Worker 1.Agent
    private Byte type;
    
    // 服务实例名称
    private String name;
    
    // 服务实例状态
    @JsonProperty(value = "state")
    private Byte status;
    
    // 最后监控时间
    private Date lastMonitorTime;
    
    public PluginServiceInstance()
    {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public PluginServiceInstance(String ip, int clusterId, String name)
    {
        super();
        this.ip = ip;
        this.clusterId = clusterId;
        this.name = name;
    }
    
    public int getClusterId()
    {
        return clusterId;
    }
    
    public String getIp()
    {
        return ip;
    }
    
    public Date getLastMonitorTime()
    {
        if (lastMonitorTime == null)
        {
            return null;
        }
        return (Date) lastMonitorTime.clone();
    }
    
    public String getName()
    {
        return name;
    }
    
    public Byte getStatus()
    {
        return status;
    }
    
    public Byte getType()
    {
        return type;
    }
    
    public void setClusterId(int clusterId)
    {
        this.clusterId = clusterId;
    }
    
    public void setIp(String ip)
    {
        this.ip = ip;
    }
    
    public void setLastMonitorTime(Date lastMonitorTime)
    {
        if (lastMonitorTime == null)
        {
            this.lastMonitorTime = null;
        }
        else
        {
            this.lastMonitorTime = (Date) lastMonitorTime.clone();
        }
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setStatus(Byte status)
    {
        this.status = status;
    }
    
    public void setType(Byte type)
    {
        this.type = type;
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + clusterId;
        result = prime * result + ((ip == null) ? 0 : ip.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (this == obj)
        {
            return true;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        PluginServiceInstance other = (PluginServiceInstance) obj;
        if (clusterId != other.clusterId)
        {
            return false;
        }
        if (ip == null)
        {
            if (other.ip != null)
            {
                return false;
            }
        }
        else if (!ip.equals(other.ip))
        {
            return false;
        }
        if (name == null)
        {
            if (other.name != null)
            {
                return false;
            }
        }
        else if (!name.equals(other.name))
        {
            return false;
        }
        return true;
    }
    
}
