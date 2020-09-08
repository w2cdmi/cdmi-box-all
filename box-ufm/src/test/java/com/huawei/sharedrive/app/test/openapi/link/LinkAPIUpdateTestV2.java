package com.huawei.sharedrive.app.test.openapi.link;

import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

import com.huawei.sharedrive.app.test.domain.user.RestUserloginRsp;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

/**
 * 修改外链
 * 
 * @author l90005448
 * 
 */
public class LinkAPIUpdateTestV2
{
    
    private static long nodeId = 59;
    
    public static void main(String[] args)
    {
        LinkAPIUpdateTestV2 testUnit = new LinkAPIUpdateTestV2();
        testUnit.testUpdateLink();
    }
    
    private static final String FILE_LINK_UPDATE = "testData/updateLink.txt";
    
    @Test
    public void testUpdateLink()
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
        
        String urlString = MyTestUtils.SERVER_UFM_URL_V1 + "link/" + user.getUserId() + "/" + nodeId;
        
        HttpURLConnection openurl = null;
        try
        {
            URL url = new URL(urlString);
            openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestMethod("PUT");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.setRequestProperty("Authorization", MyTestUtils.getTestUserToken1());
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            openurl.connect();
            
            String bodyStr = MyFileUtils.getDataFromFile(FILE_LINK_UPDATE);
            
            openurl.getOutputStream().write(bodyStr.getBytes());
            
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
