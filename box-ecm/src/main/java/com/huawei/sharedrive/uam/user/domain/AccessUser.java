package com.huawei.sharedrive.uam.user.domain;

public class AccessUser
{
    
    private String linkCode;
    
    private User user;
    
    public AccessUser()
    {
        
    }
    
    public AccessUser(String linkCode)
    {
        this.linkCode = linkCode;
    }
    
    public AccessUser(User user)
    {
        this.user = user;
    }
    
    public String getLinkCode()
    {
        return linkCode;
    }
    
    public User getUser()
    {
        return user;
    }
    
    public void setLinkCode(String linkCode)
    {
        this.linkCode = linkCode;
    }
    
    public void setUser(User user)
    {
        this.user = user;
    }
}
