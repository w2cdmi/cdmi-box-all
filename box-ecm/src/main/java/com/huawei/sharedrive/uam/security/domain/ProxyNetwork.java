package com.huawei.sharedrive.uam.security.domain;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ProxyNetwork implements Serializable
{
    private static final long serialVersionUID = -700713045354101708L;
    
    private Long id;
    
    @NotNull
    @Size(min = 1, max = 16)
    private String accessIp;
    
    private NetworkType networkType;
    
    @Size(min = 0, max = 255)
    private String description;
    
    public Long getId()
    {
        return id;
    }
    
    public void setId(Long id)
    {
        this.id = id;
    }
    
    public ProxyNetwork(String accessIp, String description)
    {
        super();
        this.accessIp = accessIp;
        this.description = description;
    }
    
    public ProxyNetwork()
    {
        super();
    }
    
    public ProxyNetwork(String accessIp, NetworkType networkType, String description)
    {
        super();
        this.accessIp = accessIp;
        this.networkType = networkType;
        this.description = description;
    }
    
    public String getAccessIp()
    {
        return accessIp;
    }
    
    public void setAccessIp(String accessIp)
    {
        this.accessIp = accessIp;
    }
    
    public NetworkType getNetworkType()
    {
        return networkType;
    }
    
    public void setNetworkType(NetworkType networkType)
    {
        this.networkType = networkType;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
}
