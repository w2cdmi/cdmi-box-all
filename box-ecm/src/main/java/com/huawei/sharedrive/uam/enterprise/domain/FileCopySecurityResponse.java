package com.huawei.sharedrive.uam.enterprise.domain;

import java.io.Serializable;
import java.util.Date;

public class FileCopySecurityResponse implements Serializable
{
    private static final long serialVersionUID = -3188807358516328392L;
    
    private long accountId;
    
    private Date createdAt;
    
    private long id;
    
    private Date modifiedAt;
    
    private long srcSafeRoleId;
    
    private String srcSafeRoleName;
    
    private long targetSafeRoleId;
    
    private String targetSafeRoleName;
    
    public long getAccountId()
    {
        return accountId;
    }
    
    public Date getCreatedAt()
    {
        if (null != createdAt)
        {
            return (Date) createdAt.clone();
        }
        return null;
    }
    
    public long getId()
    {
        return id;
    }
    
    public Date getModifiedAt()
    {
        if (null != modifiedAt)
        {
            return (Date) modifiedAt.clone();
        }
        return null;
    }
    
    public long getSrcSafeRoleId()
    {
        return srcSafeRoleId;
    }
    
    public String getSrcSafeRoleName()
    {
        return srcSafeRoleName;
    }
    
    public long getTargetSafeRoleId()
    {
        return targetSafeRoleId;
    }
    
    public String getTargetSafeRoleName()
    {
        return targetSafeRoleName;
    }
    
    public void setAccountId(long accountId)
    {
        this.accountId = accountId;
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
    
    public void setId(long id)
    {
        this.id = id;
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
    
    public void setSrcSafeRoleId(long srcSafeRoleId)
    {
        this.srcSafeRoleId = srcSafeRoleId;
    }
    
    public void setSrcSafeRoleName(String srcSafeRoleName)
    {
        this.srcSafeRoleName = srcSafeRoleName;
    }
    
    public void setTargetSafeRoleId(long targetSafeRoleId)
    {
        this.targetSafeRoleId = targetSafeRoleId;
    }
    
    public void setTargetSafeRoleName(String targetSafeRoleName)
    {
        this.targetSafeRoleName = targetSafeRoleName;
    }
    
}
