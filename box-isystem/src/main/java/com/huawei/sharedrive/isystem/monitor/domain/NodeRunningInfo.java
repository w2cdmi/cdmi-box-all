package com.huawei.sharedrive.isystem.monitor.domain;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.thrift.systemNode.NetworkInterfaceInfo;
import com.huawei.sharedrive.thrift.systemNode.SystemNodeRunningInfo;

/**
 * 节点监控
 * 
 * @author l00357199 20162016-1-5 下午5:36:52
 */
public class NodeRunningInfo
{
    private String clusterName;
    
    private String hostName;
    
    private int type;
    
    private String managerIp;
    
    private String ipmi;
    
    private long reportTime;
    
    private double cpuUsage;
    
    private int cpuCount;
    
    private int cpuThread;
    
    private int memoryUsage;
    
    private int memoryTotal;
    
    private double memoryRate;
    
    private String serviceIp;
    
    private int serviceNiccapacity;
    
    private double serviceRate;
    
    private String serviceStatus;
    
    private String manageIp;
    
    private int manageNiccapacity;
    
    private double manageRate;
    
    private String manageStatus;
    
    private String privateIp;
    
    private int privateNiccapacity;
    
    private double privateRate;
    
    private String privateStatus;
    
    @Range(min = 0)
    private long connectTotal;
    
    @Range(min = 0)
    private long establishedTotal;
    
    @Range(min = 0)
    private long fileHandleTotal;
    
    @Size(min = 1, max = 512)
    private String topInfo;
    
    private int status;
    
    @Size(min = 1, max = 512)
    private String reserve;
    
    // private String diskIO;//NodeDiskIO
    private static Logger logger = LoggerFactory.getLogger(NodeRunningInfo.class);
    
    public int getType()
    {
        return type;
    }
    
    public void setType(int type)
    {
        this.type = type;
    }
    
    public NodeRunningInfo()
    {
        
    }
    
    public void setStatus(int status)
    {
        this.status = status;
    }
    
    public void setReserve(String reserve)
    {
        this.reserve = reserve;
    }
    
    public NodeRunningInfo(SystemNodeRunningInfo systemNodeRunningInfo)
    {
        this.clusterName = systemNodeRunningInfo.clusterName;
        this.hostName = systemNodeRunningInfo.hostName;
        this.managerIp = systemNodeRunningInfo.managerIp;
        this.reportTime = systemNodeRunningInfo.reportTime;
        if (systemNodeRunningInfo.cpuInfo != null)
        {
            cpuUsage = systemNodeRunningInfo.cpuInfo.cpuUsage;
            cpuCount = systemNodeRunningInfo.cpuInfo.cpuCount;
            cpuThread = systemNodeRunningInfo.cpuInfo.cpuThread;
            
        }
        if (systemNodeRunningInfo.memoryInfo != null)
        {
            memoryUsage = (int) systemNodeRunningInfo.memoryInfo.getUsage();
            memoryTotal = (int) systemNodeRunningInfo.memoryInfo.getTotal();
            memoryRate = systemNodeRunningInfo.memoryInfo.getRate();
        }
        
        this.connectTotal = systemNodeRunningInfo.connectTotal;
        this.establishedTotal = systemNodeRunningInfo.establishedTotal;
        this.fileHandleTotal = systemNodeRunningInfo.fileHandleTotal;
        this.topInfo = systemNodeRunningInfo.topInfo;
        this.type = systemNodeRunningInfo.type;
        if (null != systemNodeRunningInfo.getNetworkInfo())
        {
            for (NetworkInterfaceInfo tmp : systemNodeRunningInfo.getNetworkInfo())
            {
                switch (tmp.type)
                {
                    case "service":
                        serviceIp = tmp.ip;
                        serviceNiccapacity = tmp.niccapacity;
                        serviceRate = tmp.rate;
                        serviceStatus = tmp.status;
                        
                        break;
                    
                    case "manager":
                        manageIp = tmp.ip;
                        manageNiccapacity = tmp.niccapacity;
                        manageRate = tmp.rate;
                        manageStatus = tmp.status;
                        break;
                    case "private":
                        privateIp = tmp.ip;
                        privateNiccapacity = tmp.niccapacity;
                        privateRate = tmp.rate;
                        privateStatus = tmp.status;
                        break;
                    // luhua
                    case "ipmi":
                        ipmi = tmp.ip;
                        break;
                    default:
                        break;
                }
            }
        }
        
    }
    
    public String getIpmiIP()
    {
        return ipmi;
    }
    
    public void setIpmiIP(String ipmiIP)
    {
        this.ipmi = ipmiIP;
    }
    
    public static Logger getLogger()
    {
        return logger;
    }
    
    public static void setLogger(Logger logger)
    {
        NodeRunningInfo.logger = logger;
    }
    
    public int getStatus()
    {
        return status;
    }
    
    public String getReserve()
    {
        return reserve;
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
    
    public long getConnectTotal()
    {
        return connectTotal;
    }
    
    public long getEstablishedTotal()
    {
        return establishedTotal;
    }
    
    public long getFileHandleTotal()
    {
        return fileHandleTotal;
    }
    
    public String getTopInfo()
    {
        return topInfo;
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
    
    public void setConnectTotal(long connectTotal)
    {
        this.connectTotal = connectTotal;
    }
    
    public void setEstablishedTotal(long establishedTotal)
    {
        this.establishedTotal = establishedTotal;
    }
    
    public void setFileHandleTotal(long fileHandleTotal)
    {
        this.fileHandleTotal = fileHandleTotal;
    }
    
    public void setTopInfo(String topInfo)
    {
        this.topInfo = topInfo;
    }
    
    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("NodeInfo:[" + clusterName + "," + hostName + "," + type + "," + managerIp + ","
            + reportTime + "," + cpuCount + "," + cpuThread + "," + cpuUsage + "," + memoryUsage + ","
            + memoryTotal + "," + memoryRate + "," + serviceIp + "," + serviceNiccapacity + "," + serviceRate
            + "," + serviceStatus + "," + manageIp + "," + manageNiccapacity + "," + manageRate + ","
            + manageStatus + "," + privateIp + "," + privateNiccapacity + "," + privateRate + ","
            + privateStatus + ",");
        
        sb.append(connectTotal + "," + establishedTotal + "," + fileHandleTotal + "," + status + ","
            + topInfo + "," + reserve + "]");
        return sb.toString();
    }

    public String getIpmi()
    {
        return ipmi;
    }

    public void setIpmi(String ipmi)
    {
        this.ipmi = ipmi;
    }

    public double getCpuUsage()
    {
        return cpuUsage;
    }

    public void setCpuUsage(double cpuUsage)
    {
        this.cpuUsage = cpuUsage;
    }

    public int getCpuCount()
    {
        return cpuCount;
    }

    public void setCpuCount(int cpuCount)
    {
        this.cpuCount = cpuCount;
    }

    public int getCpuThread()
    {
        return cpuThread;
    }

    public void setCpuThread(int cpuThread)
    {
        this.cpuThread = cpuThread;
    }

    public int getMemoryUsage()
    {
        return memoryUsage;
    }

    public void setMemoryUsage(int memoryUsage)
    {
        this.memoryUsage = memoryUsage;
    }

    public int getMemoryTotal()
    {
        return memoryTotal;
    }

    public void setMemoryTotal(int memoryTotal)
    {
        this.memoryTotal = memoryTotal;
    }

    public double getMemoryRate()
    {
        return memoryRate;
    }

    public void setMemoryRate(double memoryRate)
    {
        this.memoryRate = memoryRate;
    }

    public String getServiceIp()
    {
        return serviceIp;
    }

    public void setServiceIp(String serviceIp)
    {
        this.serviceIp = serviceIp;
    }

    public int getServiceNiccapacity()
    {
        return serviceNiccapacity;
    }

    public void setServiceNiccapacity(int serviceNiccapacity)
    {
        this.serviceNiccapacity = serviceNiccapacity;
    }

    public double getServiceRate()
    {
        return serviceRate;
    }

    public void setServiceRate(double serviceRate)
    {
        this.serviceRate = serviceRate;
    }

    public String getServiceStatus()
    {
        return serviceStatus;
    }

    public void setServiceStatus(String serviceStatus)
    {
        this.serviceStatus = serviceStatus;
    }

    public String getManageIp()
    {
        return manageIp;
    }

    public void setManageIp(String manageIp)
    {
        this.manageIp = manageIp;
    }

    public int getManageNiccapacity()
    {
        return manageNiccapacity;
    }

    public void setManageNiccapacity(int manageNiccapacity)
    {
        this.manageNiccapacity = manageNiccapacity;
    }

    public double getManageRate()
    {
        return manageRate;
    }

    public void setManageRate(double manageRate)
    {
        this.manageRate = manageRate;
    }

    public String getManageStatus()
    {
        return manageStatus;
    }

    public void setManageStatus(String manageStatus)
    {
        this.manageStatus = manageStatus;
    }

    public String getPrivateIp()
    {
        return privateIp;
    }

    public void setPrivateIp(String privateIp)
    {
        this.privateIp = privateIp;
    }

    public int getPrivateNiccapacity()
    {
        return privateNiccapacity;
    }

    public void setPrivateNiccapacity(int privateNiccapacity)
    {
        this.privateNiccapacity = privateNiccapacity;
    }

    public double getPrivateRate()
    {
        return privateRate;
    }

    public void setPrivateRate(double privateRate)
    {
        this.privateRate = privateRate;
    }

    public String getPrivateStatus()
    {
        return privateStatus;
    }

    public void setPrivateStatus(String privateStatus)
    {
        this.privateStatus = privateStatus;
    }
}
