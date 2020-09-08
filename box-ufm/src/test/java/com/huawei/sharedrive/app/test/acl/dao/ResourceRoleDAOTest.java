


package com.huawei.sharedrive.app.test.acl.dao;


import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.huawei.sharedrive.app.acl.dao.ResourceRoleDAO;
import com.huawei.sharedrive.app.acl.domain.ResourceRole;
import com.huawei.sharedrive.app.test.other.AbstractSpringTest;



public class ResourceRoleDAOTest extends AbstractSpringTest
{
    
    private static Logger logger = LoggerFactory.getLogger(ResourceRoleDAOTest.class);
    
    @Autowired
    private ResourceRoleDAO resourceRoleDAO;
    
    @Test
    public void testListResourceRole()
    {
        List<ResourceRole> lstRole = resourceRoleDAO.listResourceRole();
        
        for(ResourceRole role:lstRole)
        {
            logger.info(ToStringBuilder.reflectionToString(role));
        }
        
    }
    
    @Test
    public void insertResourceRole()
    {
        ResourceRole role = new ResourceRole();
        role.setResourceRole("edit1r");
        role.setACL(121111);
        role.setCreatedBy(1L);
        resourceRoleDAO.create(role);
    }
    
    
    @Test
    public void getResourceRole()
    {
        ResourceRole role ;
        role = resourceRoleDAO.get("editer");
        logger.info(ToStringBuilder.reflectionToString(role));
    }
    
    
    @Test
    public void updateResourceRole()
    {
        ResourceRole role = new ResourceRole() ;

        role.setResourceRole("edit1r");
        role.setACL(1);
        resourceRoleDAO.update(role);
        logger.info(ToStringBuilder.reflectionToString(role));
    }
    
    
    @Test
    public void deleteResourceRole()
    {
        ResourceRole role = new ResourceRole() ;

        role.setResourceRole("edit1r");
        role.setACL(1);
        resourceRoleDAO.delete(role);
        logger.info(ToStringBuilder.reflectionToString(role));
    }
   
}

