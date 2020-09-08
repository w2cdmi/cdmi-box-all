/**
 * 
 */
package com.huawei.sharedrive.app.test.service;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.huawei.sharedrive.app.test.other.AbstractSpringTest;
import com.huawei.sharedrive.app.user.domain.User;
import com.huawei.sharedrive.app.user.service.UserService;
import com.huawei.sharedrive.app.user.service.UserSyncVersionService;


public class UserServiceTest extends AbstractSpringTest
{
    @Autowired
    private UserService userservice;
    
    @Autowired
    private UserSyncVersionService  usersyncversionservice;
    
    
    @Test
    public void sync()
    {
    	
    	long num =  usersyncversionservice.getNextUserSyncVersion(123456);
    	System.out.println("----------"+ToStringBuilder.reflectionToString(num));
    }
}
