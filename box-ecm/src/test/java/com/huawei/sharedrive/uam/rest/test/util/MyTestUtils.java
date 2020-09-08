package com.huawei.sharedrive.uam.rest.test.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import com.huawei.sharedrive.uam.openapi.domain.RestTokenCreateResponse;
import com.huawei.sharedrive.uam.openapi.domain.RestTokenRefreshResponse;
import com.huawei.sharedrive.uam.rest.test.UserRestTestUtils;

public class MyTestUtils
{
    private static final String TEST_NORMAL_USER = "testData/testNormalLogin.txt";
    
    private static final boolean showResult = false;
    
    public static final RestTokenCreateResponse login() throws Exception
    {
        String urlString = Constants.SERVER_RUL + "/api/v2/token";
        String tokenStr = "";
        URL url = null;
        url = new URL(urlString);
        System.out.println("url is " + urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        try
        {
            openurl.setRequestProperty("x-device-type", "ios");
            openurl.setRequestProperty("x-device-sn", "111");
            openurl.setRequestProperty("x-device-os", "MIUI 2.3.5");
            openurl.setRequestProperty("x-device-name", "xiaomi-one");
            openurl.setRequestProperty("x-client-version", "V1.1");
            openurl.setRequestMethod("POST");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            openurl.connect();
            String bodyStr = UserRestTestUtils.getDataFromFile(TEST_NORMAL_USER);
            openurl.getOutputStream().write(bodyStr.getBytes());
            if (openurl.getResponseCode() == 200)
            {
                InputStream stream = openurl.getInputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(stream));
                StringBuilder sb = new StringBuilder();
                String re = null;
                while ((re = in.readLine()) != null)
                {
                    sb.append(re);
                }
                tokenStr = sb.toString();
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
        MyResponseUtils.assert200(openurl, showResult);
        RestTokenCreateResponse user = JsonUtils.stringToObject(tokenStr, RestTokenCreateResponse.class);
        return user;
    }
    
    public static final RestTokenRefreshResponse refreshToken(String refreshToken) throws Exception
    {
        // 登录
        
        String urlString = Constants.SERVER_RUL + "/api/v2/token";
        URL url = null;
        String tokenStr = "";
        url = new URL(urlString);
        System.out.println("url is " + urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        try
        {
            openurl.setRequestProperty("Authorization", refreshToken);
            openurl.setRequestMethod("PUT");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            openurl.connect();
            if (openurl.getResponseCode() == 200)
            {
                InputStream stream = openurl.getInputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(stream));
                StringBuilder sb = new StringBuilder();
                String re = null;
                while ((re = in.readLine()) != null)
                {
                    sb.append(re);
                }
                tokenStr = sb.toString();
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
        MyResponseUtils.assert200(openurl, showResult);
        RestTokenRefreshResponse token = JsonUtils.stringToObject(tokenStr, RestTokenRefreshResponse.class);
        return token;
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
            if (null == stream)
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
    
    public static void outputWithCheck(HttpURLConnection openurl, Integer httpCode, String code)
        throws Exception
    {
        InputStream stream = null;
        try
        {
            stream = openurl.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));
            System.out.println("Normal result is ------------------------------------------");
            System.out.println(openurl.getResponseCode());
            String re = in.readLine();
            System.out.println(re);
        }
        catch (IOException e)
        {
            stream = openurl.getErrorStream();
            if (null == stream)
            {
                System.err.println("There is not any return data.");
            }
            else
            {
                BufferedReader in = new BufferedReader(new InputStreamReader(stream));
                if (httpCode != null && code != null)
                {
                    int returnhttpCode = openurl.getResponseCode();
                    String re = in.readLine();
                    RestErrorRsp resp = JsonUtils.stringToObject(re, RestErrorRsp.class);
                    
                    if (httpCode != null && returnhttpCode != httpCode)
                    {
                        System.err.println(openurl.getResponseCode());
                        System.err.println(re);
                        IOUtils.closeQuietly(in);
                    }
                    else if (code != null && !code.equals(resp.getCode()))
                    {
                        System.err.println(openurl.getResponseCode());
                        System.err.println(re);
                        IOUtils.closeQuietly(in);
                    }
                    else
                    {
                        System.out.println("Normal error result");
                        System.out.println(openurl.getResponseCode());
                        System.out.println(re);
                    }
                }
                else
                {
                    System.err.println(openurl.getResponseCode());
                    String re = in.readLine();
                    System.err.println(re);
                    IOUtils.closeQuietly(in);
                }
            }
        }
        
    }
    
}
