package pw.cdmi.box.disk.test.openapi;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import org.junit.Test;

public class LinkAPIControllerTest
{
    String baseUrl = "http://127.0.0.1:8080/sharedrive/api/v1/link/";
    
    long owner_id = 2;
    
    long inodeId = 18;
    
    String token = "009de9a0be1de2c2c71c67ca4cd76d04";
    
    String tokenType = "RefreshAble";
    
    @Test
    public void createLink()
    {
        String urlString = baseUrl;
        urlString += owner_id + "/" + inodeId;
        URL url;
        
        try
        {
            
            url = new URL(urlString);
            
            HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestMethod("PUT");
            openurl.setRequestProperty("Content-type", "application/json");
            String auth = Constants.tokenType + " " + token;
            openurl.setRequestProperty("Authorization", auth);
            
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            openurl.connect();
            
            Calendar ca = Calendar.getInstance();
            
            long effectiveAttime = ca.getTime().getTime();
            
            ca.set(Calendar.DAY_OF_MONTH, ca.get(Calendar.DAY_OF_MONTH) + 4);
            
            long expireAttime = ca.getTime().getTime();
            
            String loginBody = "{\"accessCode\":\"@J^+gj4!\", \"effectiveAt\":\"" + effectiveAttime
                + "\", \"expireAt\":\"" + expireAttime + "\"}";
            
            System.out.println(loginBody);
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
    public void updateLink()
    {
        String urlString = baseUrl;
        urlString += owner_id + "/" + inodeId + "/update";
        URL url;
        
        try
        {
            
            url = new URL(urlString);
            
            HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestMethod("PUT");
            openurl.setRequestProperty("Content-type", "application/json");
            String auth = Constants.tokenType + " " + token;
            openurl.setRequestProperty("Authorization", auth);
            
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            openurl.connect();
            
            Calendar ca = Calendar.getInstance();
            
            ca.set(Calendar.DAY_OF_MONTH, ca.get(Calendar.DAY_OF_MONTH) + 4);
            
            String loginBody = "{\"accessCode\":\"@J^+gj4!\"}";
            
            System.out.println(loginBody);
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
    public void getLink()
    {
        String urlString = baseUrl;
        urlString += owner_id + "/" + inodeId;
        URL url;
        
        try
        {
            url = new URL(urlString);
            
            HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestMethod("GET");
            String auth = Constants.tokenType + " " + token;
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
    public void deleteLink()
    {
        String urlString = baseUrl;
        urlString += owner_id + "/" + inodeId + "/";
        URL url;
        
        try
        {
            url = new URL(urlString);
            
            HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestMethod("DELETE");
            String auth = Constants.tokenType + " " + token;
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
    public void sendLink()
    {
        String urlString = baseUrl;
        urlString += owner_id + "/" + inodeId + "/sendemail";
        URL url;
        
        try
        {
            
            url = new URL(urlString);
            
            HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestMethod("PUT");
            openurl.setRequestProperty("Content-type", "application/json");
            String auth = Constants.tokenType + " " + token;
            openurl.setRequestProperty("Authorization", auth);
            
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            openurl.connect();
            
            String loginBody = "{\"linkUrl\":\"http://127.0.0.1:8080/sharedrive/p/ae07786b\", \"totalCount\":1, \"emails\":\"tang@dd.com\"}";
            
            System.out.println(loginBody);
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
}
