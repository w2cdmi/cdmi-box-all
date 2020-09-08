package com.huawei.sharedrive.isystem.monitor.domain;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;

import com.huawei.sharedrive.thrift.cluster.ClusterRunningInfo;


/**
 * ClusterRunningInfo
 * 
 * @author l00357199 20162016-1-5 上午11:25:56
 */
public class Cluster
{

    @Size(min = 1, max = 64)
    private String clusterName;
    
    public String getClusterName()
    {
        return clusterName;
    }
    
    public void setClusterName(String clusterName)
    {
        this.clusterName = clusterName;
    }
    
    public void setClusterServiceName(String clusterServiceName)
    {
        this.clusterServiceName = clusterServiceName;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
    public void setStatus(int status)
    {
        this.status = status;
    }
    
    public void setReportTime(long reportTime)
    {
        this.reportTime = reportTime;
    }
    
    public void setReserve(String reserve)
    {
        this.reserve = reserve;
    }
    
    public String getClusterServiceName()
    {
        return clusterServiceName;
    }
    
    public String getType()
    {
        return type;
    }
    
    public int getStatus()
    {
        return status;
    }
    
    public long getReportTime()
    {
        return reportTime;
    }
    
    public String getReserve()
    {
        return reserve;
    }
    

    @Size(min = 1, max = 64)
    private String clusterServiceName;
    
    @Size(min = 1, max = 64)
    private String type;
    
    private int status;
    
    @Range(min = 0)
    private long reportTime;
    
    @Size(min = 1, max = 512)
    private String reserve;
    
    public Cluster()
    {
        
    }
    
    public Cluster(ClusterRunningInfo clusterRunningInfo)
    {
        this.clusterName = clusterRunningInfo.clusterName;
        this.clusterServiceName = clusterRunningInfo.clusterServiceName;
        this.type = clusterRunningInfo.type;
        this.status = clusterRunningInfo.status;
        this.reportTime = clusterRunningInfo.reportTime;
        this.reserve = clusterRunningInfo.reserve;
        
    }
    
    @Override
    public String toString()
    {
        return "ClusterInfo:[" + clusterName + "," + clusterServiceName + "," + type + "," + status + ","
            + reportTime + "," + reserve + "]";
    }
}
