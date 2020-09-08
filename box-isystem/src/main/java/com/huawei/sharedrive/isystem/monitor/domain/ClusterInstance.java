package com.huawei.sharedrive.isystem.monitor.domain;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;

import com.huawei.sharedrive.thrift.cluster.ServerRunningInfo;

/**ServerRunningInfo
 * @author l00357199  
 * 20162016-1-5 上午11:22:08 
 */
public class ClusterInstance
{
    public void setClusterName(String clusterName)
    {
        this.clusterName = clusterName;
    }
    public void setClusterServiceName(String clusterServiceName)
    {
        this.clusterServiceName = clusterServiceName;
    }
    public void setHostName(String hostName)
    {
        this.hostName = hostName;
    }
    public void setStatus(int status)
    {
        this.status = status;
    }
    public void setRunRole(String runRole)
    {
        this.runRole = runRole;
    }
    public void setDataStatus(String dataStatus)
    {
        this.dataStatus = dataStatus;
    }
    public void setReportTime(long reportTime)
    {
        this.reportTime = reportTime;
    }
    public void setVip(String vip)
    {
        this.vip = vip;
    }
    @Size(min = 1, max = 64)
    private String clusterName;
    public String getClusterName()
    {
        return clusterName;
    }
    public String getClusterServiceName()
    {
        return clusterServiceName;
    }
    public String getHostName()
    {
        return hostName;
    }
    public int getStatus()
    {
        return status;
    }
    public String getRunRole()
    {
        return runRole;
    }
    public String getDataStatus()
    {
        return dataStatus;
    }
    public long getReportTime()
    {
        return reportTime;
    }
    public String getVip()
    {
        return vip;
    }
    @Size(min = 1, max = 64)
    private String clusterServiceName;
    //hostname或者IP+端口
    @Size(min = 1, max = 64)
    private String hostName;
    //@Size(min = 1, max = 64)
    private int status;
    @Size(min = 1, max = 64)
    private String runRole;
    @Size(min = 1, max = 64)
    private String dataStatus;
    @Range(min=0)
    private long reportTime;
    @Size(min = 1, max = 64)
    private String vip;
    @Size(min = 1, max = 64)
    private String innerIP;
    
    public String getInnerIP()
    {
        return innerIP;
    }
    public void setInnerIP(String innerIP)
    {
        this.innerIP = innerIP;
    }
    
    public ClusterInstance( )
    {
    }
    public ClusterInstance(ServerRunningInfo serverRunningInfo )
    {
         this.clusterName = serverRunningInfo.clusterName;
         this.clusterServiceName=serverRunningInfo.clusterServiceName;
         this.hostName=serverRunningInfo.hostname;
         this.status=serverRunningInfo.status;
         this.runRole=serverRunningInfo.runRole;
         this.dataStatus=serverRunningInfo.dataStatus;
         this.reportTime=serverRunningInfo.reportTime;
         this.vip=serverRunningInfo.vip;
         this.innerIP=serverRunningInfo.inner;
    }
    
    @Override
    public String toString()
    {
        // TODO Auto-generated method stub
        return "ClusterInstance:["+clusterName+","+clusterServiceName+","+hostName+","+reportTime+","+runRole+","+status+","+dataStatus+","+vip+","+innerIP+"]";
    }
    
}
