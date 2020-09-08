package com.huawei.sharedrive.app.utils.test;

import junit.framework.Assert;

import org.junit.Test;

import com.huawei.sharedrive.app.utils.CSRFTokenManager;

public class CSRFTokenManagerTest
{
    @Test
    public void getTokenForSessionTest()
    {
        String name = CSRFTokenManager.CSRF_TOKEN_FOR_SESSION_ATTR_NAME;
        System.out.println(name);
        Assert.assertEquals("com.huawei.sharedrive.app.utils.CSRFTokenManager.tokenval", name);
    }
}
