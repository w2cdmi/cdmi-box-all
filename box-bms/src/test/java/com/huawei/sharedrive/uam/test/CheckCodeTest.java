/**
 * 
 */
package com.huawei.sharedrive.uam.test;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import pw.cdmi.box.uam.util.CheckCode;

/**
 * @author q90003805
 * 
 */
public class CheckCodeTest
{
    @Test
    public void testCheckCodeCreate()
    {
        CheckCode checkCode = new CheckCode();
        
        checkCode = checkCode.createCheckCode();
        String checkCodeStr = checkCode.getCheckCodeStr();
        System.out.println(checkCodeStr);
        Assert.assertNotNull(checkCodeStr);
        Assert.assertEquals(4, checkCodeStr.length());
        
        Assert.assertNotNull(checkCode.getBuffImg());
        
        for (int i = 0; i < 10; i++)
        {
            checkCode = checkCode.createCheckCode();
            checkCodeStr = checkCode.getCheckCodeStr();
            System.out.println(checkCodeStr);
            Assert.assertNotNull(checkCodeStr);
            Assert.assertEquals(4, checkCodeStr.length());
            
            String filePathName = "C:\\test" + i + ".jpg";
            checkCode.createImgFile(filePathName);
            File file = new File(filePathName);
            
            Assert.assertTrue(file.exists());
        }
    }
}
