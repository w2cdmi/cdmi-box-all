package com.huawei.sharedrive.app.utils.file.test;

import junit.framework.Assert;

import org.junit.Test;

import com.huawei.sharedrive.app.utils.file.TempFileUtils;


public class TempFileUtilsTest
{
    @Test
    public void getTempPathTest()
    {
        String tempPath = TempFileUtils.getTempPath();
        System.out.println(tempPath);
        Assert.assertEquals("/opt/tomcat_ufm/webapps/ufm/temp/", tempPath);
    }
}
