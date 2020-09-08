package com.huawei.sharedrive.isystem.monitor.domain;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;

import com.huawei.sharedrive.thrift.systemNode.DiskInfo;

public class NodeDisk
{
    @Size(min = 1, max = 64)
    private String clusterName;
    
    @Size(min = 1, max = 64)
    private String hostName;
    
    @Range(min = 0)
    private long reportTime;
    
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
    
    public String getCatalogueName()
    {
        return catalogueName;
    }
    
    public void setCatalogueName(String catalogueName)
    {
        this.catalogueName = catalogueName;
    }
    
    public int getTotal()
    {
        return total;
    }
    
    public void setTotal(int total)
    {
        this.total = total;
    }
    
    public double getUsed()
    {
        return used;
    }
    
    public void setUsed(int used)
    {
        this.used = used;
    }
    
    public int getResidue()
    {
        return residue;
    }
    
    public void setResidue(int residue)
    {
        this.residue = residue;
    }
    
    @Size(min = 1, max = 64)
    private String catalogueName;
    
    @Range(min = 0)
    private int total;
    
    @Range(min = 0)
    private int used;
    
    @Range(min = 0)
    private double rate;
    
    @Range(min = 0)
    private int residue;
    
    public NodeDisk(String clusterName, String hostName, long reportTime, DiskInfo diskInfo)
    {
        this.clusterName = clusterName;
        this.hostName = hostName;
        this.reportTime = reportTime;
        this.catalogueName = diskInfo.catalogueName;
        this.total = diskInfo.total;
        this.used = (int) diskInfo.usage;
        this.residue = diskInfo.residue;
        this.rate = diskInfo.rate;
    }
    
    public NodeDisk()
    {
        
    }
    
    public double getRate()
    {
        return rate;
    }
    
    public void setRate(double rate)
    {
        this.rate = rate;
    }
    
    @Override
    public String toString()
    {
        return "NodeDisk:[" + clusterName + "," + hostName + "," + reportTime + "," + catalogueName + ","
            + used + "," + residue + "," + total + "]";
    }
}
