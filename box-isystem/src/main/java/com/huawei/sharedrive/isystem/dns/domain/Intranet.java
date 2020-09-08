package com.huawei.sharedrive.isystem.dns.domain;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.huawei.sharedrive.isystem.util.Validate;

public class Intranet implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    @NotNull
    @Size(min = 1, max = 45)
    @Pattern(regexp=Validate.REG_IPV4)
    private String netAddress;
    
    @NotNull
    @Size(min = 1, max = 45)
    @Pattern(regexp=Validate.REG_MAK)
    private String netMask;
    
    public String getNetAddress()
    {
        return netAddress;
    }
    
    public void setNetAddress(String netAddress)
    {
        this.netAddress = netAddress;
    }
    
    public String getNetMask()
    {
        return netMask;
    }
    
    public void setNetMask(String netMask)
    {
        this.netMask = netMask;
    }
    
}
