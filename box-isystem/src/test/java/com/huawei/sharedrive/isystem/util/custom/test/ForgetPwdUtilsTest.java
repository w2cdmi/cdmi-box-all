package com.huawei.sharedrive.isystem.util.custom.test;

import junit.framework.Assert;

import org.junit.Test;

import com.huawei.sharedrive.isystem.util.custom.ForgetPwdUtils;

public class ForgetPwdUtilsTest
{
    @Test
    public void enableForgetTest()
    {
        boolean enableForget = ForgetPwdUtils.enableForget();
        System.out.println(enableForget);
        Assert.assertEquals(false, enableForget);
    }
}
