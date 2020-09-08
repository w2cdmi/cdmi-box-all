package com.huawei.sharedrive.app.test.openapi.link;

import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

import com.huawei.sharedrive.app.test.domain.user.RestUserloginRsp;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

/**
 * 创建外链测试类
 * @author l90003768
 *
 */
public class LinkAPIForceGetTest
{
    private static long inodeId = 26;
    
    public static void main(String[] args)
    {
        LinkAPIForceGetTest testUnit = new LinkAPIForceGetTest();
        testUnit.testGetLink();
    }
    
    
    @Test
    public void testGetLink()
    {
        RestUserloginRsp user = null;
        try
        {
            user = MyTestUtils.getTestUser1();
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        }
        
        String urlString =  MyTestUtils.SERVER_UFM_URL_V1 + "link/forceGet/" + user.getUserId() + "/" + inodeId;
        
        HttpURLConnection openurl = null;
        try
        {
            URL url = new URL(urlString);
            openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestMethod("GET");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.setRequestProperty("Authorization", MyTestUtils.getTestUserToken1());
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            openurl.connect();
            
            
            MyTestUtils.output(openurl);
            openurl.disconnect();
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            openurl.disconnect();
        }
    }
    
}
