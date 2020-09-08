/**
 * 
 */
package com.huawei.sharedrive.app.test.dao;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.huawei.sharedrive.app.files.dao.INodeDAO;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.test.other.AbstractSpringTest;

import pw.cdmi.core.utils.HashTool;

/**
 * @author q90003805
 * 
 */
public class INodeDAOTest extends AbstractSpringTest
{
    @Autowired
    private INodeDAO iNodeDAO;
    
    @Autowired
    private TestService testService;
    
    @Test
    public void testCreate()
    {
        int[] count = new int[100];
        for (int i = 0; i < count.length; i++)
        {
            count[i] = 0;
        }
        for (int i = 5; i < 10005; i++)
        {
            INode inode = new INode();
            inode.setId((long)(i + 10000));
            inode.setName("inode_name_" + i);
            inode.setOwnedBy(i);
            inode.setSyncVersion(i);
            inode.setCreatedAt(new Date());
            inode.setType((byte) (i % 2));
            iNodeDAO.create(inode);
            count[inode.getTableSuffix()]++;
            inode = iNodeDAO.get(inode);
            Assert.assertEquals("inode_name_" + i, inode.getName());
        }
        for (int i = 0; i < count.length; i++)
        {
            System.out.print(count[i] + ",");
        }
    }
    
    @Test
    public void testTransaction()
    {
        //testService.test();
        testService.testTransaction();
    }
    
    @Test
    public void getSyncNodesBySyncVersion()
    {
        List<INode> list = iNodeDAO.getINodeBySyncVersion(123456, 0, 1000);
        System.out.println(list.size());
        for (INode iNode : list)
        {
            System.out.println(ToStringBuilder.reflectionToString(iNode));
        }
    }
    
    public static void main(String[] args)
    {
        System.out.println(HashTool.apply(String.valueOf(1025)) % 500);
    }
}
