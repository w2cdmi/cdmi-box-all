package com.huawei.sharedrive.uam.security.domain;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;

import pw.cdmi.core.utils.IpUtils;

public class NetworkRegion implements Serializable
{
    
    private static final long serialVersionUID = 1L;
    
    private long id;
    
    @NotNull
    private String ipStart;
    
    @NotNull
    private String ipEnd;
    
    private long ipStartValue;
    
    private long ipEndValue;
    
    @NotNull
    private int networkType;
    
    @NotNull
    private String location;
    
    private int regionId;
    
    private String regionName;
    
    private Long uploadBandWidth = 0L;
    
    private Long downloadBandWidth = 0L;
    
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
    
    public NetworkRegion()
    {
        this.ipStartValue = this.getStart();
        this.ipEndValue = this.getEnd();
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
    
    public long getId()
    {
        return id;
    }
    
    public void setId(long id)
    {
        this.id = id;
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
    
    public long getStart()
    {
        return IpUtils.toLong(this.ipStart);
    }
    
    public long getEnd()
    {
        return IpUtils.toLong(this.ipEnd);
    }
    
    public int getNetworkType()
    {
        return networkType;
    }
    
    public void setNetworkType(int networkType)
    {
        this.networkType = networkType;
    }
    
    public String getLocation()
    {
        return location;
    }
    
    public void setLocation(String location)
    {
        this.location = location;
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
    
    public boolean validate()
    {
        if (StringUtils.isBlank(this.getIpStart()) || StringUtils.isBlank(this.ipEnd))
        {
            return false;
        }
        if (!checkIp(this.getIpStart()) || !checkIp(this.getIpEnd()))
        {
            return false;
        }
        long start = this.getStart();
        if (start < 0)
        {
            return false;
        }
        long end = this.getEnd();
        if (end < 0)
        {
            return false;
        }
        if (end < start)
        {
            return false;
        }
        return true;
    }
    
    public boolean checkIp(String str)
    {
        Pattern pattern = Pattern.compile("\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
    
}
