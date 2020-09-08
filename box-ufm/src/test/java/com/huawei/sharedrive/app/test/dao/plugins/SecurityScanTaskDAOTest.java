package com.huawei.sharedrive.app.test.dao.plugins;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.huawei.sharedrive.app.plugins.scan.dao.SecurityScanTaskDAO;
import com.huawei.sharedrive.app.plugins.scan.domain.SecurityScanTask;
import com.huawei.sharedrive.app.test.other.AbstractSpringTest;

public class SecurityScanTaskDAOTest extends AbstractSpringTest
{
    
    private static String taskId = "c54a5312538c27aca9aa515c0aac6b79";
    
    @Autowired
    private SecurityScanTaskDAO securityScanTaskDAO;
    
    @Test
    public void testCreate()
    {
        SecurityScanTask task = new SecurityScanTask();
        task.setTaskId(taskId);
        task.setDssId(1);
        task.setNodeId(2);
        task.setNodeName("nodeName");
        task.setObjectId("objectId");
        task.setOwnedBy(3);
        task.setStatus(SecurityScanTask.STATUS_WAIING);
        task.setCreatedAt(new Date());
        task.setModifiedAt(new Date());
        securityScanTaskDAO.create(task);
    }
    
    @Test
    public void testUpdateStatus()
    {
        int result = securityScanTaskDAO.updateStatus(SecurityScanTask.STATUS_WAIING, new Date(), taskId);
        Assert.assertTrue(result == 1);
    }
    
    @Test
    public void testGetCurrentTaskNumber()
    {
        int result = securityScanTaskDAO.getTotalTasks(SecurityScanTask.STATUS_ALL);
        Assert.assertTrue(result == 1);
    }
    
    @Test
    public void testDelete()
    {
        int result = securityScanTaskDAO.delete(taskId);
        Assert.assertTrue(result == 1);
    }
    
}
