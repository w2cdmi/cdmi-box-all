package com.huawei.sharedrive.app.user.domain;

import java.util.List;

import com.huawei.sharedrive.app.openapi.domain.user.RestUserCreateRequest;

public class UserList
{
    private List<RestUserCreateRequest> users;
    
    public UserList()
    {
        
    }
    public UserList(int limit, long offset, int totalCount, List<RestUserCreateRequest> users)
    {
        this.setLimit(limit);
        this.setOffset(offset);
        this.setTotalCount(totalCount);
        this.setUsers(users);
    }
    
    public List<RestUserCreateRequest> getUsers()
    {
        return users;
    }
    
    public void setUsers(List<RestUserCreateRequest> users)
    {
        this.users = users;
    }
    
    /** 分页参数：当前页数量 */
    private int limit;
    
    /** 分页参数：偏移量 */
    private long offset;
    
    /** 总数 */
    private int totalCount;
    
    public int getLimit()
    {
        return limit;
    }
    
    public long getOffset()
    {
        return offset;
    }
    
    public int getTotalCount()
    {
        return totalCount;
    }
    
    public void setLimit(int limit)
    {
        this.limit = limit;
    }
    
    public void setOffset(long offset)
    {
        this.offset = offset;
    }
    
    public void setTotalCount(int totalCount)
    {
        this.totalCount = totalCount;
    }
    
}
