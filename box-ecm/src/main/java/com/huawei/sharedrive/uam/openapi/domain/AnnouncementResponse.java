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
package com.huawei.sharedrive.uam.openapi.domain;

import java.io.Serializable;
import java.util.Date;

import com.huawei.sharedrive.uam.announcement.domain.Announcement;

/**
 * 
 *
 */
public class AnnouncementResponse implements Serializable
{
    private static final long serialVersionUID = -1224887615058134652L;
    
    private long id;
    private String title;
    private String content;
    private Date createdAt;
    
    public AnnouncementResponse()
    {
    }
    
    public AnnouncementResponse(Announcement announcement)
    {
        this.id = announcement.getId();
        this.title = announcement.getTitle();
        this.content = announcement.getContent();
        if(null != announcement.getPublishTime())
        {
            this.createdAt = (Date)(announcement.getPublishTime().clone());
        }
    }
    
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
    public Date getCreatedAt()
    {
        if(null != this.createdAt)
        {
            return (Date)this.createdAt.clone();
        }
        return null;
    }
    public void setCreatedAt(Date createdAt)
    {
        if(null != createdAt)
        {
            this.createdAt = (Date)createdAt.clone();
        }
        else
        {
            this.createdAt = null;
        }
    }
}
