package com.huawei.sharedrive.isystem.util.file.test;

import junit.framework.Assert;

import org.junit.Test;

import com.huawei.sharedrive.isystem.util.file.TempFileUtils;

public class TempFileUtilsTest
{
    @Test
    public void getTempPathTest()
    {
        String tempPath = TempFileUtils.getTempPath();
        System.out.println(tempPath);
        Assert.assertEquals("/opt/tomcat_isystem/webapps/isystem/temp/", tempPath);
    }
}
