package com.huawei.sharedrive.uam.cmb.openapi.domain;

import java.io.Serializable;

public class RequestOrgTreeNode implements Serializable
{
    
    private static final long serialVersionUID = -5733118255274080214L;
    
    private String fatherGroupId;
    
    private Integer limit;
    
    private Integer offset;
    
    public String getFatherGroupId()
    {
        return fatherGroupId;
    }
    
    public void setFatherGroupId(String fatherGroupId)
    {
        this.fatherGroupId = fatherGroupId;
    }
    
    public Integer getLimit()
    {
        return limit;
    }
    
    public void setLimit(Integer limit)
    {
        this.limit = limit;
    }
    
    public Integer getOffset()
    {
        return offset;
    }
    
    public void setOffset(Integer offset)
    {
        this.offset = offset;
    }
    
}
