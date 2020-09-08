package com.huawei.sharedrive.uam.enterpriseuseraccount.domain;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.huawei.sharedrive.uam.accountuser.domain.UserAccount;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.user.domain.User;

public class EnterpriseUserAccount implements Serializable
{
    
    private static final long serialVersionUID = 737708534667010455L;
    
    public static final byte ROLE_ENTERPRISE_MANAGER = 1;
    
    public static final byte ROLE_ENTERPRISE_MEMBER = 2;
    
    private long id;
    
    @Size(max = 255)
    private String password;
    
    @NotNull
    private long enterpriseId;
    
    @NotNull
    private byte role = 2;
    
    private Integer roleId;
    
    @Size(max = 255)
    private String objectSid;
    
    @NotNull
    @Size(max = 127)
    private String name;
    
    @Size(max = 255)
    private String alias;
    
    @Size(max = 255)
    private String description;
    
    @NotNull
    @Size(min = 5, max = 255)
    @Pattern(regexp = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$")
    private String email;
    
    @Size(max = 20)
    private String mobile;
    
    private Date createdAt;
    
    private Date modifiedAt;
    
    private Date lastLoginAt;
    
    private byte status = 1;
    
    private byte ldapStatus = 1;
    
    private long userSource = 0;
    
    @Size(max = 128)
    private String validateKey;
    
    private int iterations = 1000;
    
    @Size(max = 255)
    private String salt;
    
    private String tableSuffix;
    
    @NotNull
    @Size(max = 20)
    private long userId;
    
    @NotNull
    @Size(max = 20)
    private long regionId;
    
    @NotNull
    @Size(max = 20)
    private long accountId;
    
    @NotNull
    @Size(max = 20)
    private long cloudUserId;
    
    @NotNull
    @Size(max = 1)
    private int accountStatus;
    
    @Size(max = 128)
    private String accessKeyId;
    
    @Size(max = 128)
    private String secretKey;
    
    private Integer maxVersions;
    
    private Long spaceQuota;
    
    private Integer teamSpaceFlag;
    
    private Integer teamSpaceMaxNum;
    
    private Long teamSpaceQuota;
    
    private Long uploadBandWidth;
    
    private Long downloadBandWidth;
    
    private Long spaceUsed;
    
    private Long fileCount;
    
    private String domain;
    
    private String appId;
    
    private long departmentId;
    
    /** 支持多版本文件的大小 */
    @NotNull
    @Min(value = -1)
    @Max(value = 999999)
    private Long versionFileSize;
    
    
    /** 支持多版本文件的类型 */
    private String versionFileType;
    
    private byte type;
    
  //员工安全等级
  	private byte staffLevel;
    
    public byte getStaffLevel() {
		return staffLevel;
	}

	public void setStaffLevel(byte staffLevel) {
		this.staffLevel = staffLevel;
	}

	public long getId()
    {
        return id;
    }
    
    public void setId(long id)
    {
        this.id = id;
    }
    
    public void setPassword(String password)
    {
        this.password = password;
    }
    
    public String getPassword()
    {
        return password;
    }
    
    public void setEnterpriseId(long enterpriseId)
    {
        this.enterpriseId = enterpriseId;
    }
    
    public long getEnterpriseId()
    {
        return enterpriseId;
    }
    
    public void setRole(Byte role)
    {
        this.role = role;
    }
    
    public Byte getRole()
    {
        return role;
    }
    
    public void setObjectSid(String objectSid)
    {
        this.objectSid = objectSid;
    }
    
    public String getObjectSid()
    {
        return objectSid;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setAlias(String alias)
    {
        this.alias = alias;
    }
    
    public String getAlias()
    {
        return alias;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public void setEmail(String email)
    {
        this.email = email;
    }
    
    public String getEmail()
    {
        return email;
    }
    
    public void setMobile(String mobile)
    {
        this.mobile = mobile;
    }
    
    public String getMobile()
    {
        return mobile;
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
    
    public Date getCreatedAt()
    {
        if (null != createdAt)
        {
            return (Date) createdAt.clone();
        }
        return null;
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
    
    public Date getModifiedAt()
    {
        if (null != modifiedAt)
        {
            return (Date) modifiedAt.clone();
        }
        return null;
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
    
    public Date getLastLoginAt()
    {
        if (null != lastLoginAt)
        {
            return (Date) lastLoginAt.clone();
        }
        return null;
    }
    
    public void setStatus(byte status)
    {
        this.status = status;
    }
    
    public byte getStatus()
    {
        return status;
    }
    
    public void setLdapStatus(byte ldapStatus)
    {
        this.ldapStatus = ldapStatus;
    }
    
    public byte getLdapStatus()
    {
        return ldapStatus;
    }
    
    public void setUserSource(long userSource)
    {
        this.userSource = userSource;
    }
    
    public long getUserSource()
    {
        return userSource;
    }
    
    public void setValidateKey(String validateKey)
    {
        this.validateKey = validateKey;
    }
    
    public String getValidateKey()
    {
        return validateKey;
    }
    
    public void setIterations(int iterations)
    {
        this.iterations = iterations;
    }
    
    public int getIterations()
    {
        return iterations;
    }
    
    public void setSalt(String salt)
    {
        this.salt = salt;
    }
    
    public String getSalt()
    {
        return salt;
    }
    
    public String getTableSuffix()
    {
        return tableSuffix;
    }
    
    public void setTableSuffix(String tableSuffix)
    {
        this.tableSuffix = tableSuffix;
    }
    
    public long getUserId()
    {
        return userId;
    }
    
    public void setUserId(long userId)
    {
        this.userId = userId;
    }
    
    public long getRegionId()
    {
        return regionId;
    }
    
    public void setRegionId(long regionId)
    {
        this.regionId = regionId;
    }
    
    public long getAccountId()
    {
        return accountId;
    }
    
    public void setAccountId(long accountId)
    {
        this.accountId = accountId;
    }
    
    public long getCloudUserId()
    {
        return cloudUserId;
    }
    
    public void setCloudUserId(long cloudUserId)
    {
        this.cloudUserId = cloudUserId;
    }
    
    public int getAccountStatus()
    {
        return accountStatus;
    }
    
    public void setAccountStatus(int accountStatus)
    {
        this.accountStatus = accountStatus;
    }
    
    public String getAccessKeyId()
    {
        return accessKeyId;
    }
    
    public void setAccessKeyId(String accessKeyId)
    {
        this.accessKeyId = accessKeyId;
    }
    
    public String getSecretKey()
    {
        return secretKey;
    }
    
    public void setSecretKey(String secretKey)
    {
        this.secretKey = secretKey;
    }
    
    public void setRole(byte role)
    {
        this.role = role;
    }
    
    public Integer getMaxVersions()
    {
        return maxVersions;
    }
    
    public void setMaxVersions(Integer maxVersions)
    {
        this.maxVersions = maxVersions;
    }
    
    public Long getSpaceQuota()
    {
        return spaceQuota;
    }
    
    public void setSpaceQuota(Long spaceQuota)
    {
        this.spaceQuota = spaceQuota;
    }
    
    public Integer getTeamSpaceFlag()
    {
        return teamSpaceFlag;
    }
    
    public void setTeamSpaceFlag(Integer teamSpaceFlag)
    {
        this.teamSpaceFlag = teamSpaceFlag;
    }
    
    public Integer getTeamSpaceMaxNum()
    {
        return teamSpaceMaxNum;
    }
    
    public void setTeamSpaceMaxNum(Integer teamSpaceMaxNum)
    {
        this.teamSpaceMaxNum = teamSpaceMaxNum;
    }
    
    public Long getTeamSpaceQuota()
    {
        return teamSpaceQuota;
    }
    
    public void setTeamSpaceQuota(Long teamSpaceQuota)
    {
        this.teamSpaceQuota = teamSpaceQuota;
    }
    
    public Long getUploadBandWidth()
    {
        return uploadBandWidth;
    }
    
    public void setUploadBandWidth(Long uploadBandWidth)
    {
        this.uploadBandWidth = uploadBandWidth;
    }
    
    public Long getDownloadBandWidth()
    {
        return downloadBandWidth;
    }
    
    public void setDownloadBandWidth(Long downloadBandWidth)
    {
        this.downloadBandWidth = downloadBandWidth;
    }
    
    public Long getSpaceUsed()
    {
        return spaceUsed;
    }
    
    public void setSpaceUsed(Long spaceUsed)
    {
        this.spaceUsed = spaceUsed;
    }
    
    public Long getFileCount()
    {
        return fileCount;
    }
    
    public void setFileCount(Long fileCount)
    {
        this.fileCount = fileCount;
    }
    
    public Integer getRoleId()
    {
        return roleId;
    }
    
    public void setRoleId(Integer roleId)
    {
        this.roleId = roleId;
    }
    
    public String getDomain()
    {
        return domain;
    }
    
    public void setDomain(String domain)
    {
        this.domain = domain;
    }
    
    public String getAppId()
    {
        return appId;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
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
    
    public static User copyEnterpriseUser(EnterpriseUserAccount enterpriseUser)
    {
        User user = new User();
        if (null != enterpriseUser)
        {
            user.setId(enterpriseUser.getId());
            user.setPassword(enterpriseUser.getPassword());
            user.setEnterpriseId(enterpriseUser.getEnterpriseId());
            user.setRole(enterpriseUser.getRole());
            user.setObjectSid(enterpriseUser.getObjectSid());
            user.setName(enterpriseUser.getAlias());
            user.setLoginName(enterpriseUser.getName());
            user.setDepartment(enterpriseUser.getDescription());
            user.setEmail(enterpriseUser.getEmail());
            user.setModifiedAt(enterpriseUser.getModifiedAt());
            user.setCreatedAt(enterpriseUser.getCreatedAt());
            user.setStatus(String.valueOf(enterpriseUser.getStatus()));
            user.setLdapStatus(enterpriseUser.getLdapStatus());
            user.setUserSource(enterpriseUser.getUserSource());
            user.setSalt(enterpriseUser.getSalt());
            user.setIterations(enterpriseUser.getIterations());
        }
        return user;
    }
    
    public static void copyEnterpriseUser(EnterpriseUserAccount enterpriseUserAccount,
        EnterpriseUser enterpriseUser)
    {
        enterpriseUserAccount.setId(enterpriseUser.getId());
        enterpriseUserAccount.setUserId(enterpriseUser.getId());
        enterpriseUserAccount.setPassword(enterpriseUser.getPassword());
        enterpriseUserAccount.setEnterpriseId(enterpriseUser.getEnterpriseId());
        enterpriseUserAccount.setObjectSid(enterpriseUser.getObjectSid());
        enterpriseUserAccount.setAlias(enterpriseUser.getAlias());
        enterpriseUserAccount.setName(enterpriseUser.getName());
        enterpriseUserAccount.setDescription(enterpriseUser.getDescription());
        enterpriseUserAccount.setEmail(enterpriseUser.getEmail());
        enterpriseUserAccount.setModifiedAt(enterpriseUser.getModifiedAt());
        enterpriseUserAccount.setCreatedAt(enterpriseUser.getCreatedAt());
        enterpriseUserAccount.setStatus(enterpriseUser.getStatus());
        enterpriseUserAccount.setLdapStatus(enterpriseUser.getLdapStatus());
        enterpriseUserAccount.setUserSource(enterpriseUser.getUserSource());
        enterpriseUserAccount.setSalt(enterpriseUser.getSalt());
        enterpriseUserAccount.setIterations(enterpriseUser.getIterations());
        enterpriseUserAccount.setMobile(enterpriseUser.getMobile());
        enterpriseUserAccount.setType(enterpriseUser.getType());
        enterpriseUserAccount.setStaffLevel(enterpriseUser.getStaffSecretLevel());
    }
    
    public static void copyAccountUser(EnterpriseUserAccount enterpriseUserAccount, UserAccount userAccount)
    {
        enterpriseUserAccount.setRegionId(userAccount.getRegionId());
        enterpriseUserAccount.setAccountId(userAccount.getAccountId());
        enterpriseUserAccount.setCloudUserId(userAccount.getCloudUserId());
        enterpriseUserAccount.setAccountStatus(userAccount.getStatus());
        enterpriseUserAccount.setAccessKeyId(userAccount.getAccessKeyId());
        enterpriseUserAccount.setSecretKey(userAccount.getSecretKey());
        enterpriseUserAccount.setMaxVersions(userAccount.getMaxVersions());
        enterpriseUserAccount.setSpaceQuota(userAccount.getSpaceQuota());
        enterpriseUserAccount.setTeamSpaceFlag(userAccount.getTeamSpaceFlag());
        enterpriseUserAccount.setTeamSpaceMaxNum(userAccount.getTeamSpaceMaxNum());
        enterpriseUserAccount.setTeamSpaceQuota(userAccount.getTeamSpaceQuota());
        enterpriseUserAccount.setUploadBandWidth(userAccount.getUploadBandWidth());
        enterpriseUserAccount.setDownloadBandWidth(userAccount.getDownloadBandWidth());
        enterpriseUserAccount.setSpaceUsed(userAccount.getSpaceUsed());
        enterpriseUserAccount.setFileCount(userAccount.getFileCount());
        enterpriseUserAccount.setRoleId(userAccount.getRoleId());
        enterpriseUserAccount.setVersionFileSize(userAccount.getVersionFileSize());
        enterpriseUserAccount.setVersionFileType(userAccount.getVersionFileType());
        if (userAccount.getStatus() == UserAccount.INT_STATUS_DISABLE)
        {
            enterpriseUserAccount.setStatus((byte) UserAccount.INT_STATUS_DISABLE);
        }
        else
        {
            enterpriseUserAccount.setStatus((byte) UserAccount.INT_STATUS_ENABLE);
        }
    }

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(long departmentId) {
		this.departmentId = departmentId;
	}

	
    
    
}
