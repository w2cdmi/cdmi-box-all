package com.huawei.sharedrive.app.test.teamspace.dao;


import java.util.Date;


import org.apache.commons.lang.builder.ToStringBuilder;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.huawei.sharedrive.app.teamspace.dao.TeamSpaceDAO;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpace;
import com.huawei.sharedrive.app.test.other.AbstractSpringTest;



public class TeamspaceDAOTest extends AbstractSpringTest
{
    
    private static Logger logger = LoggerFactory.getLogger(TeamspaceDAOTest.class);
    
    @Autowired
    private TeamSpaceDAO teamSpaceDAO;
    
    
    @Test
    public void createTeamSpace()
    {
        TeamSpace teamspace = new TeamSpace();
        teamspace.setCloudUserId(12346);
        teamspace.setCreatedAt(new Date());
        teamspace.setCreatedBy(12345);
        teamspace.setName("CSE 团队");
        teamspace.setStatus(0);
        teamspace.setDescription("技术");
        
        teamSpaceDAO.create(teamspace);
        
        logger.info(ToStringBuilder.reflectionToString(teamspace));
    }
    
    @Test
    public void getTeamSpace()
    {

        TeamSpace teamspace = teamSpaceDAO.get(12346);
        
        logger.info(ToStringBuilder.reflectionToString(teamspace));
    }
    
    
   
    
    @Test
    public void updateTeamSpace() throws Exception
    {

        TeamSpace teamspace = new TeamSpace();
        teamspace.setCloudUserId(12346);
        teamspace.setCreatedAt(new Date());
        teamspace.setCreatedBy(12345);
        teamspace.setName("CSE 团队");
        teamspace.setStatus(0);
        teamspace.setDescription("技术");
        
        int count= teamSpaceDAO.update(teamspace);
        
        if(1 != count)
        {
            throw new Exception("update failed");
        }
        
        logger.info(ToStringBuilder.reflectionToString(count));
    }
    
    
    @Test
    public void updateTeamSpaceStatus() throws Exception
    {

        int count= teamSpaceDAO.updateStatus(12346, 1);
        
        if(1 != count)
        {
            throw new Exception("update failed");
        }
        
        
        logger.info(ToStringBuilder.reflectionToString(count));
    }
    
    
    @Test
    public void deleteTeamSpace() throws Exception
    {

        int count= teamSpaceDAO.delete(12346);
        
        if(1 != count)
        {
            throw new Exception("update failed");
        }
        
        logger.info(ToStringBuilder.reflectionToString(count));
    }
    
    
    

    
    

   
}