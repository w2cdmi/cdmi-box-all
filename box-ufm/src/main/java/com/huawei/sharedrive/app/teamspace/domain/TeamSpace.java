package com.huawei.sharedrive.app.teamspace.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 团队空间
 * 
 * @author c00110381
 * 
 */
public class TeamSpace implements Serializable
{
    private static final long serialVersionUID = -3440696797373727384L;
    
    public static final String CACHE_KEY_PREFIX_ID = "teamspace_id_";
    
    /** 团队空间上传文件发送消息 */
    public static final byte UPLOAD_NOTICE_ENABLE = 0;
    
    /** 团队空间上传文件不发送消息 */
    public static final byte UPLOAD_NOTICE_DISABLE = 1;
    
    public static final int STATUS_ENABLE = 0;
    
    public static final int STATUS_DISABLE = 1;
    
    public static final int MEMBER_NUM_UNLIMITED = -1;
    
    public static final int VERSION_NUM_UNLIMITED = -1;
    
    public static final long SPACE_QUOTA_UNLIMITED = -1L;

    /*个人空间*/
    public static final int TYPE_PERSONAL = 0;

    /*部门空间*/
    public static final int TYPE_OFFICIAL = 1;

    /*工作组*/
    public static final int TYPE_WORK_GROUP = 2;

    /*兴趣群*/
    public static final int TYPE_INTEREST_COMMUNITY = 3;

    /*档案库*/
    public static final int TYPE_ARCHIVE_STORE = 4;
    
    /*收件箱*/
    public static final int TYPE_RECEIVE_FOLDER = 5;

    private long cloudUserId;
    
    private String name;

    private int type;

    private String description;
    
    private Date createdAt;
    
    private Date modifiedAt;
    
    private long ownerBy;
    
    private long createdBy;
    
    private long modifiedBy;
    
    private int status;
    
    private long curNumbers;
    
    // 单位：MB
    private long spaceQuota = SPACE_QUOTA_UNLIMITED;
    
    // 单位：MB
    private long spaceUsed = 0;
    
    private int maxMembers = MEMBER_NUM_UNLIMITED;
    
    private int maxVersions = VERSION_NUM_UNLIMITED;
    
    private String createdByUserName;
    
    private String ownerByUserName;
    
    private String appId;
    
    private long accountId;
    
    // 团队空间新增文件是否发送消息
    private byte uploadNotice;
    
    private int regionId;
    
    public static int getStatusAbnormal()
    {
        return STATUS_DISABLE;
    }
    
    public static int getStatusNormal()
    {
        return STATUS_ENABLE;
    }
    
    public long getAccountId()
    {
        return accountId;
    }
    
    public String getAppId()
    {
        return appId;
    }
    
    public long getCloudUserId()
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
    
    public long getCreatedBy()
    {
        return createdBy;
    }
    
    
    public String getCreatedByUserName()
    {
        return createdByUserName;
    }
    
    public long getCurNumbers()
    {
        return curNumbers;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public int getMaxMembers()
    {
        return maxMembers;
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
    
    public long getModifiedBy()
    {
        return modifiedBy;
    }
    
    public String getName()
    {
        return name;
    }
    
    public long getOwnerBy()
    {
        return ownerBy;
    }
    
    public String getOwnerByUserName()
    {
        return ownerByUserName;
    }
    
    public long getSpaceQuota()
    {
        return spaceQuota;
    }
    
    public long getSpaceUsed()
    {
        return spaceUsed;
    }
    
    public int getStatus()
    {
        return status;
    }
    
    public byte getUploadNotice()
    {
        return uploadNotice;
    }
    
    public void setAccountId(long accountId)
    {
        this.accountId = accountId;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
    public void setCloudUserId(long cloudUserId)
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
    
    public void setCreatedBy(long createdBy)
    {
        this.createdBy = createdBy;
    }
    
    public void setCreatedByUserName(String createdByUserName)
    {
        this.createdByUserName = createdByUserName;
    }
    
    public void setCurNumbers(long curNumbers)
    {
        this.curNumbers = curNumbers;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public void setMaxMembers(int maxMembers)
    {
        this.maxMembers = maxMembers;
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
    
    public void setModifiedBy(long modifiedBy)
    {
        this.modifiedBy = modifiedBy;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setOwnerBy(long ownerBy)
    {
        this.ownerBy = ownerBy;
    }
    
    public void setOwnerByUserName(String ownerByUserName)
    {
        this.ownerByUserName = ownerByUserName;
    }
    
    public void setSpaceQuota(long spaceQuota)
    {
        this.spaceQuota = spaceQuota;
    }
    
    public void setSpaceUsed(long spaceUsed)
    {
        this.spaceUsed = spaceUsed;
    }
    
    public void setStatus(int status)
    {
        this.status = status;
    }
    
    public void setUploadNotice(byte uploadNotice)
    {
        this.uploadNotice = uploadNotice;
    }

    public int getRegionId()
    {
        return regionId;
    }

    public void setRegionId(int regionId)
    {
        this.regionId = regionId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
