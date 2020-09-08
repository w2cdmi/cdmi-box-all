/**
 * 
 */
package com.huawei.sharedrive.app.user.domain;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class User implements Serializable
{
    public static final long ANONYMOUS_USER_ID = -1;
    
    // 系统内部用户访问
    public static final long SYSTEM_USER_ID = -2;
    
    public static final long APP_GROUP_ID = -2;
    
    public static final long APP_ACCOUNT_GROUP_ID = -3;
    
    public static final long APP_USER_ID = -2;
    
    public static final String CACHE_KEY_PREFIX_ID = "user_id_";
    
    /** 空间配额: 无限制 */
    public static final long SPACE_MAX_QUOTA = 999999999999L;
    
    public static final int SPACE_QUOTA_UNLIMITED = -1;
    
    public static final String STATUS_DISABLE = "disable";
    
    public static final String STATUS_DISABLE_INTEGER = "1";
    
    public static final String STATUS_ENABLE = "enable";
    
    public static final String STATUS_ENABLE_INTEGER = "0";
    
    public static final byte STATUS_TEAMSPACE_INTEGER = 2;
    
    public static final String USER_DELETING = "2";
    
    public static final byte USER_TYPE_ADMIN = -1;
    
    public static final byte USER_TYPE_USER = 0;
    
    public static final byte USER_TYPE_TEAMSPACE = 2;
    
    /** 文件版本数: 无限制 */
    public static final int VERSION_NUM_UNLIMITED = -1;
    
    public static final int SECURITY_ID_UNSET = 0;
    
    private static final long serialVersionUID = -2998544504532632934L;
    
    public static final int USER_DEPARTMENT = 255;
    
    public static final int LOGIN_NAME_LENGTH = 127;
    
    public static final int USER_NAME_LENGTH = 127;
    
    private String appId;
    
    private Long accountId;
    
    private Date createdAt;
    
//    private String department;
    
    private String domain;
    
    @Email
    private String email;
    
    // 用户文件数, 包括文件、文件夹、文件版本及回收站中的数据
    private long fileCount;
    
    private long id;
    
    private String label;
    
    /**
     * web最后登录时间
     */
    private Date lastLoginAt;
    
    // 最后一次执行实时统计的时间
    private Date lastStatisticsTime;
    
    @NotNull
    @Size(max = 255)
    private String loginName;
    
    // 文件最大版本数
    private int maxVersions = VERSION_NUM_UNLIMITED;
    
    private Date modifiedAt;
    
    @NotNull
    @Size(max = 255)
    private String name;
    
    private String objectSid;
    
    @JsonIgnore
    private String password;
    
    private int recycleDays;
    
    private int regionId;
    
    private long spaceQuota;
    
    private long spaceUsed;
    
    private String status;
    
    private byte type;
    
    private int securityId;
    
    /** 支持多版本文件的大小 */
    private Long versionFileSize = -1L;
    
    
    /** 支持多版本文件的类型 */
    private String versionFileType;
    
    @JsonIgnore
    private int tableSuffix;
    
    public User()
    {
        
    }
    
    public User(long accountId, String appId, String label)
    {
        this.setAccountId(accountId);
        this.setAppId(appId);
        this.setLabel(label);
    }
    
    
    
    public String getAppId()
    {
        return appId;
    }
    
    public Date getCreatedAt()
    {
        return createdAt != null ? (Date) createdAt.clone() : null;
    }
    
//    public String getDepartment()
//    {
//        return department;
//    }
    
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
        return lastLoginAt != null ? (Date) lastLoginAt.clone() : null;
    }
    
    public Date getLastStatisticsTime()
    {
        return lastStatisticsTime != null ? (Date) lastStatisticsTime.clone() : null;
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
        return modifiedAt != null ? (Date) modifiedAt.clone() : null;
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
    
    public int getRecycleDays()
    {
        return recycleDays;
    }
    
    public int getRegionId()
    {
        return regionId;
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
    
    public byte getType()
    {
        return type;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
    public Long getAccountId()
    {
        return accountId;
    }
    
    public void setAccountId(Long accountId)
    {
        this.accountId = accountId;
    }
    
    public void setCreatedAt(Date createdAt)
    {
        this.createdAt = createdAt != null ? (Date) createdAt.clone() : null;
    }
    
//    public void setDepartment(String department)
//    {
//        this.department = department;
//    }
    
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
        this.lastLoginAt = lastLoginAt != null ? (Date) lastLoginAt.clone() : null;
    }
    
    public void setLastStatisticsTime(Date lastStatisticsTime)
    {
        this.lastStatisticsTime = lastStatisticsTime != null ? (Date) lastStatisticsTime.clone() : null;
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
        this.modifiedAt = modifiedAt != null ? (Date) modifiedAt.clone() : null;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setObjectSid(String objectSid)
    {
        this.objectSid = objectSid;
    }
    
    @JsonIgnore
    public void setPassword(String password)
    {
        this.password = password;
    }
    
    public void setRecycleDays(int recycleDays)
    {
        this.recycleDays = recycleDays;
    }
    
    public void setRegionId(int regionId)
    {
        this.regionId = regionId;
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
    
    public void setType(byte type)
    {
        this.type = type;
    }
    
    @Override
    public String toString()
    {
        return objectSid;
    }
    
    public int getTableSuffix()
    {
        return tableSuffix;
    }
    
    public void setTableSuffix(int tableSuffix)
    {
        this.tableSuffix = tableSuffix;
    }
    
    public int getSecurityId()
    {
        return securityId;
    }
    
    public void setSecurityId(int securityId)
    {
        this.securityId = securityId;
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
    
    
    
}
