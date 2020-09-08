package com.huawei.sharedrive.app.test.openapi.common;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import com.huawei.sharedrive.app.test.domain.user.RestUserloginRsp;
import com.huawei.sharedrive.app.test.openapi.BaseConnection;
import com.huawei.sharedrive.app.test.openapi.share.JSonUtils;

public class LoginTest
{
    
    
    
    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception
    {
        LoginTest.getUser1();
    }
    
    private static final String FILE_USER1 = "testData/users/user1.txt";
    
    private static final String FILE_USER2 = "testData/users/user2.txt";
    
    public static RestUserloginRsp getUser1() throws Exception
    {
        return getUser(FILE_USER1);
    }
    
    public static RestUserloginRsp getUser2() throws Exception
    {
        return getUser(FILE_USER2);
    }
    
    private static RestUserloginRsp getUser(String fileName) throws Exception
    {
        String bodyStr = MyFileUtils.getDataFromFile(fileName);
        String urlStr = MyTestUtils.SERVER_URL_UAM_V2 + "token";
        String userInfoString = null;
        HttpURLConnection openurl = null;
        try {
        	openurl = BaseConnection.getURLConnection(urlStr, "POST");
            openurl.setRequestProperty("x-device-sn", "127.0.0.1");
            openurl.setRequestProperty("x-device-type", "web");
            openurl.setRequestProperty("x-device-os", "win 7");
            openurl.setRequestProperty("x-device-name", "windows");
            openurl.setRequestProperty("x-client-version", "10.0.1");
            openurl.setRequestProperty("x-request-ip", "127.0.0.1");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            openurl.connect();
            openurl.getOutputStream().write(bodyStr.getBytes());
            if(openurl.getResponseCode() == 200  )
            {
                InputStream stream = openurl.getInputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(stream));
                StringBuilder sb = new StringBuilder();
                String re = null;
                while( (re=in.readLine())!= null )
                {
                    sb.append(re);
                }
                userInfoString = sb.toString();
            }
            else
            {
                System.out.println(openurl.getResponseCode());
                throw new Exception();
            }
        } 
        finally
        {
            openurl.disconnect();
        }
        RestUserloginRsp user = JSonUtils.stringToObject(userInfoString, RestUserloginRsp.class);
        
        return user;
    }
}
