package com.huawei.sharedrive.app.openapi.domain.teamspace;

import java.util.ArrayList;
import java.util.List;

import com.huawei.sharedrive.app.teamspace.domain.TeamSpace;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpaceList;

public class RestAllTeamSpaceList
{
    private List<RestTeamSpaceInfo> teamSpaces;
    
    private int limit;
    
    private long offset;
    
    private long totalCount;
    
    public RestAllTeamSpaceList()
    {
        
    }
    
    public RestAllTeamSpaceList(TeamSpaceList teamSpaceList)
    {
        this.setTeamSpaces(teamSpaceList.getTeamSpaceList());
        this.limit = teamSpaceList.getLimit();
        this.offset = teamSpaceList.getOffset();
        this.totalCount = teamSpaceList.getTotalCount();
    }
    
    public List<RestTeamSpaceInfo> getTeamSpaces()
    {
        return teamSpaces;
    }
    
    public void setTeamSpaces(List<TeamSpace> teamSpaceList)
    {
        if (null == teamSpaceList)
        {
            return;
        }
        
        this.teamSpaces = new ArrayList<RestTeamSpaceInfo>(teamSpaceList.size());
        RestTeamSpaceInfo temp = null;
        for (TeamSpace teamSpace : teamSpaceList)
        {
            temp = new RestTeamSpaceInfo(teamSpace);
            this.teamSpaces.add(temp);
        }
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
