package com.huawei.sharedrive.isystem.monitor.domain;

public class SystemClusterInfo
{
    private String systemName;// '界面显示的系统集群名称，如UAS，DSS-1，DSS-2',
    
    private int resourceGrounpId;// '资源组ID',
    
    private int type;// '0:UAS,1:DSS',
    
    private String clusterName;// '上报集群集群名称',
    
    private int status;// '集群状态
    
    public String getSystemName()
    {
        return systemName;
    }
    
    public void setSystemName(String systemName)
    {
        this.systemName = systemName;
    }
    
    public int getResourceGrounpId()
    {
        return resourceGrounpId;
    }
    
    public void setResourceGrounpId(int resourceGrounpId)
    {
        this.resourceGrounpId = resourceGrounpId;
    }
    
    public int getType()
    {
        return type;
    }
    
    public void setType(int type)
    {
        this.type = type;
    }
    
    public String getClusterName()
    {
        return clusterName;
    }
    
    public void setClusterName(String clusterName)
    {
        this.clusterName = clusterName;
    }
    
    public int getStatus()
    {
        return status;
    }
    
    public void setStatus(int status)
    {
        this.status = status;
    }
}
