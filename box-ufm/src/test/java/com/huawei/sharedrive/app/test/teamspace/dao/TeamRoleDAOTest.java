package com.huawei.sharedrive.app.test.teamspace.dao;


import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.huawei.sharedrive.app.teamspace.dao.TeamRoleDAO;
import com.huawei.sharedrive.app.teamspace.domain.TeamRole;
import com.huawei.sharedrive.app.test.other.AbstractSpringTest;



public class TeamRoleDAOTest extends AbstractSpringTest
{
    
    private static Logger logger = LoggerFactory.getLogger(TeamRoleDAOTest.class);
    
    @Autowired
    private TeamRoleDAO teamRoleDAO;
    
    
    @Test
    public void listTeamRoleDAO()
    {
        List<TeamRole> lst =teamRoleDAO.listTeamRole();
        
        for(TeamRole role :lst)
        {
            logger.info(ToStringBuilder.reflectionToString(role));
        }
        
    }
    
    @Test
    public void getTeamRoleDAO() throws Exception
    {
        
        TeamRole role  = teamRoleDAO.getTeamRole("admin");
        if(null != role)
        {
            logger.info(ToStringBuilder.reflectionToString(role));
        }
        else
        {
            throw new Exception("notFound");
        }

        role  = teamRoleDAO.getTeamRole("admin1");
        if(null == role)
        {
            logger.info(ToStringBuilder.reflectionToString(role));
        }
        else
        {
            throw new Exception("ErrorFound");
        }
        
        
    }
    
    
//    @Test
//    public void insertINodeACLDAO()
//    {
//        INodeACL acl = new INodeACL();
//        acl.setId(12412);
//        acl.setInodeID(12345);
//        acl.setOwnedBy(12);
//        acl.setResourceRole("editer");
//        acl.setCreatedBy(12);
//        acl.setCreatedAt(new Date());
//        acl.setAccessUserID(1234);
//        iNodeACLDAO.create(acl);
//    }
//    
//    
//    @Test
//    public void getINodeACLDAO()
//    {
//        INodeACL acl = new INodeACL();
//        acl.setId(12412);
//        acl.setOwnedBy(12);
//        acl =iNodeACLDAO.get(acl);
//        logger.info(ToStringBuilder.reflectionToString(acl));
//    }
//    
//    @Test
//    public void getByResourceAndUser()
//    {
//        INodeACL acl = new INodeACL();
//        acl.setInodeID(12345);
//        acl.setOwnedBy(12);
//        acl.setAccessUserID(1234);
//        acl.setUserType(0);
//        acl =iNodeACLDAO.getByResourceAndUser(acl);
//        logger.info(ToStringBuilder.reflectionToString(acl));
//    }
//    
//    @Test
//    public void getlstACL()
//    {
//        List<INodeACL> lstACL =iNodeACLDAO.get(12, 12345, 1234, 0);
//        logger.info(ToStringBuilder.reflectionToString(lstACL));
//    }
//    
//    
//    @Test
//    public void updateACL()
//    {
//        INodeACL acl = new INodeACL();
//        acl.setId(12412);
////        acl.setInodeID(12345);
//        acl.setOwnedBy(12);
//        acl.setResourceRole("viewer");
//        //acl.setModifiedAt(new Date());
//        //acl.setModifiedBy(12);
//        iNodeACLDAO.update(acl);
//        logger.info(ToStringBuilder.reflectionToString(acl));
//    }
//    
//    
//    @Test
//    public void updateACLByResource()
//    {
//        INodeACL acl = new INodeACL();
//        //acl.setId(12412);
//        acl.setInodeID(12345);
//        acl.setOwnedBy(12);
//        acl.setAccessUserID(1234);
//        acl.setUserType(0);
//        acl.setResourceRole("auther");
//        acl.setModifiedAt(new Date());
//        acl.setModifiedBy(123);
//        iNodeACLDAO.updateByResource(acl);
//        logger.info(ToStringBuilder.reflectionToString(acl));
//    }
//    
//    
//    @Test
//    public void delteACL()
//    {
//        INodeACL acl = new INodeACL();
//        acl.setId(12412);
//        acl.setOwnedBy(12);
//        iNodeACLDAO.delete(acl);
//        logger.info(ToStringBuilder.reflectionToString(acl));
//    }
//    
//    @Test
//    public void insertOtherINodeACLDAO()
//    {
//        INodeACL acl = new INodeACL();
//        acl.setId(12412);
//        acl.setInodeID(12345);
//        acl.setOwnedBy(12);
//        acl.setResourceRole("editer");
//        acl.setCreatedBy(12);
//        acl.setCreatedAt(new Date());
//        acl.setAccessUserID(1234);
//        iNodeACLDAO.create(acl);
//    }
//    
//    @Test
//    public void deletebyACLByResource()
//    {
//        INodeACL acl = new INodeACL();
//        acl.setInodeID(12345);
//        acl.setOwnedBy(12);
//        acl.setAccessUserID(1234);
//        acl.setUserType(0);
//        iNodeACLDAO.deleteByResourceAndUser(acl);
//        logger.info(ToStringBuilder.reflectionToString(acl));
//    }
    
    

   
}