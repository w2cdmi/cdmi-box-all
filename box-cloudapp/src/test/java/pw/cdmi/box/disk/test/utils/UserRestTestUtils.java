package pw.cdmi.box.disk.test.utils;

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
import pw.cdmi.box.disk.test.openapi.share.ShareAPIAddShareTest;
import pw.cdmi.core.utils.JsonUtils;

public class UserRestTestUtils
{

    public static final String APP_TOKEN = "";
    
    public static final String SERVER_URL = "http://10.169.67.1:8080/ufm/api/v1/";
    
    public static final String SERVER_URL_V2  = "http://10.169.67.1:8080/ufm/api/v2/";
    
    private static final String APP_AUTHORIZATION = "app,cloudappid121223,cloudapp";
    
    private static final String PASSWORD_USER1 = "*******";
    
    private static final String PASSWORD_USER2 = "******";
    
    private static final String USERNAME_USER1 = "z00xxxxxx";
    
    private static final String USERNAME_USER2 = "t00xxxxxx";
    
    
    public static String getAppAuthorization()
    {
        return APP_AUTHORIZATION;
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
    
    public static RestUserloginRsp getUser1() throws Exception
    {
        if(user1 == null)
        {
            user1 = getUser(USERNAME_USER1, PASSWORD_USER1);
        }
        return user1;
    }
    

    public static RestUserloginRsp getUser2() throws Exception
    {
        if(user2 == null)
        {
            user2 = getUser(USERNAME_USER2, PASSWORD_USER2);
        }
        return user2;
    }
    
    public static String getUserToken1() throws Exception
    {
        if(null == token1)
        {
            token1 =  getUser1().getToken();
        }
        return token1;
    }
    
    public static String getUserToken2() throws Exception
    {
        if(null == token2)
        {
            token2 =  getUser1().getToken();
        }
        return token2;
    }
    
    private static String token1 = null;
    
    private static String token2 = null;
    
    private static RestUserloginRsp user1 = null;
    
    private static RestUserloginRsp user2 = null;
    
    public static void main(String[] args) throws Exception
    {
        String userToken = getUserToken1();
        System.out.println(userToken);
    }
    
    public static void output(HttpURLConnection openurl) throws Exception 
    {
        System.out.println("Return status code is : " + openurl.getResponseCode());
        InputStream stream = null;
        try
        {
            System.out.println("message is " + openurl.getResponseMessage());
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
    
    private static RestUserloginRsp getUser(String username, String pwd) throws Exception
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
            String loginBody="{\"loginName\":\""+ username +"\", \"password\":\""+pwd+"\", \"deviceType\":1, \"deviceSN\":\"23412\", \"deviceOS\":\"23412\", \"deviceName\":\"23412\", \"deviceAddress\":\"127.0.0.1\",\"deviceAgent\":\"df\"}";
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
