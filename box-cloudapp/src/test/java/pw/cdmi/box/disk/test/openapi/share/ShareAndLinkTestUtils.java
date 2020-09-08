package pw.cdmi.box.disk.test.openapi.share;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;

import org.apache.commons.io.IOUtils;

import pw.cdmi.box.disk.client.domain.user.RestUserloginRsp;
import pw.cdmi.core.utils.JsonUtils;

public class ShareAndLinkTestUtils
{

    public static void main(String[] args) throws Exception
    {
        String userToken = getUserToken();
        System.out.println(userToken);
    }
    
    public static void output(HttpURLConnection openurl) throws Exception 
    {
        System.out.println("Return status code is : " + openurl.getResponseCode());
        InputStream stream = null;
        try
        {
            stream = openurl.getInputStream();
            System.out.println("available is " + stream.available());
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));
            System.out.println("Normal result is ------------------------------------------");
            System.out.println(openurl.getResponseCode());
            String re = in.readLine();
            System.out.println(re);
        }
        catch (IOException e)
        {
            System.err.println("Unnormal result is ------------------------------------------");
            stream = openurl.getErrorStream();
            if(null == stream)
            {
                System.err.println("There is not any return data.");
            }
            else
            {
                BufferedReader in = new BufferedReader(new InputStreamReader(stream));
                System.err.println(openurl.getResponseCode());
                String re = in.readLine();
                System.err.println(re);
                IOUtils.closeQuietly(in);
            }
            
        }
        
        
    }

    public static String getDataFromFile(String fileName)
    {
        StringBuilder sb = new StringBuilder();
        String path = ShareAPIAddShareTest.class.getResource("").getPath();
        String classPath = path.substring(0, path.indexOf("/classes/") + "/classes/".length()).replaceAll("%20", " ");
        
        try
        {
            classPath = URLDecoder.decode(classPath,"utf-8");
        }
        catch (UnsupportedEncodingException e1)
        {
            e1.printStackTrace();
        }
        
        File file = new File(classPath + fileName);
        BufferedReader br = null;
        try
        {
            br = new BufferedReader(new FileReader(file));
            String temp = null;
            while( (temp = br.readLine()) != null)
            {
                sb.append(temp);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
           IOUtils.closeQuietly(br);
        }
        System.out.println(sb.toString());
        return sb.toString();
    }
    
    public static final String SERVER_URL = "http://10.169.67.1:8080/sharedrive/api/v1/";
    
    public static String getUserToken() throws Exception
    {
        
        URL url = new URL(SERVER_URL + "auth/login");
        HttpURLConnection openurl = null;
        String userInfoString = null;
        try {
            openurl = (HttpURLConnection) url
                    .openConnection();
            openurl.setRequestMethod("POST");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            openurl.connect();
            String loginBody="{\"loginName\":\"l90003768\", \"password\":\"ilove`1234\", \"deviceType\":1, \"deviceSN\":\"23412\", \"deviceOS\":\"23412\", \"deviceName\":\"23412\", \"deviceAddress\":\"127.0.0.1\",\"deviceAgent\":\"df\"}";
            openurl.getOutputStream().write(loginBody.getBytes());
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
                throw new Exception();
            }
        } 
        finally
        {
            openurl.disconnect();
        }
        RestUserloginRsp user = JsonUtils.stringToObject(userInfoString, RestUserloginRsp.class);
        System.out.println("user token is " + user.getToken());
        return user.getToken();
        
    }
    
    public static String getUserToken2() throws Exception
    {
        
        URL url = new URL(SERVER_URL + "auth/login");
        HttpURLConnection openurl = null;
        String userInfoString = null;
        try {
            openurl = (HttpURLConnection) url
                    .openConnection();
            openurl.setRequestMethod("POST");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            openurl.connect();
            String loginBody="{\"loginName\":\"l90005448\", \"password\":\"guaiguai914@\", \"deviceType\":1, \"deviceSN\":\"23412\", \"deviceOS\":\"23412\", \"deviceName\":\"23412\", \"deviceAddress\":\"127.0.0.1\",\"deviceAgent\":\"df\"}";
            openurl.getOutputStream().write(loginBody.getBytes());
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
                throw new Exception();
            }
        } 
        finally
        {
            openurl.disconnect();
        }
        RestUserloginRsp user = JsonUtils.stringToObject(userInfoString, RestUserloginRsp.class);
        System.out.println("user token is " + user.getToken());
        return user.getToken();
        
    }
    
    public static RestUserloginRsp getUser() throws Exception
    {
        
        URL url = new URL(SERVER_URL + "auth/login");
        HttpURLConnection openurl = null;
        String userInfoString = null;
        try {
            openurl = (HttpURLConnection) url
                    .openConnection();
            openurl.setRequestMethod("POST");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            openurl.connect();
            String loginBody="{\"loginName\":\"l90003768\", \"password\":\"ilove`1234\", \"deviceType\":1, \"deviceSN\":\"23412\", \"deviceOS\":\"23412\", \"deviceName\":\"23412\", \"deviceAddress\":\"127.0.0.1\",\"deviceAgent\":\"df\"}";
            openurl.getOutputStream().write(loginBody.getBytes());
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
                throw new Exception();
            }
        } 
        finally
        {
            openurl.disconnect();
        }
        RestUserloginRsp user = JsonUtils.stringToObject(userInfoString, RestUserloginRsp.class);
        
        
        
        
        System.out.println("user token is " + user.getToken());
        return user;
    }
    
}
