package com.huawei.sharedrive.isystem.monitor.domain;

import java.util.List;

/**
 * ServerRunningInfo
 * 
 * @author l00357199 20162016-1-5 上午11:22:08
 */
public class ClusterService
{
    private String serviceName;
    
    private String vip;
    
    private List<ClusterInstance> instances;
    
    public String getServiceName()
    {
        return serviceName;
    }
    
    public void setServiceName(String serviceName)
    {
        this.serviceName = serviceName;
    }
    
    public String getVip()
    {
        return vip;
    }
    
    public void setVip(String vip)
    {
        this.vip = vip;
    }
    
    public List<ClusterInstance> getInstances()
    {
        return instances;
    }
    
    public void setInstances(List<ClusterInstance> instances)
    {
        this.instances = instances;
    }
    
}
