package com.huawei.sharedrive.isystem.dns.domain;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.huawei.sharedrive.isystem.util.Validate;

public class DnsDomain implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    @NotNull
    @Size(min = 0, max = 127)
    @Pattern(regexp = Validate.REG_IPV4)
    private String domainName;
    
    @NotNull
    private int dnsServerId;
    
    public String getDomainName()
    {
        return domainName;
    }
    
    public void setDomainName(String domainName)
    {
        this.domainName = domainName;
    }
    
    public int getDnsServerId()
    {
        return dnsServerId;
    }
    
    public void setDnsServerId(int dnsServerId)
    {
        this.dnsServerId = dnsServerId;
    }
    
}
