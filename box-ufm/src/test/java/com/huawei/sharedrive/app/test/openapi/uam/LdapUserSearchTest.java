package com.huawei.sharedrive.app.test.openapi.uam;

import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

/**
 * 添加共享人测试类
 * @author l90003768
 *
 */
public class LdapUserSearchTest
{
    private static final String USER_SEARCH = "testData/users/search.txt"; 
    
    
    
    public static void main(String[] args) throws Exception
    {
        LdapUserSearchTest testUnit = new LdapUserSearchTest();
        testUnit.normalLdap();
    }
    
    @Test
    public void normalLdap()
    {
        String urlString =  MyTestUtils.SERVER_URL_UAM_V2 + "users/search";
        
        URL url = null;
        
        try
        {
            url = new URL(urlString);
            System.out.println("url is " + urlString);
            HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestMethod("POST");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.setRequestProperty("Authorization", MyTestUtils.getTestUserToken1());
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            openurl.connect();
            
            String bodyStr = MyFileUtils.getDataFromFile(USER_SEARCH);
            
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
