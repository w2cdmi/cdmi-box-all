package com.huawei.sharedrive.cloudapp.cmb.user.domain;

import java.io.Serializable;

public class RequestRetriewUser implements Serializable
{
    
    private static final long serialVersionUID = -5733118255274080214L;
    
    private Integer limit;
    
    private Integer offset;
    
    private String keyword;
    
    private Integer orgId;
    
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
    
    public String getKeyword()
    {
        return keyword;
    }
    
    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }
    
    public Integer getOrgId()
    {
        return orgId;
    }
    
    public void setOrgId(Integer orgId)
    {
        this.orgId = orgId;
    }
    
}
