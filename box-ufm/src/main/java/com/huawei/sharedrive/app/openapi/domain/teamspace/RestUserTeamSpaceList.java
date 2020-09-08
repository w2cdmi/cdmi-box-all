package com.huawei.sharedrive.app.openapi.domain.teamspace;

import java.util.ArrayList;
import java.util.List;

import com.huawei.sharedrive.app.teamspace.domain.TeamMemberList;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpaceMemberships;

public class RestUserTeamSpaceList
{
    private List<RestTeamMemberInfo> memberships;
    
    private int limit;
    
    private long offset;
    
    private long totalCount;
    
    public RestUserTeamSpaceList()
    {
        
    }
    
    public RestUserTeamSpaceList(TeamMemberList listTeamMember)
    {
        this.setMemberships(listTeamMember.getTeamMemberList());
        this.limit = listTeamMember.getLimit();
        this.offset = listTeamMember.getOffset();
        this.totalCount = listTeamMember.getTotalCount();
    }
    
    public List<RestTeamMemberInfo> getMemberships()
    {
        return memberships;
    }
    
    public void setMemberships(List<TeamSpaceMemberships> teamMemberList)
    {
        if (null == teamMemberList)
        {
            return;
        }
        
        this.memberships = new ArrayList<RestTeamMemberInfo>(teamMemberList.size());
        RestTeamMemberInfo temp = null;
        for (TeamSpaceMemberships teamSpaceMemberships : teamMemberList)
        {
            temp = new RestTeamMemberInfo(teamSpaceMemberships);
            this.memberships.add(temp);
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
