package com.huawei.sharedrive.uam.enterprise.domain;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

public class SafeLevel implements Serializable
{
    
    private static final long serialVersionUID = 1L;
    
    public static final String CACHE_KEY_PREFIX_ID = "safe_level_id_";
    
    public static final String STATUS_ENABLE_STR = "enable";
    
    public static final String STATUS_DISABLE_STR = "disable";
    
    public static final byte STATUS_ENABLE = 0;
    
    public static final byte STATUS_DISABLE = 1;
    
    private Long id;
    
    public Long getId()
    {
        return id;
    }
    
    public void setId(Long id)
    {
        this.id = id;
    }
    
    private long accountId;
    
    @Size(max = 128)
    @NotBlank
    private String safeLevelName;
    
    @Size(max = 255)
    private String safeLevelDesc;
    
    private Date createdAt;
    
    private Date modifiedAt;
    
    public Date getCreatedAt()
    {
        if (null != createdAt)
        {
            return (Date) createdAt.clone();
        }
        return null;
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
    
    public Date getModifiedAt()
    {
        if (null != modifiedAt)
        {
            return (Date) modifiedAt.clone();
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
    
    public String getSafeLevelName()
    {
        return safeLevelName;
    }
    
    public void setSafeLevelName(String safeLevelName)
    {
        this.safeLevelName = safeLevelName;
    }
    
    public String getSafeLevelDesc()
    {
        return safeLevelDesc;
    }
    
    public void setSafeLevelDesc(String safeLevelDesc)
    {
        this.safeLevelDesc = safeLevelDesc;
    }
    
    public long getAccountId()
    {
        return accountId;
    }
    
    public void setAccountId(long accountId)
    {
        this.accountId = accountId;
    }
    
}
