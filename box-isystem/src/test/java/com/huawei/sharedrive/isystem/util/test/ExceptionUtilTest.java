package com.huawei.sharedrive.isystem.util.test;

import junit.framework.Assert;

import org.junit.Test;

import com.huawei.sharedrive.isystem.exception.BusinessException;
import com.huawei.sharedrive.isystem.util.ExceptionUtil;

public class ExceptionUtilTest
{
    @Test
    public void getExceptionClassNameTest()
    {
        String className = ExceptionUtil.getExceptionClassName(new BusinessException());
        System.out.println(className);
        Assert.assertEquals("BusinessException", className);
    }
    
    @Test
    public void getExceptionClassNameTest1()
    {
        String className = ExceptionUtil.getExceptionClassName(null);
        System.out.println(className);
        Assert.assertEquals("", className);
    }
}
