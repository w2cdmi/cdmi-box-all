/**
 * 
 */
package com.huawei.sharedrive.isystem.user.domain;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * @author q90003805
 * 
 */
public class User implements Serializable
{
    
    private static final long serialVersionUID = -2998544504532632934L;
    
    public static final byte USER_TYPE_ADMIN = -1;
    
    public static final byte USER_TYPE_USER = 0;
    
    public static final String STATUS_ENABLE = "enable";
    
    public static final String STATUS_DISABLE = "disable";
    
    public static final long ANONYMOUS_USER_ID = -1;
    
    private long id;
    
    private String objectSid;
    
    private byte type;
    
    @NotNull
    @Size(min = 6, max = 30)
    private String loginName;
    
    @JsonIgnore
    private String password;
    
    @NotNull
    @Size(min = 6, max = 60)
    private String name;
    
    private String department;
    
    @NotNull
    @Email
    private String email;
    
    private Date createdAt;
    
    private Date modifiedAt;
    
    private long spaceQuota;
    
    private long spaceUsed;
    
    private String status;
    
    private String domain;
    
    private int recycleDays;
    
    private int regionId;
    
    private String spaceUseSize;
    
    public long getId()
    {
        return id;
    }
    
    public void setId(long id)
    {
        this.id = id;
    }
    
    public String getObjectSid()
    {
        return objectSid;
    }
    
    public void setObjectSid(String objectSid)
    {
        this.objectSid = objectSid;
    }
    
    public byte getType()
    {
        return type;
    }
    
    public void setType(byte type)
    {
        this.type = type;
    }
    
    public String getLoginName()
    {
        return loginName;
    }
    
    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }
    
    public String getPassword()
    {
        return password;
    }
    
    @JsonIgnore
    public void setPassword(String password)
    {
        this.password = password;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getDepartment()
    {
        return department;
    }
    
    public void setDepartment(String department)
    {
        this.department = department;
    }
    
    public String getEmail()
    {
        return email;
    }
    
    public void setEmail(String email)
    {
        this.email = email;
    }
    
    public Date getCreatedAt()
    {
        return createdAt != null ? new Date(createdAt.getTime()) : null;
    }
    
    public void setCreatedAt(Date createdAt)
    {
        if(createdAt != null)
        {
            this.createdAt = new Date(createdAt.getTime());
        }
    }
    
    public Date getModifiedAt()
    {
        return modifiedAt != null ? new Date(modifiedAt.getTime()) : null;
    }
    
    public void setModifiedAt(Date modifiedAt)
    {
        if(modifiedAt != null)
        {
            this.modifiedAt = new Date(modifiedAt.getTime());
        }
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
    
    public String getDomain()
    {
        return domain;
    }
    
    public void setDomain(String domain)
    {
        this.domain = domain;
    }
    
    public long getSpaceQuota()
    {
        return spaceQuota;
    }
    
    public void setSpaceQuota(long spaceQuota)
    {
        this.spaceQuota = spaceQuota;
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
    
    @Override
    public String toString()
    {
        return objectSid;
    }
    
    public String getSpaceUseSize()
    {
        return spaceUseSize;
    }
    
    public void setSpaceUseSize(String spaceUseSize)
    {
        this.spaceUseSize = spaceUseSize;
    }
    
}
