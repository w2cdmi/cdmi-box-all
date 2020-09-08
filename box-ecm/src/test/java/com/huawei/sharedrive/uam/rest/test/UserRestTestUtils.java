package com.huawei.sharedrive.uam.rest.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLDecoder;

import org.apache.commons.io.IOUtils;

public class UserRestTestUtils
{

    public static final String APP_TOKEN = "";
    
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
    
    /**
     * 获取Data文件内容
     * 返回Token对象
     * @param fileName
     * @return
     */
    public static String getDataFromFile(String fileName)
    {
        StringBuilder sb = new StringBuilder();
        String path = UserRestTestUtils.class.getResource("").getPath();
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
    
    private static String token1 = null;
    
    private static String token2 = null;
    
    
    
    
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
    
    
}
