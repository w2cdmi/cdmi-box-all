package pw.cdmi.box.disk.httpclient.rest.request;

public class CreateUserEntity
{
    private long id;
    
    private String loginName;
    
    private String name;
    
    private String email;
    
    private long spaceQuota;
    
    private long regionId;
    
    private String status;
    
    public long getId()
    {
        return id;
    }
    
    public void setId(long id)
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
    
    public String getEmail()
    {
        return email;
    }
    
    public void setEmail(String email)
    {
        this.email = email;
    }
    
    public long getSpaceQuota()
    {
        return spaceQuota;
    }
    
    public void setSpaceQuota(long spaceQuota)
    {
        this.spaceQuota = spaceQuota;
    }
    
    public long getRegionId()
    {
        return regionId;
    }
    
    public void setRegionId(long regionId)
    {
        this.regionId = regionId;
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
