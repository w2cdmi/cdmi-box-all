package com.huawei.sharedrive.uam.user.domain;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UserTag implements Serializable
{
    
    private static final long serialVersionUID = 2489940966971909438L;
    
    @NotNull
    @Size(min = 1, max = 20)
    private Long userId;
    
    @NotNull
    @Size(min = 1, max = 36)
    private String tagId;
    
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
    
}
