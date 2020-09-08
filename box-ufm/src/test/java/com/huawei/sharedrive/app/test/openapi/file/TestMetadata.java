package com.huawei.sharedrive.app.test.openapi.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.test.openapi.BaseConnection;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

public class TestMetadata {
	@Test
    public void listen()
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "event/listen?syncVersion=";
        urlString += 2;
        
        try
        {
            System.out.println(urlString);
            HttpURLConnection openurl = BaseConnection.getURLConnection(urlString, "GET");
            openurl.setRequestMethod("GET");
            openurl.setRequestProperty("Authorization", MyTestUtils.getTestUserToken1());
            openurl.setDoInput(true);
            openurl.setDoOutput(false);
            openurl.connect();
            String userInfoString = null;
            
            InputStream stream = openurl.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));
            StringBuilder sb = new StringBuilder();
            String re = null;
            while( (re=in.readLine())!= null )
            {
                sb.append(re);
            }
            userInfoString = sb.toString();
            
            System.out.println(userInfoString);
            openurl.disconnect();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
	
	@Test
    public void getChange()
    {
        try
        {
            String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "metadata/";
            urlString += MyTestUtils.getTestCloudUserId1() + "/0/change";
            HttpURLConnection openurl = BaseConnection.getURLConnection(urlString, "PUT");
            openurl.setRequestProperty("Authorization", MyTestUtils.getTestUserToken1());
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            
            openurl.connect();
            
            String bodyStr = "{\"modifiedAt\":126548997000}";
            openurl.getOutputStream().write(bodyStr.getBytes());
            
            String filepath = "D:/SQL_getChange.LITE";
            FileOutputStream outputStream = new FileOutputStream(new File(filepath));
            byte[] b = new byte[1024 * 64];
            int length;
            while ((length = openurl.getInputStream().read(b)) > 0)
            {
                outputStream.write(b, 0, length);
            }
            
            outputStream.close();
            
            System.out.println(openurl.getResponseCode());
            openurl.disconnect();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    @Test
    public void getFolderMetadata()
    {
        try
        {
            String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "metadata/";
            urlString += MyTestUtils.getTestCloudUserId1() + "/0?isNeedZip=true";
            
            HttpURLConnection openurl = BaseConnection.getURLConnection(urlString, "GET");
            openurl.setRequestProperty("Authorization", MyTestUtils.getTestUserToken1());
        
            openurl.setDoInput(true);
            openurl.setDoOutput(false);
            openurl.connect();
            
            String filepath = "D:/SQL_getFolderMetadata.LITE.zip";
            FileOutputStream outputStream = new FileOutputStream(new File(filepath));
            byte[] b = new byte[1024 * 64];
            int length;
            while ((length = openurl.getInputStream().read(b)) > 0)
            {
                outputStream.write(b, 0, length);
            }
            
            outputStream.close();
            
            System.out.println(openurl.getResponseCode());
            openurl.disconnect();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    public void getDeltaMetadata()
    {
        try
        {
            String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "metadata/";
            urlString += MyTestUtils.getTestCloudUserId1() + "?syncVersion=";
            urlString += 2 +"&zip=true";
            System.out.println("url is:"+urlString);
            HttpURLConnection openurl = BaseConnection.getURLConnection(urlString, "GET");
            openurl.setRequestProperty("Authorization", MyTestUtils.getTestUserToken1());
            openurl.setDoInput(true);
            openurl.setDoOutput(false);
            openurl.connect();
            
            String filepath = "D:/SQL_getDeltaMetadata.LITE";
            FileOutputStream outputStream = new FileOutputStream(new File(filepath));
            byte[] b = new byte[1024 * 64];
            int length;
            while ((length = openurl.getInputStream().read(b)) > 0)
            {
                outputStream.write(b, 0, length);
            }
            
            outputStream.close();
            
            System.out.println(openurl.getResponseCode());
            openurl.disconnect();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
