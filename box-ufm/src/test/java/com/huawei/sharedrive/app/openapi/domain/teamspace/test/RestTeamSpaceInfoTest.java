package com.huawei.sharedrive.app.openapi.domain.teamspace.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.huawei.sharedrive.app.openapi.domain.teamspace.RestTeamMember;
import com.huawei.sharedrive.app.openapi.domain.teamspace.RestTeamMemberInfo;
import com.huawei.sharedrive.app.openapi.domain.teamspace.RestTeamMemberList;
import com.huawei.sharedrive.app.openapi.domain.teamspace.RestTeamSpaceInfo;
import com.huawei.sharedrive.app.openapi.domain.teamspace.RestUserTeamSpaceList;
import com.huawei.sharedrive.app.teamspace.domain.TeamMemberList;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpace;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpaceMemberships;
import com.huawei.sharedrive.app.user.domain.User;

public class RestTeamSpaceInfoTest
{
    @Test
    public void junitTest()
    {
        RestTeamSpaceInfo r = new RestTeamSpaceInfo();
        r.setCreatedAt(new Date());
        r.getCreatedAt();
        r.setCreatedBy(123456L);
        r.getCreatedBy();
        r.setCreatedByUserName("createdByUserName");
        r.getCreatedByUserName();
        r.setCurNumbers(12345L);
        r.getCurNumbers();
        r.setDescription("description");
        r.getDescription();
        r.setId(123L);
        r.getId();
        r.setMaxMembers(123);
        r.getMaxMembers();
        r.setMaxVersions(123);
        r.getMaxVersions();
        r.setName("name");
        r.getName();
        r.setNewFileMsg((byte) 2);
        r.getNewFileMsg();
        r.setOwnedByUserName("ownedByUserName");
        r.getOwnedByUserName();
        r.setOwnerBy(123L);
        r.getOwnerBy();
        r.setOwnerByUserName("ownerByUserName");
        r.getOwnerByUserName();
        r.setRegionId(123);
        r.getRegionId();
        r.setSpaceQuota(123L);
        r.getSpaceQuota();
        r.setSpaceUsed(123L);
        r.getSpaceUsed();
        r.setStatus((byte) 0);
        r.getStatus();
        r.setCreatedAt(null);
        r.getCreatedAt();
        TeamSpace teamSpace = new TeamSpace();
        new RestTeamSpaceInfo(teamSpace);
    }
    
    @Test
    public void junitTest1()
    {
        RestUserTeamSpaceList r = new RestUserTeamSpaceList();
        r.getLimit();
        r.setLimit(123);
        r.getOffset();
        r.setOffset(1234L);
        r.getTotalCount();
        r.setTotalCount(1234L);
        r.getMemberships();
        r.setMemberships(null);
        List<TeamSpaceMemberships> teamMemberList = new ArrayList<TeamSpaceMemberships>();
        TeamSpaceMemberships e = new TeamSpaceMemberships();
        e.setCreatedAt(new Date());
        e.setCreatedBy(123456L);
        e.setId(123L);
        teamMemberList.add(e);
        TeamMemberList listTeamMember = new TeamMemberList();
        listTeamMember.setLimit(123);
        listTeamMember.setOffset(123L);
        listTeamMember.setTotalCount(123L);
        listTeamMember.setTeamMemberList(teamMemberList);
        try
        {
            RestUserTeamSpaceList rr = new RestUserTeamSpaceList(listTeamMember);
            rr.setMemberships(teamMemberList);
        }
        catch (Exception e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }
    
    @Test
    public void junitTest2()
    {
        RestTeamMemberInfo r = new RestTeamMemberInfo();
        r.getTeamId();
        r.setTeamId(123L);
        r.getTeamRole();
        r.setTeamRole("teamRole");
        r.getRole();
        r.setRole("role");
        r.getMember();
        r.setMember(new RestTeamMember());
        r.getId();
        r.setId(1234L);
        r.getTeamspace();
        r.setTeamspace(new RestTeamSpaceInfo());
        TeamSpaceMemberships teamMember = new TeamSpaceMemberships();
        teamMember.setId(123L);
        teamMember.setLoginName("tom");
        teamMember.setCloudUserId(123L);
        teamMember.setCreatedAt(new Date());
        teamMember.setCreatedBy(123L);
        teamMember.setMember(new User());
        teamMember.setModifiedAt(new Date());
        teamMember.setModifiedBy(123L);
        teamMember.setRole("role");
        teamMember.setStatus(1);
        teamMember.setTableSuffix(2);
        teamMember.setTeamRole("SteamRole");
        teamMember.setTeamSpace(new TeamSpace());
        teamMember.setUserId(1);
        teamMember.setUsername("tom");
        teamMember.setUserType("1");
        new RestTeamMemberInfo(teamMember);
    }
    
    @Test
    public void junitTest3()
    {
        try
        {
            RestTeamMemberList r = new RestTeamMemberList();
            r.getLimit();
            r.setLimit(123);
            r.getOffset();
            r.setOffset(123L);
            r.getTotalCount();
            r.setTotalCount(123L);
            List<TeamSpaceMemberships> teamMemberList = new ArrayList<TeamSpaceMemberships>();
            TeamSpaceMemberships e = new TeamSpaceMemberships();
            e.setCreatedAt(new Date());
            e.setCreatedBy(123456L);
            e.setId(123L);
            e.setCloudUserId(123L);
            e.setStatus(1);
            e.setCreatedBy(123L);
            teamMemberList.add(e);
            TeamMemberList listTeamMember = new TeamMemberList();
            listTeamMember.setLimit(123);
            listTeamMember.setOffset(123L);
            listTeamMember.setTotalCount(123L);
            listTeamMember.setTeamMemberList(teamMemberList);
            RestTeamMemberList list = new RestTeamMemberList(listTeamMember);
            list.getMemberships();
            list.setMemberships(null);
            list.setMemberships(teamMemberList);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
