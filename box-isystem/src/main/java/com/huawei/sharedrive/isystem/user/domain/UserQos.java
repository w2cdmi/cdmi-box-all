/*
 * Copyright Notice:
 *      Copyright  1998-2009, Huawei Technologies Co., Ltd.  ALL Rights Reserved.
 *
 *      Warning: This computer software sourcecode is protected by copyright law
 *      and international treaties. Unauthorized reproduction or distribution
 *      of this sourcecode, or any portion of it, may result in severe civil and
 *      criminal penalties, and will be prosecuted to the maximum extent
 *      possible under the law.
 */
package com.huawei.sharedrive.isystem.user.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import pw.cdmi.common.domain.SystemConfig;

/**
 * 
 * @author s90006125
 * 
 */
public class UserQos implements Serializable
{
    private static final long serialVersionUID = 2005354715875508084L;
    
    public static final String USER_QOS_CONFIG_PREFIX = "user.qos";
    
    private static final String USER_QOS_UPLOAD_TRAFFICE = "user.qos.upload.traffice";
    
    private static final String USER_QOS_DOWNLOAD_TRAFFICE = "user.qos.download.traffice";
    
    private long userId;
    
    /** 上传带宽 ，单位KB/S */
    @NotNull
    @Min(value = 0)
    @Digits(fraction = 0, integer = 10)
    private long uploadTraffic;
    
    /** 下载带宽，单位KB/S */
    @NotNull
    @Min(value = 0)
    @Digits(fraction = 0, integer = 10)
    private long downloadTraffic;
    
    /** 并发数 */
    private int concurrent;
    
    public UserQos()
    {
    }
    
    public UserQos(long userId, long uploadTraffic, long downloadTraffic, int concurrent)
    {
        this.userId = userId;
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
        list.add(new SystemConfig(null, USER_QOS_UPLOAD_TRAFFICE, String.valueOf(this.getUploadTraffic())));
        list.add(new SystemConfig(null, USER_QOS_DOWNLOAD_TRAFFICE, String.valueOf(this.getDownloadTraffic())));
        return list;
    }
    
    /**
     * @param itemList
     * @return
     */
    public static UserQos buildUserQos(List<SystemConfig> itemList)
    {
        Map<String, String> map = new HashMap<String, String>(16);
        for (SystemConfig configItem : itemList)
        {
            map.put(configItem.getId(), configItem.getValue());
        }
        UserQos userQos = new UserQos();
        userQos.setUploadTraffic(Long.parseLong(map.get(USER_QOS_UPLOAD_TRAFFICE)));
        userQos.setDownloadTraffic(Long.parseLong(map.get(USER_QOS_DOWNLOAD_TRAFFICE)));
        
        return userQos;
    }
}
