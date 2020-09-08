package pw.cdmi.box.disk.share.domain;

public class ADSearchUser
{
    public static final String STATUS_NO_SYSTEM = "nonesystemuser";
    
    private String appId;
    
    private Long cloudUserId;
    
    private String description;
    
    private String email;
    
    private Long id;
    
    private String loginName;
    
    private String name;
    
    private String objectSid;
    
    private Integer regionId;
    
    private Long spaceQuota;
    
    private Long spaceUsed;
    
    private String status;
    
    public String getAppId()
    {
        return appId;
    }
    
    public Long getCloudUserId()
    {
        return cloudUserId;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public String getEmail()
    {
        return email;
    }
    
    public Long getId()
    {
        return id;
    }
    
    public String getLoginName()
    {
        return loginName;
    }
    
    public String getName()
    {
        return name;
    }
    
    public String getObjectSid()
    {
        return objectSid;
    }
    
    public Integer getRegionId()
    {
        return regionId;
    }
    
    public Long getSpaceQuota()
    {
        return spaceQuota;
    }
    
    public Long getSpaceUsed()
    {
        return spaceUsed;
    }
    
    public String getStatus()
    {
        return status;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
    public void setCloudUserId(Long cloudUserId)
    {
        this.cloudUserId = cloudUserId;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public void setEmail(String email)
    {
        this.email = email;
    }
    
    public void setId(Long id)
    {
        this.id = id;
    }
    
    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setObjectSid(String objectSid)
    {
        this.objectSid = objectSid;
    }
    
    public void setRegionId(Integer regionId)
    {
        this.regionId = regionId;
    }
    
    public void setSpaceQuota(Long spaceQuota)
    {
        this.spaceQuota = spaceQuota;
    }
    
    public void setSpaceUsed(Long spaceUsed)
    {
        this.spaceUsed = spaceUsed;
    }
    
    public void setStatus(String status)
    {
        this.status = status;
    }
    
}
