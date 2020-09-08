package com.huawei.sharedrive.app.test.teamspace.dao;


import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.huawei.sharedrive.app.teamspace.dao.TeamSpaceMembershipsDAO;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpaceMemberships;
import com.huawei.sharedrive.app.test.other.AbstractSpringTest;


public class TeamSpaceMembershipsDAOTest extends AbstractSpringTest
{
    
    private static Logger logger = LoggerFactory.getLogger(TeamSpaceMembershipsDAOTest.class);
    
    @Autowired
    private TeamSpaceMembershipsDAO teamSpaceMembershipsDAO;
    
    
    @Test
    public void createTeamSpaceMemberships()
    {
        TeamSpaceMemberships teamSpaceMemberships = new TeamSpaceMemberships();
        
        teamSpaceMemberships.setCloudUserId(12346);
        teamSpaceMemberships.setId(12);
        teamSpaceMemberships.setUserId(123);
        teamSpaceMemberships.setUserType(TeamSpaceMemberships.TYPE_USER);
        teamSpaceMemberships.setTeamRole("admin");
        teamSpaceMemberships.setCreatedBy(1234L);
        teamSpaceMemberships.setCreatedAt(new Date());

        teamSpaceMembershipsDAO.create(teamSpaceMemberships);
        
        logger.info(ToStringBuilder.reflectionToString(teamSpaceMemberships));
        
    }
    
    @Test
    public void getTeamSpaceMembershipsByID() throws Exception
    {
    
        TeamSpaceMemberships teamSpaceMemberships  = teamSpaceMembershipsDAO.getById(12346, 12);

        if(null == teamSpaceMemberships)
        {
            throw new Exception("get failed");
        }
        
        logger.info(ToStringBuilder.reflectionToString(teamSpaceMemberships));
        
    }
    
    
    @Test
    public void getTeamSpaceMemberships() throws Exception
    {
    
        TeamSpaceMemberships teamSpaceMemberships  = teamSpaceMembershipsDAO.getByUser(12346, "123", TeamSpaceMemberships.TYPE_USER);

        if(null == teamSpaceMemberships)
        {
            throw new Exception("get failed");
        }
        
        logger.info(ToStringBuilder.reflectionToString(teamSpaceMemberships));
        
    }
    
    
    @Test
    public void getTeamSpaceMembershipsCount() throws Exception
    {
    
        long count  = teamSpaceMembershipsDAO.getTeamSpaceMembershipsCount(12346, null, null);

        
        logger.info(ToStringBuilder.reflectionToString(count));
        
    }
    
    
    @Test
    public void listTeamSpaceMemberships() throws Exception
    {
    
        List<TeamSpaceMemberships> list  = teamSpaceMembershipsDAO.listTeamSpaceMemberships(12346, null, null, null, null);

        for(TeamSpaceMemberships ships :list)
        {
            logger.info(ToStringBuilder.reflectionToString(ships));
        }
       
        
    }
    
    
    @Test
    public void listUserTeamSpaceMemberships() throws Exception
    {
    
        List<TeamSpaceMemberships> list  = teamSpaceMembershipsDAO.listUserTeamSpaceMemberships("123", 0, TeamSpaceMemberships.TYPE_USER, null, null);

        for(TeamSpaceMemberships ships :list)
        {
            logger.info(ToStringBuilder.reflectionToString(ships));
        }
       
        
    }
    
    
    
    @Test
    public void updateTeamSpaceMemberships() throws Exception
    {
        TeamSpaceMemberships teamSpaceMemberships = new TeamSpaceMemberships();
        
        teamSpaceMemberships.setCloudUserId(12346);
        teamSpaceMemberships.setUserId(123);
        teamSpaceMemberships.setUserType(TeamSpaceMemberships.TYPE_USER);
        teamSpaceMemberships.setTeamRole("member");
        teamSpaceMemberships.setModifiedBy(1234L);
        teamSpaceMemberships.setModifiedAt(new Date());

        int count = teamSpaceMembershipsDAO.update(teamSpaceMemberships);
        
        
        if(1 != count)
        {
            throw new Exception("update failed");
        }
        
        logger.info(ToStringBuilder.reflectionToString(teamSpaceMemberships));
        
    }
    
    @Test
    public void updateTeamSpaceMembershipsByID() throws Exception
    {
        TeamSpaceMemberships teamSpaceMemberships = new TeamSpaceMemberships();
        
        teamSpaceMemberships.setCloudUserId(12346);
        teamSpaceMemberships.setUserId(123);
        teamSpaceMemberships.setUserType(TeamSpaceMemberships.TYPE_USER);
        teamSpaceMemberships.setTeamRole("member");
        teamSpaceMemberships.setModifiedBy(1234L);
        teamSpaceMemberships.setModifiedAt(new Date());
        int count = teamSpaceMembershipsDAO.updateTeamSpaceMemberRole(teamSpaceMemberships);
        
        
        if(1 != count)
        {
            throw new Exception("update failed");
        }
        
        logger.info(ToStringBuilder.reflectionToString(teamSpaceMemberships));
        
    }
    
    
    @Test
    public void deleteTeamSpaceMemberships()
    {
        TeamSpaceMemberships teamSpaceMemberships = new TeamSpaceMemberships();
        
        teamSpaceMemberships.setCloudUserId(12346);
        teamSpaceMemberships.setId(12);
        teamSpaceMemberships.setUserId(123);
        teamSpaceMemberships.setUserType(TeamSpaceMemberships.TYPE_USER);
    

        teamSpaceMembershipsDAO.delete(teamSpaceMemberships);
        
        logger.info(ToStringBuilder.reflectionToString(teamSpaceMemberships));
        
    }
    
    

   
}