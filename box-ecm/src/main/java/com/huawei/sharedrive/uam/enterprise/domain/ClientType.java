package com.huawei.sharedrive.uam.enterprise.domain;

import java.io.Serializable;

public class ClientType implements Serializable
{
    
    private static final long serialVersionUID = 1L;
    
    private long id;
    
    private String clientTypeName;
    
    public long getId()
    {
        return id;
    }
    
    public void setId(long id)
    {
        this.id = id;
    }
    
    public String getClientTypeName()
    {
        return clientTypeName;
    }
    
    public void setClientTypeName(String clientTypeName)
    {
        this.clientTypeName = clientTypeName;
    }
    
}
