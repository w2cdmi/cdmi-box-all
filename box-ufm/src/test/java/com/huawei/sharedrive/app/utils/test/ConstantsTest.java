package com.huawei.sharedrive.app.utils.test;

import junit.framework.Assert;

import org.junit.Test;

import com.huawei.sharedrive.app.utils.Constants;

public class ConstantsTest
{
    @Test
    public void constractTest()
    {
        com.huawei.sharedrive.app.utils.Constants constant = new com.huawei.sharedrive.app.utils.Constants();
        constant.toString();
        Assert.assertEquals("SecurityScan", com.huawei.sharedrive.app.utils.Constants.APPID_SECURITYSCAN);
        
        Assert.assertEquals("Accept", Constants.HTTP_ACCEPT);
       
        Assert.assertEquals("X-Real-IP", Constants.HTTP_X_REAL_IP);

        Assert.assertEquals(false, Constants.IS_FILED_NAME_COMPATIBLE);
        
        Assert.assertEquals(false, Constants.IS_DIGEST_NAME_COMPATIBLE);
        
        Assert.assertEquals("application/json", Constants.JSON_TYPE);
        
        Assert.assertEquals((Integer)30, Constants.MAX_NAME_LOG);
        
        Assert.assertEquals((Integer)256, Constants.SAMPLING_LENGTH_FOR_SMALLER_FILE);

        Assert.assertEquals((Integer)600, Constants.STATISTICS_PERIODS_FOR_CHECK_QUOTA);
        
        Assert.assertEquals((Integer)20, Constants.STATISTICS_PERIODS_FOR_GET_INFO);
            
        Assert.assertEquals("/thumbnail?minHeight=96&minWidth=96", Constants.THUMBNAIL_PREFIX_BIG);
        
        Assert.assertEquals("/thumbnail?minHeight=200&minWidth=200", Constants.THUMBNAIL_PREFIX_HUGE);
        
        Assert.assertEquals("/thumbnail?minHeight=32&minWidth=32", Constants.THUMBNAIL_PREFIX_SMALL);
        
        Assert.assertEquals("-1", Constants.UFM_DEFAULT_APP_ID);
        
        Assert.assertEquals(60000, Constants.THRIFT_DSS_SOCKET_TIMEOUT);
         
        Assert.assertEquals("PreviewPlugin", Constants.APPID_PPREVIEW);
        
        Assert.assertEquals("SecurityScan", Constants.APPID_SECURITYSCAN);
        
    }
}
