package pw.cdmi.box.disk.test.openapi;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import org.junit.Test;

import pw.cdmi.box.disk.test.openapi.share.ShareAndLinkTestUtils;

public class FolderAPIControllerTest
{
    @Test
    public void copyFolder()
    {
        String urlString = "http://127.0.0.1:8080/sharedrive/api/v1/folders/";
        urlString += Constants.owner_id + "/";
        urlString += Constants.folder_id + "/copy";
        
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
            openurl.setDoOutput(true);
            openurl.connect();
            
            String time = Constants.sdf.format(Calendar.getInstance().getTime());
            System.out.println(time);
            
            String loginBody = "{\"name\":\"testfolder2110\", \"destParent\": \"0\"}";
            //
            // String loginBody = "{}";
            openurl.getOutputStream().write(loginBody.getBytes());
            
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
            e.printStackTrace();
        }
    }
    
    @Test
    public void createFolder()
    {
        String urlString = "http://127.0.0.1:8080/sharedrive/api/v1/folders/";
        urlString += Constants.owner_id + "/";
        
        URL url;
        
        try
        {
            
            for (int i = 0; i < 100; i++)
            {
                url = new URL(urlString);
                
                HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
                openurl.setRequestMethod("PUT");
                openurl.setRequestProperty("Content-type", "application/json");
                String auth = Constants.tokenType + " " + Constants.token;
                openurl.setRequestProperty("Authorization", auth);
                
                openurl.setDoInput(true);
                openurl.setDoOutput(true);
                openurl.connect();
                
                long time = Calendar.getInstance().getTime().getTime();
                
                String loginBody = "{\"name\":\"" + "testfolder211" + i + "\", \"contentCreatedAt\":" + time
                    + "}";
                
                openurl.getOutputStream().write(loginBody.getBytes());
                
                InputStream stream = openurl.getInputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(stream));
                System.out.println(openurl.getResponseCode());
                String re = in.readLine();
                System.out.println(re);
                
                System.out.println(openurl.getResponseCode());
                openurl.disconnect();
            }
            
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    public void deleteFolder()
    {
        String urlString = "http://127.0.0.1:8080/sharedrive/api/v1/folders/";
        urlString += Constants.owner_id + "/";
        urlString += Constants.folder_id;
        
        URL url;
        
        try
        {
            
            url = new URL(urlString);
            
            HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestMethod("DELETE");
            openurl.setRequestProperty("Content-type", "application/json");
            String auth = Constants.tokenType + " " + Constants.token;
            openurl.setRequestProperty("Authorization", auth);
            
            openurl.setDoInput(true);
            openurl.setDoOutput(false);
            openurl.connect();
            
            System.out.println(openurl.getResponseCode());
            
            openurl.disconnect();
            
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        getFolderInfo();
    }
    
    @Test
    public void getFolderInfo()
    {
        String urlString = "http://127.0.0.1:8080/sharedrive/api/v1/folders/";
        urlString += Constants.owner_id + "/";
        urlString += Constants.folder_id + "/";
        
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
            
            ShareAndLinkTestUtils.output(openurl);
            openurl.disconnect();
            
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    public void list1Folder()
    {
        String urlString = "http://127.0.0.1:8080/sharedrive/api/v1/folders/";
        urlString += Constants.owner_id + "/";
        urlString += Constants.parent_folder_id + "/items";
        
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
            
            BufferedReader in = new BufferedReader(new InputStreamReader(openurl.getInputStream()));
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
    public void listFolder()
    {
        String urlString = "http://127.0.0.1:8080/sharedrive/api/v1/folders/";
        urlString += Constants.owner_id + "/";
        
        URL url;
        
        try
        {
            
            url = new URL(urlString);
            
            HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestMethod("GET");
            String auth = Constants.tokenType + " " + Constants.token;
            openurl.setRequestProperty("Authorization", auth);
            // openurl.setRequestProperty("Content-type", "application/json");
            
            openurl.setDoInput(true);
            openurl.setDoOutput(false);
            openurl.connect();
            //
            // String loginBody="{ssss}";
            // openurl.getOutputStream().write(loginBody.getBytes());
            
            BufferedReader in = new BufferedReader(new InputStreamReader(openurl.getInputStream()));
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
    public void moveFolder()
    {
        String urlString = "http://127.0.0.1:8080/sharedrive/api/v1/folders/";
        urlString += Constants.owner_id + "/";
        urlString += Constants.folder_id + "/";
        urlString += "move";
        
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
            openurl.setDoOutput(true);
            openurl.connect();
            
            String loginBody = "{\"destParent\":\"239\"}";
            //
            // String loginBody = "{}";
            openurl.getOutputStream().write(loginBody.getBytes());
            
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
    public void renameFolder()
    {
        String urlString = "http://127.0.0.1:8080/sharedrive/api/v1/folders/";
        urlString += Constants.owner_id + "/";
        urlString += Constants.folder_id + "/rename";
        
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
            openurl.setDoOutput(true);
            openurl.connect();
            
            String loginBody = "{\"name\":\"newname\"}";
            //
            openurl.getOutputStream().write(loginBody.getBytes());
            System.out.println(openurl.getResponseCode());
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
    public void searchFolder()
    {
        
        String urlString = "http://127.0.0.1:8080/sharedrive/api/v1/search/";
        urlString += Constants.owner_id + "/";
        urlString += "items";
        
        URL url;
        
        try
        {
            
            url = new URL(urlString);
            
            HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestMethod("PUT");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.setRequestProperty("Authorization", Constants.token);
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            openurl.connect();
            
            String loginBody = "{\"name\":\"test\"}";
            openurl.getOutputStream().write(loginBody.getBytes());
            
            BufferedReader in = new BufferedReader(new InputStreamReader(openurl.getInputStream()));
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
