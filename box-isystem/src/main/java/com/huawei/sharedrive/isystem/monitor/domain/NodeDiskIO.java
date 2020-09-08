package com.huawei.sharedrive.isystem.monitor.domain;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;

import com.huawei.sharedrive.thrift.systemNode.DiskIO;

public class NodeDiskIO
{
    @Size(min = 1, max = 64)
    private String clusterName;
    
    @Size(min = 1, max = 64)
    private String hostName;
    
    @Range(min = 0)
    private long reportTime;
    
    @Size(min = 1, max = 64)
    private String diskName;
    
    public void setRate(double rate)
    {
        this.rate = rate;
    }
    
    @Range(min = 0)
    private int avgeResponeTime;
    
    public String getClusterName()
    {
        return clusterName;
    }
    
    public void setClusterName(String clusterName)
    {
        this.clusterName = clusterName;
    }
    
    public String getHostName()
    {
        return hostName;
    }
    
    public void setHostName(String hostName)
    {
        this.hostName = hostName;
    }
    
    public long getReportTime()
    {
        return reportTime;
    }
    
    public void setReportTime(long reportTime)
    {
        this.reportTime = reportTime;
    }
    
    public String getDiskName()
    {
        return diskName;
    }
    
    public void setDiskName(String diskName)
    {
        this.diskName = diskName;
    }
    
    public int getAvgeResponeTime()
    {
        return avgeResponeTime;
    }
    
    public void setAvgeResponeTime(int avgeResponeTime)
    {
        this.avgeResponeTime = avgeResponeTime;
    }
    
    public double getRate()
    {
        return rate;
    }
    
    public void setRate(int rate)
    {
        this.rate = rate;
    }
    
    @Range(min = 0)
    private double rate;
    
    public NodeDiskIO()
    {
        
    }
    
    public NodeDiskIO(String clusterName, String hostName, long reportTime, DiskIO diskIO)
    {
        this.clusterName = clusterName;
        this.hostName = hostName;
        this.reportTime = reportTime;
        this.diskName = diskIO.deskName;
        this.avgeResponeTime = (int) diskIO.avgeResponseTime;
        this.rate = diskIO.rate;
    }
    
    @Override
    public String toString()
    {
        return "NodeDiskIO:[" + clusterName + "," + hostName + "," + reportTime + "," + diskName + ","
            + avgeResponeTime + "," + rate + "]";
    }
}
