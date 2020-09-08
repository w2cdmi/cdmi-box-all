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
package pw.cdmi.box.uam.announcement.domain;

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
        return publishTime == null ? null : (Date) publishTime.clone();
    }
    
    public void setPublishTime(Date publishTime)
    {
        this.publishTime = (publishTime == null ? null : (Date) publishTime.clone());
    }
    
    public Date getTopTime()
    {
        return topTime == null ? null : (Date) topTime.clone();
    }
    
    public void setTopTime(Date topTime)
    {
        this.topTime = (topTime == null ? null : (Date) topTime.clone());
    }
}
