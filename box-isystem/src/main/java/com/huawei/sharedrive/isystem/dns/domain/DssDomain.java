package com.huawei.sharedrive.isystem.dns.domain;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.huawei.sharedrive.isystem.cluster.domain.DataCenter;

public class DssDomain implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    @NotNull
    private DnsDomain dnsDomain;
    
    @NotNull
    private DataCenter dataCenter;
    
    public DnsDomain getDnsDomain()
    {
        return dnsDomain;
    }
    
    public void setDnsDomain(DnsDomain dnsDomain)
    {
        this.dnsDomain = dnsDomain;
    }
    
    public DataCenter getDataCenter()
    {
        return dataCenter;
    }
    
    public void setDataCenter(DataCenter dataCenter)
    {
        this.dataCenter = dataCenter;
    }
    
    public DssDomain(DnsDomain dnsDomain, DataCenter dataCenter)
    {
        this.dnsDomain = dnsDomain;
        this.dataCenter = dataCenter;
    }
    
    public DssDomain()
    {
        super();
    }
    
}
