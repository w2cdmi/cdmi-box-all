package com.huawei.sharedrive.uam.accountuser.domain;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UserAccount implements Serializable
{
    
    public static final int INT_STATUS_DISABLE = 1;
    
    public static final int INT_STATUS_ENABLE = 0;
    
    public static final int STATUS_NONE_FIRST_LOGIN = 1;
    
    public static final int PARAMETER_UNDEFINED = -2;
    
    public static final long SPACE_QUOTA_UNLIMITED = -1;
    
    public final static int SPACE_UNIT = 1024;
    
    public static final String STATUS_DISABLE = "disable";
    
    public static final String STATUS_ENABLE = "enable";
    
    private static final long serialVersionUID = 6015509540123813284L;
    
    @Size(max = 128)
    private String accessKeyId;
    
    @NotNull
    @Size(max = 20)
    private long accountId;
    
    private String alias;
    
    @NotNull
    @Size(max = 20)
    private long cloudUserId;

    private String imAccount;

    private Date createdAt;
    
    private String description;
    
    private Long downloadBandWidth;
    
    private String email;
    
    @NotNull
    @Size(max = 20)
    private long enterpriseId;
    
    private Long fileCount;
    
    private long id;
    
    private Date lastLoginAt;
    
    private Integer maxVersions;
    
    private Date modifiedAt;
    
    private String name;
    
    @NotNull
    @Size(max = 20)
    private long regionId;
    
    private Long resourceType;
    
    private Integer roleId;
    
    @Size(max = 128)
    private String secretKey;
    
    private Long spaceQuota;
    
    private Long spaceUsed;
    
    @NotNull
    @Size(max = 1)
    private Integer status;
    
    private String tableSuffix;
    
    private Integer teamSpaceFlag;
    
    private Integer teamSpaceMaxNum;
    
    private Long teamSpaceQuota;
    
    private Long uploadBandWidth;
    
    
    /** 支持多版本文件的大小 */
    private Long versionFileSize = -1L;
    
    
    /** 支持多版本文件的类型 */
    private String versionFileType;
    
   

	@NotNull
    @Size(max = 20)
    private long userId;
    
    private Integer firstLogin;
    
    public UserAccount()
    {
    }
    
    public UserAccount(long userId, long enterpriseId, long accountId)
    {
        this.userId = userId;
        this.enterpriseId = enterpriseId;
        this.accountId = accountId;
        this.status = UserAccount.INT_STATUS_ENABLE;
    }
    
    public UserAccount(long userId, long enterpriseId, long accountId, long cloudUserId)
    {
        this.userId = userId;
        this.enterpriseId = enterpriseId;
        this.accountId = accountId;
        this.cloudUserId = cloudUserId;
        this.status = UserAccount.INT_STATUS_ENABLE;
    }
    
    public String getAccessKeyId()
    {
        return accessKeyId;
    }
    
    public long getAccountId()
    {
        return accountId;
    }
    
    public String getAlias()
    {
        return alias;
    }
    
    public long getCloudUserId()
    {
        return cloudUserId;
    }
    
    public Date getCreatedAt()
    {
        if (null != createdAt)
        {
            return (Date) createdAt.clone();
        }
        return null;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public Long getDownloadBandWidth()
    {
        return downloadBandWidth;
    }
    
    public String getEmail()
    {
        return email;
    }
    
    public long getEnterpriseId()
    {
        return enterpriseId;
    }
    
    public Long getFileCount()
    {
        return fileCount;
    }
    
    public long getId()
    {
        return id;
    }
    
    public Date getLastLoginAt()
    {
        if (null != lastLoginAt)
        {
            return (Date) lastLoginAt.clone();
        }
        return null;
    }
    
    public Integer getMaxVersions()
    {
        return maxVersions;
    }
    
    public Date getModifiedAt()
    {
        if (null != modifiedAt)
        {
            return (Date) modifiedAt.clone();
        }
        return null;
    }
    
    public String getName()
    {
        return name;
    }
    
    public long getRegionId()
    {
        return regionId;
    }
    
    public Long getResourceType()
    {
        return resourceType;
    }
    
    public Integer getRoleId()
    {
        return roleId;
    }
    
    public String getSecretKey()
    {
        return secretKey;
    }
    
    public Long getSpaceQuota()
    {
        return spaceQuota;
    }
    
    public Long getSpaceUsed()
    {
        return spaceUsed;
    }
    
    public Integer getStatus()
    {
        return status;
    }
    
    public String getTableSuffix()
    {
        return tableSuffix;
    }
    
    public Integer getTeamSpaceFlag()
    {
        return teamSpaceFlag;
    }
    
    public Integer getTeamSpaceMaxNum()
    {
        return teamSpaceMaxNum;
    }
    
    public Long getTeamSpaceQuota()
    {
        return teamSpaceQuota;
    }
    
    public Long getUploadBandWidth()
    {
        return uploadBandWidth;
    }
    
    public long getUserId()
    {
        return userId;
    }
    
    public void setAccessKeyId(String accessKeyId)
    {
        this.accessKeyId = accessKeyId;
    }
    
    public void setAccountId(long accountId)
    {
        this.accountId = accountId;
    }
    
    public void setAlias(String alias)
    {
        this.alias = alias;
    }
    
    public void setCloudUserId(long cloudUserId)
    {
        this.cloudUserId = cloudUserId;
    }
    
    public void setCreatedAt(Date createdAt)
    {
        if (null != createdAt)
        {
            this.createdAt = (Date) createdAt.clone();
        }
        else
        {
            this.createdAt = null;
        }
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public void setDownloadBandWidth(Long downloadBandWidth)
    {
        this.downloadBandWidth = downloadBandWidth;
    }
    
    public void setEmail(String email)
    {
        this.email = email;
    }
    
    public void setEnterpriseId(long enterpriseId)
    {
        this.enterpriseId = enterpriseId;
    }
    
    public void setFileCount(Long fileCount)
    {
        this.fileCount = fileCount;
    }
    
    public void setId(long id)
    {
        this.id = id;
    }
    
    public void setLastLoginAt(Date lastLoginAt)
    {
        if (null != lastLoginAt)
        {
            this.lastLoginAt = (Date) lastLoginAt.clone();
        }
        else
        {
            this.lastLoginAt = null;
        }
    }
    
    public void setMaxVersions(Integer maxVersions)
    {
        this.maxVersions = maxVersions;
    }
    
    public void setModifiedAt(Date modifiedAt)
    {
        if (null != modifiedAt)
        {
            this.modifiedAt = (Date) modifiedAt.clone();
        }
        else
        {
            this.modifiedAt = null;
        }
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setRegionId(long regionId)
    {
        this.regionId = regionId;
    }
    
    public void setResourceType(Long resourceType)
    {
        this.resourceType = resourceType;
    }
    
    public void setRoleId(Integer roleId)
    {
        this.roleId = roleId;
    }
    
    public void setSecretKey(String secretKey)
    {
        this.secretKey = secretKey;
    }
    
    public void setSpaceQuota(Long spaceQuota)
    {
        this.spaceQuota = spaceQuota;
    }
    
    public void setSpaceUsed(Long spaceUsed)
    {
        this.spaceUsed = spaceUsed;
    }
    
    public void setStatus(Integer status)
    {
        this.status = status;
    }
    
    public void setTableSuffix(String tableSuffix)
    {
        this.tableSuffix = tableSuffix;
    }
    
    public void setTeamSpaceFlag(Integer teamSpaceFlag)
    {
        this.teamSpaceFlag = teamSpaceFlag;
    }
    
    public void setTeamSpaceMaxNum(Integer teamSpaceMaxNum)
    {
        this.teamSpaceMaxNum = teamSpaceMaxNum;
    }
    
    public void setTeamSpaceQuota(Long teamSpaceQuota)
    {
        this.teamSpaceQuota = teamSpaceQuota;
    }
    
    public void setUploadBandWidth(Long uploadBandWidth)
    {
        this.uploadBandWidth = uploadBandWidth;
    }
    
    public void setUserId(long userId)
    {
        this.userId = userId;
    }
    
    public Integer getFirstLogin()
    {
        return firstLogin;
    }
    
    public void setFirstLogin(Integer firstLogin)
    {
        this.firstLogin = firstLogin;
    }
    public Long getVersionFileSize() {
		return versionFileSize;
	}

	public void setVersionFileSize(Long versionFileSize) {
		this.versionFileSize = versionFileSize;
	}

	public String getVersionFileType() {
		return versionFileType;
	}

	public void setVersionFileType(String versionFileType) {
		this.versionFileType = versionFileType;
	}

    public String getImAccount() {
        return imAccount;
    }

    public void setImAccount(String imAccount) {
        this.imAccount = imAccount;
    }
}
