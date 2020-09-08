package com.huawei.sharedrive.isystem.dns.domain;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.huawei.sharedrive.isystem.util.Validate;

public class UasNode implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 2580431889583921642L;
    
    @NotNull
    @Size(min = 1, max = 45)
    @Pattern(regexp = Validate.REG_IPV4)
    private String managerIp;
    
    @NotNull
    @Size(min = 0, max = 5)
    private String managerport;
    
    @NotNull
    @Pattern(regexp = Validate.REG_IPV4)
    private String innerAddr;
    
    @NotNull
    @Size(min = 1, max = 128)
    @Pattern(regexp = Validate.REG_IPV4)
    private String serviceAddr;
    
    @Size(min = 0, max = 128)
    @Pattern(regexp = Validate.REG_IPV4)
    private String natAddr;
    
    private int satuts;
    
    private int runtimestate;
    
    private long lastReportTime;
    
    private boolean runtimeStatus = false;
    
    public String getManagerport()
    {
        return managerport;
    }
    
    public void setManagerport(String managerport)
    {
        this.managerport = managerport;
    }
    
    public int getSatuts()
    {
        return satuts;
    }
    
    public void setSatuts(int satuts)
    {
        this.satuts = satuts;
    }
    
    public int getRuntimestate()
    {
        return runtimestate;
    }
    
    public void setRuntimestate(int runtimestate)
    {
        this.runtimestate = runtimestate;
    }
    
    public boolean isRuntimeStatus()
    {
        return runtimeStatus;
    }
    
    public void setRuntimeStatus(boolean runtimeStatus)
    {
        this.runtimeStatus = runtimeStatus;
    }
    
    public long getLastReportTime()
    {
        return lastReportTime;
    }
    
    public void setLastReportTime(long lastReportTime)
    {
        this.lastReportTime = lastReportTime;
    }
    
    public String getInnerAddr()
    {
        return innerAddr;
    }
    
    public void setInnerAddr(String innerAddr)
    {
        this.innerAddr = innerAddr;
    }
    
    public String getNatAddr()
    {
        return natAddr;
    }
    
    public void setNatAddr(String natAddr)
    {
        this.natAddr = natAddr;
    }
    
    public String getManagerIp()
    {
        return managerIp;
    }
    
    public void setManagerIp(String managerIp)
    {
        this.managerIp = managerIp;
    }
    
    public String getServiceAddr()
    {
        return serviceAddr;
    }
    
    public void setServiceAddr(String serviceAddr)
    {
        this.serviceAddr = serviceAddr;
    }
    
}
