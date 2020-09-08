package com.huawei.sharedrive.app.test.openapi.link;

import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

import com.huawei.sharedrive.app.test.domain.user.RestUserloginRsp;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

/**
 * 邮件发送外链
 * @author l90005448
 *
 */
public class LinkAPISendMailTestV2
{
    private static long nodeId = 59;
    
    public static void main(String[] args)
    {
        LinkAPISendMailTestV2 testUnit = new LinkAPISendMailTestV2();
        testUnit.testSendMail();
    }
    
    private static final String FILE_SEND_MAIL = "testData/linkSendMail.txt"; 
    
    @Test
    public void testSendMail()
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
        
        String urlString =  MyTestUtils.SERVER_UFM_URL_V1 + "link/" + user.getUserId() + "/" + nodeId + "/sendemail";
        
        HttpURLConnection openurl = null;
        try
        {
            URL url = new URL(urlString);
            openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestMethod("POST");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.setRequestProperty("Authorization", MyTestUtils.getTestUserToken1());
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            openurl.connect();
            
            String bodyStr = MyFileUtils.getDataFromFile(FILE_SEND_MAIL);
            
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
