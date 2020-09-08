package com.huawei.sharedrive.app.core.domain;

import java.io.Serializable;

/**
 * 缩略图地址对象
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2014-5-6
 * @see
 * @since
 */
public class ThumbnailUrl implements Serializable
{
    private static final long serialVersionUID = -5876796331896360439L;

    private String url;
    
    public ThumbnailUrl()
    {
        
    }
    
    public ThumbnailUrl(String thumbnailUrl)
    {
        this.url = thumbnailUrl;
    }
    
    public String getThumbnailUrl()
    {
        return url;
    }
    
    public void setThumbnailUrl(String thumbnailUrl)
    {
        this.url = thumbnailUrl;
    }
    
}
