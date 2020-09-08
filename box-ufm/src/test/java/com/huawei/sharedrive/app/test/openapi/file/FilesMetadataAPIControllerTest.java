package com.huawei.sharedrive.app.test.openapi.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

import com.huawei.sharedrive.app.test.openapi.BaseConnection;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;


public class FilesMetadataAPIControllerTest
{
    private String syncVersion = "0";
    
    @Test
    public void getFolderChangeMetadata()
    {
        String urlString = "http://127.0.0.1:8080/sharedrive/api/v1/metadata/";
        urlString += Constants.owner_id + "/0";
        urlString += "/change";
        
        URL url;
        
        try
        {
            
            url = new URL(urlString);
            HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestMethod("PUT");
            openurl.setRequestProperty("Authorization", Constants.token);
            openurl.setRequestProperty("Content-type", "application/json");
            
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            
            String loginBody = "{\"modifiedAt\":126548997000}";
            //
            openurl.getOutputStream().write(loginBody.getBytes());
            openurl.connect();
            
            String filepath = "D:/SQL_0.LITE";
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

        //urlString += "?limit=/";
        //urlString += "?offset=";
        
        try
        {
            String urlString = Constants.SERVER_URL + "/api/v1/metadata/";
            urlString += MyTestUtils.getTestCloudUserId1() + "/0?isNeedZip=true";
            
            HttpURLConnection openurl = BaseConnection.getURLConnection(urlString, "GET");
            String auth = Constants.tokenType + " " + MyTestUtils.getTestUserToken1();
            openurl.setRequestProperty("Authorization", auth);
        
            openurl.setDoInput(true);
            openurl.setDoOutput(false);
            openurl.connect();
            
            String filepath = "F:/SQL_0.LITE.zip";
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
    public void getSyncMetadata()
    {
        String urlString = "http://127.0.0.1:8080/sharedrive/api/v1/metadata/";
        urlString += Constants.owner_id + "?syncVersion=";
        urlString += syncVersion;
        
        URL url;
        
        try
        {
            
            url = new URL(urlString);
            HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestMethod("GET");
            openurl.setRequestProperty("Authorization", Constants.token);
            openurl.setDoInput(true);
            openurl.setDoOutput(false);
            openurl.connect();
            
            String filepath = "J:/SQL_0.LITE";
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
    public void listen()
    {
        String urlString = "http://10.169.34.108:8080/ufm_c10/api/v3/event/listen?syncVersion=";
        urlString += 2;
        
        URL url;
        
        try
        {
            
            url = new URL(urlString);
            HttpURLConnection openurl = BaseConnection.getURLConnection(urlString, "GET");
            openurl.setRequestMethod("GET");
            openurl.setRequestProperty("Authorization", "OneBox/7ca1365f54dd8a4cb5f76349270fb185");
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
    
}
