package com.huawei.sharedrive.isystem.util.test;

import junit.framework.Assert;

import org.junit.Test;

import com.huawei.sharedrive.isystem.util.CSRFTokenManager;

public class CSRFTokenManagerTest
{
    @Test
    public void getTokenForSessionTest()
    {
        String name = CSRFTokenManager.CSRF_TOKEN_FOR_SESSION_ATTR_NAME;
        System.out.println(name);
        Assert.assertEquals("com.huawei.sharedrive.isystem.util.CSRFTokenManager.tokenval", name);        
    }
}
