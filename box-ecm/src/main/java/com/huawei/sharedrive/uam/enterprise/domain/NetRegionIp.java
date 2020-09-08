package com.huawei.sharedrive.uam.enterprise.domain;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

public class NetRegionIp implements Serializable
{
    
    private static final long serialVersionUID = 1L;
    
    public static final String CACHE_KEY_PREFIX_ID = "net_region_ip_id_";
    
    public static final String STATUS_ENABLE_STR = "enable";
    
    public static final String STATUS_DISABLE_STR = "disable";
    
    public static final byte STATUS_ENABLE = 0;
    
    public static final byte STATUS_DISABLE = 1;
    
    private long id;
    
    private long netRegionId;
    
    private long accountId;
    
    @Size(max = 32)
    @NotBlank
    private String ipStart;
    
    @Size(max = 32)
    @NotBlank
    private String ipEnd;
    
    private long ipStartValue;
    
    private long ipEndValue;
    
    private int networkTypeId;
    
    private int regionId;
    
    private String regionName;
    
    private Long uploadBandWidth;
    
    private Long downloadBandWidth;
    
    private String location;
    
    private Date createdAt;
    
    private Date modifiedAt;
    
    private String netRegionName;
    
    private String netRegionDesc;
    
    private String errorCode;
    
    public long getId()
    {
        return id;
    }
    
    public void setId(long id)
    {
        this.id = id;
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
    
    public int getNetworkTypeId()
    {
        return networkTypeId;
    }
    
    public void setNetworkTypeId(int networkTypeId)
    {
        this.networkTypeId = networkTypeId;
    }
    
    public int getRegionId()
    {
        return regionId;
    }
    
    public void setRegionId(int regionId)
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
    
    public String getLocation()
    {
        return location;
    }
    
    public void setLocation(String location)
    {
        this.location = location;
    }
    
    public long getNetRegionId()
    {
        return netRegionId;
    }
    
    public void setNetRegionId(long netRegionId)
    {
        this.netRegionId = netRegionId;
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
    
    public String getNetRegionName()
    {
        return netRegionName;
    }
    
    public void setNetRegionName(String netRegionName)
    {
        this.netRegionName = netRegionName;
    }
    
    public String getNetRegionDesc()
    {
        return netRegionDesc;
    }
    
    public void setNetRegionDesc(String netRegionDesc)
    {
        this.netRegionDesc = netRegionDesc;
    }
    
    public String getErrorCode()
    {
        return errorCode;
    }
    
    public void setErrorCode(String errorCode)
    {
        this.errorCode = errorCode;
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
