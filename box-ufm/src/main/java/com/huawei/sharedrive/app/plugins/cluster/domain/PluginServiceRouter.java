package com.huawei.sharedrive.app.plugins.cluster.domain;

import java.io.Serializable;

/**
 * 服务集群 - DSS关系对象
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2015-4-10
 * @see
 * @since
 */
public class PluginServiceRouter implements Serializable
{
    
    private static final long serialVersionUID = -1968579991646456976L;
    
    private Integer dssId;
    
    // 处理dss数据的服务集群id
    private Integer clusterId;
    
    public Integer getClusterId()
    {
        return clusterId;
    }
    
    public Integer getDssId()
    {
        return dssId;
    }
    
    public void setClusterId(Integer clusterId)
    {
        this.clusterId = clusterId;
    }
    
    public void setDssId(Integer dssId)
    {
        this.dssId = dssId;
    }
    
}
