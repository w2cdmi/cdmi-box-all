package com.huawei.sharedrive.app.test.openapi.share;

import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

public class ShareAPIListAdUserTest
{
    private static final String FILE_LIST_ADUSER = "testData/listAdUser.txt"; 
    
    
    public static void main(String[] args)
    {
        ShareAPIListAdUserTest testUnit = new ShareAPIListAdUserTest();
        testUnit.testListAdUser();
    }
    
    @Test
    public void testListAdUser()
    {
        String urlString =  MyTestUtils.SERVER_UFM_URL_V1 + "share/listADUser";
        
        URL url = null;
        
        try
        {
            url = new URL(urlString);
            HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestMethod("POST");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.setRequestProperty("Authorization", MyTestUtils.getTestUserToken1());
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            openurl.connect();
            
            String bodyStr = MyFileUtils.getDataFromFile(FILE_LIST_ADUSER);
            
            openurl.getOutputStream().write(bodyStr.getBytes());
            MyTestUtils.output(openurl);
            openurl.disconnect();
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    
}
