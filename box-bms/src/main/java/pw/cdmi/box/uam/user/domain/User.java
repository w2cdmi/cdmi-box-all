package pw.cdmi.box.uam.user.domain;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class User implements Serializable
{
    public static final long ANONYMOUS_USER_ID = -1;
    
    public static final int PARAMETER_UNDEFINED = -2;
    
    public static final String CACHE_KEY_PREFIX_ID = "uam_userdao_user_id_";
    
    public static final String CACHE_KEY_CLOUD_PREFIX_ID = "cloudapp_userdao_user_id_";
    
    private static final long serialVersionUID = -2998544504532632934L;
    
    public static final String STATUS_DISABLE = "disable";
    
    public static final String STATUS_ENABLE = "enable";
    
    public static final String STATUS_ALL = "all";
    
    public static final byte TEAMSPACE_FLAG_SET = 1;
    
    public static final byte TEAMSPACE_FLAG_UNSET = 0;
    
    public static final int TEAMSPACE_NUM_UNLIMITED = -1;
    
    public static final byte USER_TYPE_ADMIN = -1;
    
    public static final byte USER_TYPE_USER = 0;
    
    public static final int REGION_ID = 0;
    
    public static final int DEFAULT_FILE_COUNT = 0;
    
    public static final long DEFAULT_SPACE_USED = 0L;
    
    public static final int VERSION_NUM_UNLIMITED = -1;
    
    public static final int REGION_ID_UNDEFINED = -1;
    
    public static final long SPACE_QUOTA_UNLIMITED = -1;
    
    public final static int SPACE_UNIT = 1024;
    
    public final static int LOGINNAME_LENGTH = 127;
    
    public final static int APPID_LENGTH = 64;
    
    public final static long SPACE_QUOTA_MAX_G = 999999;
    
    @NotNull
    private long enterpriseId;
    
    private long accountId;
    
    private byte ldapStatus = 1;
    
    private long userSource = 0;
    
    @NotNull
    private byte role = 2;
    
    private String appId;
    
    private Long cloudUserId;
    
    private Date createdAt;
    
    private String department;
    
    private String departmentCode;
    
    private String domain;
    
    @NotNull
    @Size(min = 5, max = 255)
    @Pattern(regexp = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$")
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
    private String oldPasswd;
    
    @JsonIgnore
    private String password;
    
    private int principalType = -1;
    
    private int recycleDays;
    
    private int regionId;
    
    private int resourceType = -1;
    
    private long spaceQuota;
    
    private long spaceUsed;
    
    private long spaceCount;
    
    private String description;
    
    private String status;
    
    private byte teamSpaceFlag = TEAMSPACE_FLAG_SET;
    
    private int teamSpaceMaxNum = TEAMSPACE_NUM_UNLIMITED;
    
    private int iterations;
    
    private String salt;
    
    private Long uploadBandWidth;
    
    private Long downloadBandWidth;
    
    private byte type;
    
    private String validateKey;
    
    private Integer roleId;
    
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
        return createdAt == null ? null : (Date) createdAt.clone();
    }
    
    public void setCreatedAt(Date createdAt)
    {
        this.createdAt = (createdAt == null ? null : (Date) createdAt.clone());
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
        return lastLoginAt == null ? null : (Date) lastLoginAt.clone();
    }
    
    public void setLastLoginAt(Date lastLoginAt)
    {
        this.lastLoginAt = (lastLoginAt == null ? null : (Date) lastLoginAt.clone());
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
        return modifiedAt == null ? null : (Date) modifiedAt.clone();
    }
    
    public void setModifiedAt(Date modifiedAt)
    {
        this.modifiedAt = (modifiedAt == null ? null : (Date) modifiedAt.clone());
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
    
    public byte getTeamSpaceFlag()
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
    
    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }
    
    public void setMaxVersions(int maxVersions)
    {
        this.maxVersions = maxVersions;
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
    
    public void setTeamSpaceFlag(byte teamSpaceFlag)
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
    
    public String getValidateKey()
    {
        return validateKey;
    }
    
    public void setValidateKey(String validateKey)
    {
        this.validateKey = validateKey;
    }
    
    public String getOldPasswd()
    {
        return oldPasswd;
    }
    
    public void setOldPasswd(String oldPasswd)
    {
        this.oldPasswd = oldPasswd;
    }
    
    public int getIterations()
    {
        return iterations;
    }
    
    public void setIterations(int iterations)
    {
        this.iterations = iterations;
    }
    
    public String getSalt()
    {
        return salt;
    }
    
    public void setSalt(String salt)
    {
        this.salt = salt;
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
    
    public long getSpaceCount()
    {
        return spaceCount;
    }
    
    public void setSpaceCount(long spaceCount)
    {
        this.spaceCount = spaceCount;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public long getEnterpriseId()
    {
        return enterpriseId;
    }
    
    public void setEnterpriseId(long enterpriseId)
    {
        this.enterpriseId = enterpriseId;
    }
    
    public byte getLdapStatus()
    {
        return ldapStatus;
    }
    
    public void setLdapStatus(byte ldapStatus)
    {
        this.ldapStatus = ldapStatus;
    }
    
    public long getUserSource()
    {
        return userSource;
    }
    
    public void setUserSource(long userSource)
    {
        this.userSource = userSource;
    }
    
    public byte getRole()
    {
        return role;
    }
    
    public void setRole(byte role)
    {
        this.role = role;
    }
    
    public long getAccountId()
    {
        return accountId;
    }
    
    public void setAccountId(long accountId)
    {
        this.accountId = accountId;
    }
    
    public Integer getRoleId()
    {
        return roleId;
    }
    
    public void setRoleId(Integer roleId)
    {
        this.roleId = roleId;
    }
}
