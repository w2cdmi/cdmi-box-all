package com.huawei.sharedrive.uam.cmb.openapi.domain;

import java.io.Serializable;
import java.util.List;

import com.huawei.sharedrive.uam.cmb.retrieve.domain.RetrieveUser;

public class ResponseRetriewUser implements Serializable
{
    private static final long serialVersionUID = 8540031573969857721L;
    
    private Integer limit;
    
    private Long totalCount;
    
    private Long offset;
    
    private List<RetrieveUser> users;
    
    public Integer getLimit()
    {
        return limit;
    }
    
    public Long getTotalCount()
    {
        return totalCount;
    }
    
    public List<RetrieveUser> getUsers()
    {
        return users;
    }
    
    public void setLimit(Integer limit)
    {
        this.limit = limit;
    }
    
    public void setTotalCount(Long totalCount)
    {
        this.totalCount = totalCount;
    }
    
    public void setUsers(List<RetrieveUser> users)
    {
        this.users = users;
    }
    
    public Long getOffset()
    {
        return offset;
    }
    
    public void setOffset(Long offset)
    {
        this.offset = offset;
    }
}
