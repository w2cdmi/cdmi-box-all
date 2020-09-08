package com.huawei.sharedrive.app.test.acl.service;



import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.huawei.sharedrive.app.acl.domain.ACL;
import com.huawei.sharedrive.app.acl.domain.INodeACL;
import com.huawei.sharedrive.app.acl.service.impl.ACLManager;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.FileBaseService;

import com.huawei.sharedrive.app.test.other.AbstractSpringTest;

public class ACLManagerTest extends AbstractSpringTest
{

    private static Logger logger = LoggerFactory.getLogger(ACLManagerTest.class);
    
    @Autowired
    private ACLManager aCLManager;
    
    @Autowired
    private FileBaseService fileBaseService;
    
    
    
//    @Test
//    public void getINodeACL() throws BaseRunException
//    {
//        long total=0;
//        for(int i=0;i<100;i++)
//        {
//            long begin = System.currentTimeMillis();
//            INode inode = fileBaseService.getINodeInfo(1, 10);
//            ACL acl = aCLManager.getACLForAccessUser(1234, INodeACL.TYPE_USER, inode, null);
//            logger.info(ToStringBuilder.reflectionToString(acl));
//            long tmp = System.currentTimeMillis()-begin;
//            total =total+ tmp;
//            logger.info("tmp:"+(tmp));
//        }
//        
//        logger.info("total:"+(total/100));
//        
//    }
}
