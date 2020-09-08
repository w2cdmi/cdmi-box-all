package pw.cdmi.box.disk.client.domain.user;

public class RestUserCreateRequest
{
    private String appId;
    
    private Long cloudUserId;
    
    private String description;
    
    private String email;
    
    private Integer fileCount;
    
    private Long id;
    
    private String loginName;
    
    private Integer maxVersions;
    
    private String name;
    
    private Byte regionId;
    
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
    
    public Integer getMaxVersions()
    {
        return maxVersions;
    }
    
    public String getName()
    {
        return name;
    }
    
    public Byte getRegionId()
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
    
    public Integer getFileCount()
    {
        return fileCount;
    }
    
    public void setFileCount(Integer fileCount)
    {
        this.fileCount = fileCount;
    }
    
    public void setId(Long id)
    {
        this.id = id;
    }
    
    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }
    
    public void setMaxVersions(Integer maxVersions)
    {
        this.maxVersions = maxVersions;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setRegionId(Byte regionId)
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
