package com.huawei.sharedrive.uam.core;

import java.io.Serializable;
import java.util.List;

import com.huawei.sharedrive.uam.user.domain.TeamSpace;
import com.huawei.sharedrive.uam.user.domain.User;

public class RankResponse implements Serializable
{
    
    private static final long serialVersionUID = -2688265771364586455L;
    
    private int limit;
    
    private long offset;
    
    private List<TeamSpace> teamSpace;
    
    private int totalCount;
    
    private List<User> users;
    
    public int getLimit()
    {
        return limit;
    }
    
    public long getOffset()
    {
        return offset;
    }
    
    public List<TeamSpace> getTeamSpace()
    {
        return teamSpace;
    }
    
    public int getTotalCount()
    {
        return totalCount;
    }
    
    public List<User> getUsers()
    {
        return users;
    }
    
    public void setLimit(int limit)
    {
        this.limit = limit;
    }
    
    public void setOffset(long offset)
    {
        this.offset = offset;
    }
    
    public void setTeamSpace(List<TeamSpace> teamSpace)
    {
        this.teamSpace = teamSpace;
    }
    
    public void setTotalCount(int totalCount)
    {
        this.totalCount = totalCount;
    }
    
    public void setUsers(List<User> users)
    {
        this.users = users;
    }
    
}
