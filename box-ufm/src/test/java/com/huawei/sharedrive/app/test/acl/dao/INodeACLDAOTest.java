package com.huawei.sharedrive.app.test.acl.dao;


import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.huawei.sharedrive.app.acl.dao.INodeACLDAO;
import com.huawei.sharedrive.app.acl.domain.INodeACL;
import com.huawei.sharedrive.app.test.other.AbstractSpringTest;



public class INodeACLDAOTest extends AbstractSpringTest
{
    
    private static Logger logger = LoggerFactory.getLogger(INodeACLDAOTest.class);
    
    @Autowired
    private INodeACLDAO iNodeACLDAO;
    
    
    
    @Test
    public void insertINodeACLDAO()
    {
        INodeACL acl = new INodeACL();
        acl.setId(12412);
        acl.setiNodeId(12345);
        acl.setOwnedBy(12);
        acl.setResourceRole("editer");
        acl.setCreatedBy(12);
        acl.setCreatedAt(new Date());
        acl.setAccessUserId("1234");
        iNodeACLDAO.create(acl);
    }
    
    
    @Test
    public void getINodeACLDAO()
    {
        INodeACL acl =iNodeACLDAO.get(12, 12412);
        logger.info(ToStringBuilder.reflectionToString(acl));
    }
    
    @Test
    public void getByResourceAndUser()
    {
        INodeACL acl =iNodeACLDAO.getByResourceAndUser(12, 12345, "1234", INodeACL.TYPE_USER);
        logger.info(ToStringBuilder.reflectionToString(acl));
    }
    
//    @Test
//    public void getlstACL()
//    {
//        List<INodeACL> lstACL =iNodeACLDAO.get(12, 12345, 1234, 0);
//        logger.info(ToStringBuilder.reflectionToString(lstACL));
//    }
    
    
    @Test
    public void updateACL()
    {
        INodeACL acl = new INodeACL();
        acl.setId(12412);
//        acl.setInodeID(12345);
        acl.setOwnedBy(12);
        acl.setResourceRole("viewer");
        //acl.setModifiedAt(new Date());
        //acl.setModifiedBy(12);
        iNodeACLDAO.updateById(acl);
        logger.info(ToStringBuilder.reflectionToString(acl));
    }
    
    
    @Test
    public void updateACLByResource()
    {
        INodeACL acl = new INodeACL();
        //acl.setId(12412);
        acl.setiNodeId(12345);
        acl.setOwnedBy(12);
        acl.setAccessUserId("1234");
        acl.setUserType(INodeACL.TYPE_USER);
        acl.setResourceRole("auther");
        acl.setModifiedAt(new Date());
        acl.setModifiedBy(123);
        iNodeACLDAO.updateByResourceAndUser(acl);
        logger.info(ToStringBuilder.reflectionToString(acl));
    }
    
    
    @Test
    public void delteACL()
    {
        iNodeACLDAO.delete(12, 12412);
    }
    
    @Test
    public void insertOtherINodeACLDAO()
    {
        INodeACL acl = new INodeACL();
        acl.setId(12412);
        acl.setiNodeId(12345);
        acl.setOwnedBy(12);
        acl.setResourceRole("editer");
        acl.setCreatedBy(12);
        acl.setCreatedAt(new Date());
        acl.setAccessUserId("1234");
        iNodeACLDAO.create(acl);
    }
    
    @Test
    public void deletebyACLByResource()
    {
        iNodeACLDAO.deleteByResourceAndUser(12, 12345,"1234",INodeACL.TYPE_USER);
    }
    
    

   
}