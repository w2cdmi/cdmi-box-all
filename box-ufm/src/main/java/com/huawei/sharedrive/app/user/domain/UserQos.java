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
package com.huawei.sharedrive.app.user.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.huawei.sharedrive.app.utils.Constants;

import pw.cdmi.common.domain.SystemConfig;

/**
 * 
 * @author s90006125
 * 
 */
public class UserQos implements Serializable
{
    public static final String USER_QOS_CONFIG_PREFIX = "user.qos";
    
    private static final long serialVersionUID = 2005354715875508084L;
    
    private static final String USER_QOS_CONCURRENT = "user.qos.concurrent";
    
    private static final String USER_QOS_DOWNLOAD_TRAFFICE = "user.qos.download.traffice";
    
    private static final String USER_QOS_UPLOAD_TRAFFICE = "user.qos.upload.traffice";
    
    /** 并发数 */
    private int concurrent;
    
    /** 下载带宽，单位KB/S */
    private long downloadTraffic;
    
    /** 上传带宽 ，单位KB/S */
    private long uploadTraffic;
    
    private long userId;
    
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
    
    /**
     * @param itemList
     * @return
     */
    public static UserQos buildUserQos(List<SystemConfig> itemList)
    {
        Map<String, String> map = new HashMap<String, String>(8);
        for (SystemConfig configItem : itemList)
        {
            map.put(configItem.getId(), configItem.getValue());
        }
        UserQos userQos = new UserQos();
        userQos.setUploadTraffic(Long.parseLong(map.get(USER_QOS_UPLOAD_TRAFFICE)));
        userQos.setDownloadTraffic(Long.parseLong(map.get(USER_QOS_DOWNLOAD_TRAFFICE)));
        userQos.setConcurrent(Integer.parseInt(map.get(USER_QOS_CONCURRENT)));
        
        return userQos;
    }
    
    public int getConcurrent()
    {
        return concurrent;
    }
    
    public long getDownloadTraffic()
    {
        return downloadTraffic;
    }
    
    public long getUploadTraffic()
    {
        return uploadTraffic;
    }
    
    public long getUserId()
    {
        return userId;
    }
    
    public void setConcurrent(int concurrent)
    {
        this.concurrent = concurrent;
    }
    
    public void setDownloadTraffic(long downloadTraffic)
    {
        this.downloadTraffic = downloadTraffic;
    }
    
    public void setUploadTraffic(long uploadTraffic)
    {
        this.uploadTraffic = uploadTraffic;
    }
    
    public void setUserId(long userId)
    {
        this.userId = userId;
    }
    
    public List<SystemConfig> toConfigItem()
    {
        List<SystemConfig> list = new ArrayList<SystemConfig>(8);
        list.add(new SystemConfig(Constants.UFM_DEFAULT_APP_ID, USER_QOS_UPLOAD_TRAFFICE,
            String.valueOf(this.getUploadTraffic())));
        list.add(new SystemConfig(Constants.UFM_DEFAULT_APP_ID, USER_QOS_DOWNLOAD_TRAFFICE,
            String.valueOf(this.getDownloadTraffic())));
        list.add(new SystemConfig(Constants.UFM_DEFAULT_APP_ID, USER_QOS_CONCURRENT,
            String.valueOf(this.getConcurrent())));
        return list;
    }
}
