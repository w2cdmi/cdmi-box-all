package com.huawei.sharedrive.app.plugins.cluster.domain;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.huawei.sharedrive.app.utils.Constants;

/**
 * 服务集群域对象
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2015-4-10
 * @see
 * @since
 */
public class PluginServiceCluster implements Serializable
{
    
    private static final String[] APP_KIA = {Constants.APPID_SECURITYSCAN};
    
    /** 服务集群状态: 0 - 正常 */
    public static final byte STATUS_NORMAL = 0;
    
    /** 服务集群状态: 1 - 部分异常 */
    public static final byte STATUS_ABNORMAL = 1;
    
    /** 服务集群状态: 2 - 离线(全部异常) */
    public static final byte STATUS_OFFLINE = 2;
    
    private static final long serialVersionUID = -4364118815693739476L;
    
    // 外挂服务集群id
    private int clusterId;
    
    // 该服务集群所属的dss
    private Integer dssId;
    
    // 该服务集群所属的外挂应用id
    private String appId;
    
    // 集群名称
    private String name;
    
    // 集群描述信息
    private String description;
    
    // 集群状态监控周期, 单位:秒
    @JsonProperty(value = "monitorCycle")
    private Integer monitorPeriod;
    
    // 集群状态, 0:正常 1.部分异常 2.离线
    @JsonProperty(value = "state")
    private Byte status;
    
    // 最后一次监控时间
    private Date lastMonitorTime;
    
    public String getAppId()
    {
        return appId;
    }
    
    public int getClusterId()
    {
        return clusterId;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public Integer getDssId()
    {
        return dssId;
    }
    
    public Date getLastMonitorTime()
    {
        if (lastMonitorTime == null)
        {
            return null;
        }
        return (Date) lastMonitorTime.clone();
    }
    
    public Integer getMonitorPeriod()
    {
        return monitorPeriod;
    }
    
    public String getName()
    {
        return name;
    }
    
    public Byte getStatus()
    {
        return status;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
    public void setClusterId(int clusterId)
    {
        this.clusterId = clusterId;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public void setDssId(Integer dssId)
    {
        this.dssId = dssId;
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
    
    public void setMonitorPeriod(Integer monitorPeriod)
    {
        this.monitorPeriod = monitorPeriod;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setStatus(Byte status)
    {
        this.status = status;
    }
    
    public Boolean isAppKIA()
    {
        if (StringUtils.isNotEmpty(this.appId))
        {
            for (String s : APP_KIA)
            {
                if (this.appId.equals(s))
                {
                    return true;
                }
            }
        }
        return false;
    }
}
