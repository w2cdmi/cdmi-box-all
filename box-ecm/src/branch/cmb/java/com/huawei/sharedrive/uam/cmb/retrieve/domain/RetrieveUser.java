package com.huawei.sharedrive.uam.cmb.retrieve.domain;

import java.io.Serializable;

public class RetrieveUser implements Serializable
{
    private static final long serialVersionUID = -9130775931582601596L;
    
    private Long cloudUserId;
    
    private String description;
    
    private String email;
    
    private Long id;
    
    private String loginName;
    
    private String name;
    
    private String status;
    
    public Long getCloudUserId()
    {
        return cloudUserId;
    }
    
    public void setCloudUserId(Long cloudUserId)
    {
        this.cloudUserId = cloudUserId;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public String getEmail()
    {
        return email;
    }
    
    public void setEmail(String email)
    {
        this.email = email;
    }
    
    public Long getId()
    {
        return id;
    }
    
    public void setId(Long id)
    {
        this.id = id;
    }
    
    public String getLoginName()
    {
        return loginName;
    }
    
    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getStatus()
    {
        return status;
    }
    
    public void setStatus(String status)
    {
        this.status = status;
    }
    
}
