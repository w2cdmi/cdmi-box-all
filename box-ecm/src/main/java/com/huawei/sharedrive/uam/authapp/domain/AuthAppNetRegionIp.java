package com.huawei.sharedrive.uam.authapp.domain;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

public class AuthAppNetRegionIp implements Serializable
{
    
    private static final long serialVersionUID = 1L;
    
    private Long id;
    
    private String authAppId;
    
    @Size(max = 32)
    @NotBlank
    private String ipStart;
    
    @Size(max = 32)
    @NotBlank
    private String ipEnd;
    
    private long ipStartValue;
    
    private long ipEndValue;
    
    private Integer regionId;
    
    private String regionName;
    
    private Long uploadBandWidth;
    
    private Long downloadBandWidth;
    
    private Date createdAt;
    
    private Date modifiedAt;
    
    public AuthAppNetRegionIp()
    {
    }
    
    public AuthAppNetRegionIp(long id)
    {
        this.id = id;
    }
    
    public Long getId()
    {
        return id;
    }
    
    public void setId(Long id)
    {
        this.id = id;
    }
    
    public String getAuthAppId()
    {
        return authAppId;
    }
    
    public void setAuthAppId(String authAppId)
    {
        this.authAppId = authAppId;
    }
    
    public String getIpStart()
    {
        return ipStart;
    }
    
    public void setIpStart(String ipStart)
    {
        this.ipStart = ipStart;
    }
    
    public String getIpEnd()
    {
        return ipEnd;
    }
    
    public void setIpEnd(String ipEnd)
    {
        this.ipEnd = ipEnd;
    }
    
    public long getIpStartValue()
    {
        return ipStartValue;
    }
    
    public void setIpStartValue(long ipStartValue)
    {
        this.ipStartValue = ipStartValue;
    }
    
    public long getIpEndValue()
    {
        return ipEndValue;
    }
    
    public void setIpEndValue(long ipEndValue)
    {
        this.ipEndValue = ipEndValue;
    }
    
    public Integer getRegionId()
    {
        return regionId;
    }
    
    public void setRegionId(Integer regionId)
    {
        this.regionId = regionId;
    }
    
    public String getRegionName()
    {
        return regionName;
    }
    
    public void setRegionName(String regionName)
    {
        this.regionName = regionName;
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
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((authAppId == null) ? 0 : authAppId.hashCode());
        result = prime * result + ((ipEnd == null) ? 0 : ipEnd.hashCode());
        result = prime * result + ((ipStart == null) ? 0 : ipStart.hashCode());
        result = prime * result + ((regionId == null) ? 0 : regionId.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        AuthAppNetRegionIp other = (AuthAppNetRegionIp) obj;
        if (isAppIdNotEqual(other))
        {
            return false;
        }
        
        if (isIpEndNotEqual(other))
        {
            return false;
        }
        
        if (isIpStartNotEqual(other))
        {
            return false;
        }
        
        if (isRegionIdNotEqual(other))
        {
            return false;
        }
        
        return true;
    }

    private boolean isAppIdNotEqual(AuthAppNetRegionIp other)
    {
        if (authAppId == null)
        {
            if (other.authAppId != null)
            {
                return true;
            }
            
        }
        else if (!authAppId.equals(other.authAppId))
        {
            return true;
        }
        return false;
    }
    
    private boolean isIpEndNotEqual(AuthAppNetRegionIp other)
    {
        if (ipEnd == null)
        {
            if (other.ipEnd != null)
            {
                return true;
            }
            
        }
        else if (!ipEnd.equals(other.ipEnd))
        {
            return true;
        }
        return false;
    }
    
    private boolean isIpStartNotEqual(AuthAppNetRegionIp other)
    {
        if (ipStart == null)
        {
            if (other.ipStart != null)
            {
                return true;
            }
            
        }
        else if (!ipStart.equals(other.ipStart))
        {
            return true;
        }
        return false;
    }
    
    private boolean isRegionIdNotEqual(AuthAppNetRegionIp other)
    {
        if (regionId == null)
        {
            if (other.regionId != null)
            {
                return true;
            }
            
        }
        else if (!regionId.equals(other.regionId))
        {
            return true;
        }
        return false;
    }
    
    @Override
    public String toString()
    {
        return "AuthAppNetRegionIp [id=" + id + ", authAppId=" + authAppId + ", ipStart=" + ipStart
            + ", ipEnd=" + ipEnd + ", regionId=" + regionId + ", regionName=" + regionName + "]";
    }
}
