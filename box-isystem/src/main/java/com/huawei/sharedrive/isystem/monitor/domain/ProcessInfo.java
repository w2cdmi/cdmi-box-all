package com.huawei.sharedrive.isystem.monitor.domain;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;

import com.huawei.sharedrive.thrift.process.ProcessRunningInfo;

public class ProcessInfo
{
    @Size(min = 1, max = 64)
    private String clusterName;
    
    @Size(min = 1, max = 64)
    private String hostName;
    
    @Size(min = 1, max = 64)
    private String managerIp;
    
    @Range(min = 0)
    private long reportTime;
    
    @Size(min = 1, max = 64)
    private String processName;
    
    private double cpuUsage;
    
    private double memoryUsage;
    
    private long fileHandleTotal;
    
    private int threadTotal;
    
    @Size(min = 1, max = 64)
    private String type;
    
    private int status;
    
    @Size(min = 1, max = 64)
    private String role;
    
    private int port;// 端口
    
    private int processCount;// 进程总数
    
    @Size(min = 1, max = 64)
    private String syn;// 同步状态
    
    @Size(min = 1, max = 64)
    private String vip;// 虚拟ip
    
    @Size(min = 1, max = 512)
    private String reserve;
    
    public ProcessInfo(ProcessRunningInfo processRunningInfo)
    {
        clusterName = processRunningInfo.clusterName;
        hostName = processRunningInfo.hostName;
        managerIp = processRunningInfo.managerIp;
        reportTime = processRunningInfo.reportTime;
        processName = processRunningInfo.processName;
        cpuUsage = processRunningInfo.cpuUsage;
        memoryUsage = processRunningInfo.memoryUsage;
        fileHandleTotal = processRunningInfo.fileHandleTotal;
        threadTotal = processRunningInfo.threadTotal;
        type = processRunningInfo.type;
        status = processRunningInfo.status;
        
        port = processRunningInfo.port;
        role = processRunningInfo.role;
        processCount = processRunningInfo.processCount;
        syn = processRunningInfo.sync;
        vip = processRunningInfo.vip;
        // reserve=processRunningInfo.reserve;
    }
    
    public String getRole()
    {
        return role;
    }
    
    public void setRole(String role)
    {
        this.role = role;
    }
    
    public int getPort()
    {
        return port;
    }
    
    public void setPort(int port)
    {
        this.port = port;
    }
    
    public int getProcessCount()
    {
        return processCount;
    }
    
    public void setProcessCount(int processCount)
    {
        this.processCount = processCount;
    }
    
    public String getSyn()
    {
        return syn;
    }
    
    public void setSyn(String syn)
    {
        this.syn = syn;
    }
    
    public String getVip()
    {
        return vip;
    }
    
    public void setVip(String vip)
    {
        this.vip = vip;
    }
    
    public String getReserve()
    {
        return reserve;
    }
    
    public void setReserve(String reserve)
    {
        this.reserve = reserve;
    }
    
    public ProcessInfo()
    {
    }
    
    public String getClusterName()
    {
        return clusterName;
    }
    
    public String getHostName()
    {
        return hostName;
    }
    
    public String getManagerIp()
    {
        return managerIp;
    }
    
    public long getReportTime()
    {
        return reportTime;
    }
    
    public String getProcessName()
    {
        return processName;
    }
    
    public double getCpuUsage()
    {
        return cpuUsage;
    }
    
    public double getMemoryUsage()
    {
        return memoryUsage;
    }
    
    public void setClusterName(String clusterName)
    {
        this.clusterName = clusterName;
    }
    
    public void setHostName(String hostName)
    {
        this.hostName = hostName;
    }
    
    public void setManagerIp(String managerIp)
    {
        this.managerIp = managerIp;
    }
    
    public void setReportTime(long reportTime)
    {
        this.reportTime = reportTime;
    }
    
    public void setProcessName(String processName)
    {
        this.processName = processName;
    }
    
    public void setCpuUsage(double cpuUsage)
    {
        this.cpuUsage = cpuUsage;
    }
    
    public void setMemoryUsage(double memoryUsage)
    {
        this.memoryUsage = memoryUsage;
    }
    
    public void setFileHandleTotal(long fileHandleTotal)
    {
        this.fileHandleTotal = fileHandleTotal;
    }
    
    public void setThreadTotal(int threadTotal)
    {
        this.threadTotal = threadTotal;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
    public void setStatus(int status)
    {
        this.status = status;
    }
    
    public long getFileHandleTotal()
    {
        return fileHandleTotal;
    }
    
    public int getThreadTotal()
    {
        return threadTotal;
    }
    
    public String getType()
    {
        return type;
    }
    
    public int getStatus()
    {
        return status;
    }
    
    @Override
    public String toString()
    {
        // TODO Auto-generated method stub
        return "ProcessInfo:[" + clusterName + "," + hostName + "," + processName + "," + managerIp + ","
            + reportTime + "," + cpuUsage + "," + memoryUsage + "," + fileHandleTotal + "," + threadTotal
            + "," + type + "," + status + "," + port + "," + role + "," + processCount + "," + syn + ","
            + vip + "," + reserve + "," + "]";
    }
}
