package com.huawei.sharedrive.app.openapi.domain.user;


/**
 * 
 * @author c00110381
 * 
 */
public class RestUserInfo
{
    protected String description;
    
    protected String email;
    
    protected String loginName;
    
    protected String name;
    
    protected String password;
    
    protected int recycleDays;
    
    protected String region;
    
    protected long spaceQuota;
    
    protected long spaceUsed;
    
    protected String status;

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

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public int getRecycleDays()
    {
        return recycleDays;
    }

    public void setRecycleDays(int recycleDays)
    {
        this.recycleDays = recycleDays;
    }

    public String getRegion()
    {
        return region;
    }

    public void setRegion(String region)
    {
        this.region = region;
    }

    public long getSpaceQuota()
    {
        return spaceQuota;
    }

    public void setSpaceQuota(long spaceQuota)
    {
        this.spaceQuota = spaceQuota;
    }

    public long getSpaceUsed()
    {
        return spaceUsed;
    }

    public void setSpaceUsed(long spaceUsed)
    {
        this.spaceUsed = spaceUsed;
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
