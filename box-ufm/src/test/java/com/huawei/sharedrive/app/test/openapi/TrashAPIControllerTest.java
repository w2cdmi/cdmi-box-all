package com.huawei.sharedrive.app.test.openapi;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

import com.huawei.sharedrive.app.test.openapi.file.Constants;

public class TrashAPIControllerTest
{
    @Test
    public void listTrashItems()
    {
        String urlString = "http://127.0.0.1:8080/api/1.0/trash/";
        urlString += Constants.owner_id + "/";
        
        URL url;
        
        try
        {
            
            url = new URL(urlString);
            
            HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestMethod("GET");
            String auth = Constants.tokenType + " " + Constants.token;
            openurl.setRequestProperty("Authorization", auth);
            
            openurl.setDoInput(true);
            openurl.setDoOutput(false);
            openurl.connect();
           
            
            InputStream stream = openurl.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));
            System.out.println(openurl.getResponseCode());
            String re = in.readLine();
            System.out.println(re);
            
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
    public void restoreTrashItem()
    {
        String urlString = "http://127.0.0.1:8080/api/1.0/trash/";
        urlString += Constants.owner_id + "/";
        urlString += Constants.folder_id + "/";
//        urlString +=  "restore";
        
        URL url;
        
        try
        {
            
            url = new URL(urlString);
            
            HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestMethod("PUT");
            openurl.setRequestProperty("Content-type", "application/json");
            String auth = Constants.tokenType + " " + Constants.token;
            openurl.setRequestProperty("Authorization", auth);
            
            openurl.setDoInput(true);
            openurl.setDoOutput(false);
            openurl.connect();
            
            InputStream stream = openurl.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));
            System.out.println(openurl.getResponseCode());
            String re = in.readLine();
            System.out.println(re);
            
            System.out.println(openurl.getResponseCode());
            openurl.disconnect();
            
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
