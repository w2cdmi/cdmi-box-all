package com.huawei.sharedrive.isystem.util.test;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import com.huawei.sharedrive.isystem.util.CheckCode;

public class CheckTest
{
    @Test
    public void testCheckCodeCreate()
    {
        try
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
                
                String filePathName = "/opt/tomcat_isystem/webapps/isystem/temp/" + i + ".jpg";
                checkCode.createImgFile(filePathName);
                File file = new File(filePathName);
                
                Assert.assertTrue(file.exists());
                
            }
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    public void getSetTest()
    {
        CheckCode c = new CheckCode();
        
        c.getBuffImg();
        
        c.setBuffImg(null);
        
        c.getCheckCodeStr();
        
        c.setCheckCodeStr("111111");
        
        c.getWidth();
        
        c.setWidth(6);
        
        c.getHeight();
        
        c.setHeight(6);
        
        c.getCodeCount();
        
        c.setCodeCount(5);
    }
}
