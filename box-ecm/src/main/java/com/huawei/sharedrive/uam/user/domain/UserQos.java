package com.huawei.sharedrive.uam.user.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import pw.cdmi.common.domain.BasicConfig;
import pw.cdmi.common.domain.SystemConfig;

public class UserQos extends BasicConfig implements Serializable
{
    private static final long serialVersionUID = 2005354715875508084L;
    
    public static final String USER_QOS_CONFIG_PREFIX = "user.qos";
    
    private static final String USER_QOS_UPLOAD_TRAFFICE = "user.qos.upload.traffice";
    
    private static final String USER_QOS_DOWNLOAD_TRAFFICE = "user.qos.download.traffice";
    
    private long userId;
    
    @NotNull
    @Min(value = 0)
    @Digits(fraction = 0, integer = 10)
    private long uploadTraffic;
    
    @NotNull
    @Min(value = 0)
    @Digits(fraction = 0, integer = 10)
    private long downloadTraffic;
    
    private int concurrent;
    
    public UserQos()
    {
    }
    
    public UserQos(long uploadTraffic, long downloadTraffic, int concurrent)
    {
        this.uploadTraffic = uploadTraffic;
        this.downloadTraffic = downloadTraffic;
        this.concurrent = concurrent;
    }
    
    public long getUserId()
    {
        return userId;
    }
    
    public void setUserId(long userId)
    {
        this.userId = userId;
    }
    
    public long getUploadTraffic()
    {
        return uploadTraffic;
    }
    
    public void setUploadTraffic(long uploadTraffic)
    {
        this.uploadTraffic = uploadTraffic;
    }
    
    public long getDownloadTraffic()
    {
        return downloadTraffic;
    }
    
    public void setDownloadTraffic(long downloadTraffic)
    {
        this.downloadTraffic = downloadTraffic;
    }
    
    public int getConcurrent()
    {
        return concurrent;
    }
    
    public void setConcurrent(int concurrent)
    {
        this.concurrent = concurrent;
    }
    
    public List<SystemConfig> toConfigItem()
    {
        List<SystemConfig> list = new ArrayList<SystemConfig>(2);
        list.add(new SystemConfig(getAppId(), USER_QOS_UPLOAD_TRAFFICE,
            String.valueOf(this.getUploadTraffic())));
        list.add(new SystemConfig(getAppId(), USER_QOS_DOWNLOAD_TRAFFICE,
            String.valueOf(this.getDownloadTraffic())));
        return list;
    }
}
