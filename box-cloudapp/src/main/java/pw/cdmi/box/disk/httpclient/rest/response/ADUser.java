package pw.cdmi.box.disk.httpclient.rest.response;

import java.io.Serializable;

public class ADUser implements Serializable
{
    
    private static final long serialVersionUID = 1410308126140492511L;
    
    public static final long ANONYMOUS_USER_ID = -1;
    
    public static final String STATUS_DISABLE = "disable";
    
    public static final String STATUS_ENABLE = "enable";
    
    public static final byte USER_TYPE_ADMIN = -1;
    
    public static final byte USER_TYPE_USER = 0;
    
    private String department;
    
    private long modifiedAt;
    
    private String domain;
    
    private String email;
    
    private long id;
    
    private String loginName;
    
    private String name;
    
    private String objectSid;
    
    private int recycleDays;
    
    private int regionId;
    
    private long spaceQuota;
    
    private long spaceUsed;
    
    private String status;
    
    private byte type;
    
    public String getDepartment()
    {
        return department;
    }
    
    public void setDepartment(String department)
    {
        this.department = department;
    }
    
    public long getModifiedAt()
    {
        return modifiedAt;
    }
    
    public void setModifiedAt(long modifiedAt)
    {
        this.modifiedAt = modifiedAt;
    }
    
    public String getDomain()
    {
        return domain;
    }
    
    public void setDomain(String domain)
    {
        this.domain = domain;
    }
    
    public String getEmail()
    {
        return email;
    }
    
    public void setEmail(String email)
    {
        this.email = email;
    }
    
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
    
    public String getObjectSid()
    {
        return objectSid;
    }
    
    public void setObjectSid(String objectSid)
    {
        this.objectSid = objectSid;
    }
    
    public int getRecycleDays()
    {
        return recycleDays;
    }
    
    public void setRecycleDays(int recycleDays)
    {
        this.recycleDays = recycleDays;
    }
    
    public int getRegionId()
    {
        return regionId;
    }
    
    public void setRegionId(int regionId)
    {
        this.regionId = regionId;
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
    
    public byte getType()
    {
        return type;
    }
    
    public void setType(byte type)
    {
        this.type = type;
    }
    
    @Override
    public String toString()
    {
        return "ADUser [department=" + department + ", modifiedAt=" + modifiedAt + ", domain=" + domain
            + ", email=" + email + ", id=" + id + ", loginName=" + loginName + ", name=" + name
            + ", objectSid=" + objectSid + ", recycleDays=" + recycleDays + ", regionId=" + regionId
            + ", spaceQuota=" + spaceQuota + ", spaceUsed=" + spaceUsed + ", status=" + status + ", type="
            + type + ']';
    }
    
}
