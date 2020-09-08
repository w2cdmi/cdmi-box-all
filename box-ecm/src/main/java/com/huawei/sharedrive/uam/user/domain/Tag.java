package com.huawei.sharedrive.uam.user.domain;

import java.io.Serializable;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

@SuppressWarnings("PMD.AvoidFieldNameMatchingTypeName")
public class Tag implements Serializable
{
    private static final long serialVersionUID = -9180076960743137786L;
    
    public static final String CACHE_KEY_PREFIX_ID = "uam_tagdao_tag_";
    
    public static final int DEFAULT_PAGE_SIZE = 100;
    
    @NotBlank
    @Size(min = 1, max = 64)
    private String tag;
    
    private String id;
    
    public String getTag()
    {
        return tag;
    }
    
    public void setTag(String tag)
    {
        this.tag = tag;
    }
    
    public String getId()
    {
        return id;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
}
