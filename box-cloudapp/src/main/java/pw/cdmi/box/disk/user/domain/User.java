package pw.cdmi.box.disk.user.domain;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class User implements Serializable
{
    public static final String SESSION_ID_KEY = "session.user.id";
    
    public static final String CACHE_KEY_PREFIX_ID = "cloudapp_userdao_user_id_";
    
    public static final long ANONYMOUS_USER_ID = -1;
    
    public static final String STATUS_DISABLE = "disable";
    
    public static final String STATUS_ENABLE = "enable";
    
    public static final byte TEAMSPACE_FLAG_SET = 0;
    
    public static final byte TEAMSPACE_FLAG_UNSET = 1;
    
    public static final int TEAMSPACE_NUM_UNLIMITED = -1;
    
    public static final byte USER_TYPE_ADMIN = -1;
    
    public static final byte USER_TYPE_USER = 0;
    
    public static final int VERSION_NUM_UNLIMITED = -1;
    
    private static final long serialVersionUID = -2998544504532632934L;
    
    private String appId;
    
    private Long cloudUserId;
    
    private Date createdAt;
    
    private String department;
    
    private String departmentCode;
    
    private String domain;
    
    @NotNull
    @Email
    private String email;
    
    private long fileCount;
    
    private long id;
    
    private String label;
    
    private Date lastLoginAt;
    
    @NotNull
    @Size(min = 6, max = 30)
    private String loginName;
    
    private int maxVersions;
    
    private Date modifiedAt;
    
    @NotNull
    @Size(min = 6, max = 60)
    private String name;
    
    private String objectSid;
    
    @JsonIgnore
    private String password;
    
    @JsonIgnore
    private String oldPassword;
    
    private int principalType = -1;
    
    private int recycleDays;
    
    private long enterpriseId;
    
    private long accountId;
    
    private int regionId;
    
    private int resourceType = -1;
    
    private long spaceQuota;
    
    private long spaceUsed;
    
    private String status;
    
    private int teamSpaceFlag = TEAMSPACE_FLAG_SET;
    
    private int teamSpaceMaxNum = TEAMSPACE_NUM_UNLIMITED;
    
    private byte type;
    
    private String validateKey;
    
    private String mobile;
    
    public String getAppId()
    {
        return appId;
    }
    
    public Long getCloudUserId()
    {
        return cloudUserId;
    }
    
    public Date getCreatedAt()
    {
        if (createdAt == null)
        {
            return null;
        }
        return (Date) createdAt.clone();
    }
    
    public String getDepartment()
    {
        return department;
    }
    
    public String getDepartmentCode()
    {
        return departmentCode;
    }
    
    public String getDomain()
    {
        return domain;
    }
    
    public String getEmail()
    {
        return email;
    }
    
    public long getFileCount()
    {
        return fileCount;
    }
    
    public long getId()
    {
        return id;
    }
    
    public String getLabel()
    {
        return label;
    }
    
    public Date getLastLoginAt()
    {
        if (lastLoginAt == null)
        {
            return null;
        }
        return (Date) lastLoginAt.clone();
    }
    
    public String getLoginName()
    {
        return loginName;
    }
    
    public int getMaxVersions()
    {
        return maxVersions;
    }
    
    public Date getModifiedAt()
    {
        if (modifiedAt == null)
        {
            return null;
        }
        return (Date) modifiedAt.clone();
    }
    
    public String getName()
    {
        return name;
    }
    
    public String getObjectSid()
    {
        return objectSid;
    }
    
    public String getPassword()
    {
        return password;
    }
    
    public int getPrincipalType()
    {
        return principalType;
    }
    
    public int getRecycleDays()
    {
        return recycleDays;
    }
    
    public int getRegionId()
    {
        return regionId;
    }
    
    public int getResourceType()
    {
        return resourceType;
    }
    
    public long getSpaceQuota()
    {
        return spaceQuota;
    }
    
    public long getSpaceUsed()
    {
        return spaceUsed;
    }
    
    public String getStatus()
    {
        return status;
    }
    
    public int getTeamSpaceFlag()
    {
        return teamSpaceFlag;
    }
    
    public int getTeamSpaceMaxNum()
    {
        return teamSpaceMaxNum;
    }
    
    public byte getType()
    {
        return type;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
    public void setCloudUserId(Long cloudUserId)
    {
        this.cloudUserId = cloudUserId;
    }
    
    public void setCreatedAt(Date createdAt)
    {
        if (createdAt == null)
        {
            this.createdAt = null;
        }
        else
        {
            this.createdAt = (Date) createdAt.clone();
        }
    }
    
    public void setDepartment(String department)
    {
        this.department = department;
    }
    
    public void setDepartmentCode(String departmentCode)
    {
        this.departmentCode = departmentCode;
    }
    
    public void setDomain(String domain)
    {
        this.domain = domain;
    }
    
    public void setEmail(String email)
    {
        this.email = email;
    }
    
    public void setFileCount(long fileCount)
    {
        this.fileCount = fileCount;
    }
    
    public void setId(long id)
    {
        this.id = id;
    }
    
    public void setLabel(String label)
    {
        this.label = label;
    }
    
    public void setLastLoginAt(Date lastLoginAt)
    {
        if (lastLoginAt == null)
        {
            this.lastLoginAt = null;
        }
        else
        {
            this.lastLoginAt = (Date) lastLoginAt.clone();
        }
    }
    
    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }
    
    public void setMaxVersions(int maxVersions)
    {
        this.maxVersions = maxVersions;
    }
    
    public void setModifiedAt(Date modifiedAt)
    {
        if (modifiedAt == null)
        {
            this.modifiedAt = null;
        }
        else
        {
            this.modifiedAt = (Date) modifiedAt.clone();
        }
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setObjectSid(String objectSid)
    {
        this.objectSid = objectSid;
    }
    
    public void setPassword(String password)
    {
        this.password = password;
    }
    
    public void setPrincipalType(int principalType)
    {
        this.principalType = principalType;
    }
    
    public void setRecycleDays(int recycleDays)
    {
        this.recycleDays = recycleDays;
    }
    
    public void setRegionId(int regionId)
    {
        this.regionId = regionId;
    }
    
    public void setResourceType(int resourceType)
    {
        this.resourceType = resourceType;
    }
    
    public void setSpaceQuota(long spaceQuota)
    {
        this.spaceQuota = spaceQuota;
    }
    
    public void setSpaceUsed(long spaceUsed)
    {
        this.spaceUsed = spaceUsed;
    }
    
    public void setStatus(String status)
    {
        this.status = status;
    }
    
    public void setTeamSpaceFlag(int teamSpaceFlag)
    {
        this.teamSpaceFlag = teamSpaceFlag;
    }
    
    public void setTeamSpaceMaxNum(int teamSpaceMaxNum)
    {
        this.teamSpaceMaxNum = teamSpaceMaxNum;
    }
    
    public void setType(byte type)
    {
        this.type = type;
    }
    
    @Override
    public String toString()
    {
        return objectSid;
    }
    
    public String getValidateKey()
    {
        return validateKey;
    }
    
    public void setValidateKey(String validateKey)
    {
        this.validateKey = validateKey;
    }
    
    public long getEnterpriseId()
    {
        return enterpriseId;
    }
    
    public void setEnterpriseId(long enterpriseId)
    {
        this.enterpriseId = enterpriseId;
    }
    
    public long getAccountId()
    {
        return accountId;
    }
    
    public void setAccountId(long accountId)
    {
        this.accountId = accountId;
    }
    
    public String getOldPassword()
    {
        return oldPassword;
    }
    
    public void setOldPassword(String oldPassword)
    {
        this.oldPassword = oldPassword;
    }

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
}
