package com.huawei.sharedrive.app.openapi.domain.teamspace.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.huawei.sharedrive.app.openapi.domain.teamspace.RestAllTeamSpaceList;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpace;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpaceList;

public class RestAllTeamSpaceListTest
{
    @Test
    public void junitTest()
    {
        RestAllTeamSpaceList rest = new RestAllTeamSpaceList();
        rest.getTeamSpaces();
        TeamSpaceList teamSpaceList = new TeamSpaceList();
        teamSpaceList.setLimit(123);
        teamSpaceList.setOffset(1000L);
        teamSpaceList.setTotalCount(1000L);
        List<TeamSpace> list = new ArrayList<TeamSpace>();
        TeamSpace tt = new TeamSpace();
        list.add(tt);
        teamSpaceList.setTeamSpaceList(list);
        
        rest.getTeamSpaces();
        rest.setTeamSpaces(null);
        rest.setTeamSpaces(list);
        rest.getTeamSpaces();
        RestAllTeamSpaceList rest1 = new RestAllTeamSpaceList(teamSpaceList);
        rest1.getTeamSpaces();
        rest1.setTeamSpaces(null);
        rest1.setTeamSpaces(list);
        rest1.getTeamSpaces();
        rest1.setLimit(123);
        rest1.getLimit();
        rest1.getOffset();
        rest1.setOffset(123456L);
        rest1.getTotalCount();
        rest1.setTotalCount(123456L);
    }
}
