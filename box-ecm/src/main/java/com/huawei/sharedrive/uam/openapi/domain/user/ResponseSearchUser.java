package com.huawei.sharedrive.uam.openapi.domain.user;

import java.util.List;

public class ResponseSearchUser
{
    private Integer limit;
    
    private Long offset;
    
    private Long totalCount;
    
    private List<ResponseUser> users;
    
    public Integer getLimit()
    {
        return limit;
    }
    
    public Long getOffset()
    {
        return offset;
    }
    
    public Long getTotalCount()
    {
        return totalCount;
    }
    
    public List<ResponseUser> getUsers()
    {
        return users;
    }
    
    public void setLimit(Integer limit)
    {
        this.limit = limit;
    }
    
    public void setOffset(Long offset)
    {
        this.offset = offset;
    }
    
    public void setTotalCount(Long totalCount)
    {
        this.totalCount = totalCount;
    }
    
    public void setUsers(List<ResponseUser> users)
    {
        this.users = users;
    }
    
}
