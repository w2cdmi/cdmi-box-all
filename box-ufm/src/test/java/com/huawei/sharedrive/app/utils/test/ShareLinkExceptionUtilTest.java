package com.huawei.sharedrive.app.utils.test;

import org.junit.Test;

import junit.framework.Assert;

import com.huawei.sharedrive.app.dataserver.exception.BusinessException;
import com.huawei.sharedrive.app.utils.ShareLinkExceptionUtil;

public class ShareLinkExceptionUtilTest
{
    @Test
    public void getClassNameTest()
    {
        String className = ShareLinkExceptionUtil.getClassName(new BusinessException());
        System.out.println(className);
        Assert.assertEquals("BusinessException", className);
    }
}
