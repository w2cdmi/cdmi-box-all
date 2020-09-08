package com.huawei.sharedrive.uam.enterprise.domain;

import java.io.Serializable;

public class ImportNetRegionIp implements Serializable
{
    private static final long serialVersionUID = 2830850552125651742L;
    
    private NetRegionIp netRegionIp;
    
    private boolean parseSucess;
    
    private String errorCode;
    
    public NetRegionIp getNetRegionIp()
    {
        return netRegionIp;
    }
    
    public void setNetRegionIp(NetRegionIp netRegionIp)
    {
        this.netRegionIp = netRegionIp;
    }
    
    public boolean isParseSucess()
    {
        return parseSucess;
    }
    
    public void setParseSucess(boolean parseSucess)
    {
        this.parseSucess = parseSucess;
    }
    
    public String getErrorCode()
    {
        return errorCode;
    }
    
    public void setErrorCode(String errorCode)
    {
        this.errorCode = errorCode;
    }
    
}
