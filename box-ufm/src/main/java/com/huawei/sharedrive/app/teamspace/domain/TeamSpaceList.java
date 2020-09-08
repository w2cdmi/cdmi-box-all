package com.huawei.sharedrive.app.teamspace.domain;

import java.util.List;

public class TeamSpaceList
{
    private List<TeamSpace> teamSpacesList;
    
    private int limit;
    
    private long offset;
    
    private long totalCount;
    
    public List<TeamSpace> getTeamSpaceList()
    {
        return teamSpacesList;
    }
    
    public void setTeamSpaceList(List<TeamSpace> teamSpaceList)
    {
        this.teamSpacesList = teamSpaceList;
    }
    
    public int getLimit()
    {
        return limit;
    }
    
    public void setLimit(int limit)
    {
        this.limit = limit;
    }
    
    public long getTotalCount()
    {
        return totalCount;
    }
    
    public void setTotalCount(long totalCount)
    {
        this.totalCount = totalCount;
    }
    
    public long getOffset()
    {
        return offset;
    }
    
    public void setOffset(long offset)
    {
        this.offset = offset;
    }
    
}
