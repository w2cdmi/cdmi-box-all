/**
 * 
 */
package com.huawei.sharedrive.app.test.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.huawei.sharedrive.app.files.service.INodeIdGenerateService;
import com.huawei.sharedrive.app.test.other.AbstractSpringTest;

/**
 * @author q90003805
 * 
 */
public class INodeIdGenerateServiceTest extends AbstractSpringTest
{
    @Autowired
    private INodeIdGenerateService iNodeIdGenerateService;
    
    @Test
    public void testGet() throws Exception
    {
        System.out.println(iNodeIdGenerateService.getNextUserNodeId(123));
    }
    @Test
    public void testDel() throws Exception
    {
        iNodeIdGenerateService.delete(123456);
    }
}
