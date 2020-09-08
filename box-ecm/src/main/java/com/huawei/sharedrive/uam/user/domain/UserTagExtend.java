package com.huawei.sharedrive.uam.user.domain;

import java.io.Serializable;

public class UserTagExtend implements Serializable
{
    private static final long serialVersionUID = -1532060342627434233L;
    
    private Long userId;
    
    private String tagId;
    
    private String tag;
    
    public String getTagId()
    {
        return tagId;
    }
    
    public void setTagId(String tagId)
    {
        this.tagId = tagId;
    }
    
    public Long getUserId()
    {
        return userId;
    }
    
    public void setUserId(Long userId)
    {
        this.userId = userId;
    }
    
    public String getTag()
    {
        return tag;
    }
    
    public void setTag(String tag)
    {
        this.tag = tag;
    }
    
}
