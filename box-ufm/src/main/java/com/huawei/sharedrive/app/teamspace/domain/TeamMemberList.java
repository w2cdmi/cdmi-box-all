package com.huawei.sharedrive.app.teamspace.domain;

import java.util.List;

public class TeamMemberList
{
    private List<TeamSpaceMemberships> teamSpaceMembershipsList;
    
    private int limit;
    
    private long offset;
    
    private long totalCount;
    
    public List<TeamSpaceMemberships> getTeamMemberList()
    {
        return teamSpaceMembershipsList;
    }
    
    public void setTeamMemberList(List<TeamSpaceMemberships> teamMemberList)
    {
        this.teamSpaceMembershipsList = teamMemberList;
    }
    
    public int getLimit()
    {
        return limit;
    }
    
    public void setLimit(int limit)
    {
        this.limit = limit;
    }
    
    public long getOffset()
    {
        return offset;
    }
    
    public void setOffset(long offset)
    {
        this.offset = offset;
    }
    
    public long getTotalCount()
    {
        return totalCount;
    }
    
    public void setTotalCount(long totalCount)
    {
        this.totalCount = totalCount;
    }
    
}
