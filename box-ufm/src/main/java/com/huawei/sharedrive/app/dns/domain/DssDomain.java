package com.huawei.sharedrive.app.dns.domain;

import java.io.Serializable;

public class DssDomain implements Serializable
{
    
    /**
     * 
     */
    private static final long serialVersionUID = -1443103251101396318L;
    
    private String domain;
    
    private long dssId;
    
    public String getDomain()
    {
        return domain;
    }
    
    public void setDomain(String domain)
    {
        this.domain = domain;
    }
    
    public long getDssId()
    {
        return dssId;
    }
    
    public void setDssId(long dssId)
    {
        this.dssId = dssId;
    }
    
}
