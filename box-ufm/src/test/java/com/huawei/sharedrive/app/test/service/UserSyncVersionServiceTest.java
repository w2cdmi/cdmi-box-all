package com.huawei.sharedrive.app.test.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.huawei.sharedrive.app.test.other.AbstractSpringTest;
import com.huawei.sharedrive.app.user.service.UserSyncVersionService;

public class UserSyncVersionServiceTest extends AbstractSpringTest
{
    
    @Autowired
    private UserSyncVersionService userSyncVersionService;
    
    @Test
    public void testGetNextVersion()
    {
        System.out.println(userSyncVersionService.getNextUserSyncVersion(123456));
    }
    
    @Test
    public void testDelete()
    {
        
        userSyncVersionService.delete(123456);
        
    }
    
    @Test
    public void testNotify() throws Exception
    {
        userSyncVersionService.notifyUserCurrentSyncVersionChanged(12, 35);
        Thread.sleep(1000);
        userSyncVersionService.notifyUserCurrentSyncVersionChanged(Long.MAX_VALUE, Long.MAX_VALUE);
        Thread.sleep(1000);
        userSyncVersionService.notifyUserCurrentSyncVersionChanged(1512154515, 255215151215515L);
        System.in.read();
    }
    
}
