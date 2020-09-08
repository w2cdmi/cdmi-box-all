/**
 * 
 */
package com.huawei.sharedrive.app.test.dao;

import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.app.files.dao.INodeDAO;
import com.huawei.sharedrive.app.files.domain.INode;

/**
 * @author q90003805
 * 
 */
@Component
public class TestService
{
    @Autowired
    private INodeDAO iNodeDAO;
    
    @Autowired
    private ApplicationContext context;
    
    private TestService proxySelf;
    
    @PostConstruct
    private void init()
    {
        proxySelf = context.getBean(TestService.class);
    }
    
    public void testTransaction()
    {
        proxySelf.test();
        //test();
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    public void test()
    {
        int i = 1;
        INode inode = new INode();
        inode.setId((long)(i + 10000));
        inode.setName("inode_name_" + i);
        inode.setOwnedBy(i);
        inode.setSyncVersion(i);
        inode.setCreatedAt(new Date());
        inode.setType((byte) (i % 2));
        iNodeDAO.create(inode);
        i = 1025;
        inode = new INode();
        inode.setId((long)(i + 10000));
        inode.setName("inode_name_" + i);
        inode.setOwnedBy(i);
        inode.setSyncVersion(i);
        inode.setCreatedAt(new Date());
        inode.setType((byte) (i % 2));
        iNodeDAO.create(inode);
    }
}
