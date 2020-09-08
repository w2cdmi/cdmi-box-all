package com.huawei.sharedrive.app.openapi.domain.share;

import java.io.Serializable;

public class LinkIdentityInfo implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = -1748971512312017318L;
    
    private String identity;
    
    public LinkIdentityInfo()
    {
        
    }
    
    public LinkIdentityInfo(String identity)
    {
        this.identity = identity;
    }
    
    public String getIdentity()
    {
        return identity;
    }
    
    public void setIdentity(String identity)
    {
        this.identity = identity;
    }
    
}
