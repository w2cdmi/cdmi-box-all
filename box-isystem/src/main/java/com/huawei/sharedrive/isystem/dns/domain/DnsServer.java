package com.huawei.sharedrive.isystem.dns.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.huawei.sharedrive.isystem.util.Validate;

public class DnsServer implements Serializable
{
    private static final long serialVersionUID = 7526621725973969012L;
    
    @NotNull
    private int id;
    
    @Size(min = 0, max = 45)
    @Pattern(regexp = Validate.REG_IPV4)
    private String manageIp;
    
    @Max(65535)
    @Min(1)
    private int managePort;
    
    private List<DssDomain> dssdomains = new ArrayList<DssDomain>(10);
    
    private boolean availAble = false;
    
    public DnsServer()
    {
    }
    
    public DnsServer(int id, String manageIp, int managePort)
    {
        this.id = id;
        this.manageIp = manageIp;
        this.managePort = managePort;
    }
    
    public int getId()
    {
        return id;
    }
    
    public void setId(int id)
    {
        this.id = id;
    }
    
    public String getManageIp()
    {
        return manageIp;
    }
    
    public void setManageIp(String manageIp)
    {
        this.manageIp = manageIp;
    }
    
    public int getManagePort()
    {
        return managePort;
    }
    
    public void setManagePort(int managePort)
    {
        this.managePort = managePort;
    }
    
    public List<DssDomain> getDssdomains()
    {
        return dssdomains;
    }
    
    public void setDssdomains(List<DssDomain> dssdomains)
    {
        this.dssdomains = dssdomains;
    }
    
    public boolean isAvailAble()
    {
        return availAble;
    }
    
    public void setAvailAble(boolean availAble)
    {
        this.availAble = availAble;
    }
    
}
