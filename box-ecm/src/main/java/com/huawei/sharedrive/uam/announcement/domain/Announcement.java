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
package com.huawei.sharedrive.uam.announcement.domain;

import java.io.Serializable;
import java.util.Date;

public class Announcement implements Serializable
{
    private static final long serialVersionUID = -1224887615058134652L;
    
    private long id;
    private String title;
    private String content;
    private long publisherId;
    private Date publishTime;
    private Date topTime;
    
    public long getId()
    {
        return id;
    }
    public void setId(long id)
    {
        this.id = id;
    }
    public String getTitle()
    {
        return title;
    }
    public void setTitle(String title)
    {
        this.title = title;
    }
    public String getContent()
    {
        return content;
    }
    public void setContent(String content)
    {
        this.content = content;
    }
    public long getPublisherId()
    {
        return publisherId;
    }
    public void setPublisherId(long publisherId)
    {
        this.publisherId = publisherId;
    }
    public Date getPublishTime()
    {
        if(null != this.publishTime)
        {
            return (Date)this.publishTime.clone();
        }
        return null;
    }
    public void setPublishTime(Date publishTime)
    {
        if(null != publishTime)
        {
            this.publishTime = (Date)publishTime.clone();
        }
        else
        {
            this.publishTime = null;
        }
    }
    public Date getTopTime()
    {
        if(null != this.topTime)
        {
            return (Date)this.topTime.clone();
        }
        return null;
    }
    public void setTopTime(Date topTime)
    {
        if(null != topTime)
        {
            this.topTime = (Date)topTime.clone();
        }
        else
        {
            this.topTime = null;
        }
    }
}
