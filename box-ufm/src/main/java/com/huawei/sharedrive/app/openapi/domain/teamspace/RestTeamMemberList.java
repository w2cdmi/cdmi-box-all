package com.huawei.sharedrive.app.openapi.domain.teamspace;

import java.util.ArrayList;
import java.util.List;

import com.huawei.sharedrive.app.teamspace.domain.TeamMemberList;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpaceMemberships;

public class RestTeamMemberList
{
    private List<RestTeamMemberInfo> memberships;
    
    private int limit;
    
    private long offset;
    
    private long totalCount;
    
    public RestTeamMemberList()
    {
        
    }
    
    public RestTeamMemberList(TeamMemberList teamMemberList2)
    {
        System.out.print(teamMemberList2.getTeamMemberList().size());
        this.setMemberships(teamMemberList2.getTeamMemberList());
        this.limit = teamMemberList2.getLimit();
        this.offset = teamMemberList2.getOffset();
        this.totalCount = teamMemberList2.getTotalCount();
    }
    
    public List<RestTeamMemberInfo> getMemberships()
    {
        return memberships;
    }
    
    public void setMemberships(List<TeamSpaceMemberships> memberList)
    {
        if (memberList == null)
        {
            return;
        }
        memberships = new ArrayList<RestTeamMemberInfo>(memberList.size());
        RestTeamMemberInfo memberInfo = null;
        for (TeamSpaceMemberships teamMember : memberList)
        {
            memberInfo = new RestTeamMemberInfo(teamMember);
            this.memberships.add(memberInfo);
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
