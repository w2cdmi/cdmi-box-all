package com.huawei.sharedrive.uam.rest.test.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;

import com.huawei.sharedrive.uam.exception.ErrorCode;

public class MyResponseUtils
{
   
    private static final int CREATED = 201;
    
    private static final int SUCCESS = 200;
    
    
    
    public static void assert200(HttpURLConnection openurl, boolean showResult) throws Exception
    {
        assertHttpStatus(openurl, showResult, SUCCESS);
    }
    
    public static void assert201(HttpURLConnection openurl, boolean showResult) throws Exception
    {
        assertHttpStatus(openurl, showResult, CREATED);
    }
    
    public static void assertReturnCode(HttpURLConnection openurl, ErrorCode expected, boolean showResult) throws Exception 
    {
        assertReturnCode(openurl, expected.getCode(), showResult);
    }
    
    public static void assertReturnCode(HttpURLConnection openurl, String expected, boolean showResult) throws Exception 
    {

        InputStream stream = null;
        BufferedReader in = null;
        String returnCode = null;
        try
        {
            stream = openurl.getInputStream();
            in = new BufferedReader(new InputStreamReader(stream));
            String returnData = in.readLine();
            if(showResult)
            {
                System.err.println(returnData);
            }
            RestException restException = JsonUtils.stringToObject(returnData, RestException.class);
            if(null != restException)
            {
                returnCode = restException.getCode();
            }
        }
       catch(Exception e)
       {
           stream = openurl.getErrorStream();
           if(null == stream)
           {
               System.err.println("There is not any return data.");
           }
           else
           {
               in = new BufferedReader(new InputStreamReader(stream));
               String returnData = in.readLine();
               if(showResult)
               {
                   System.err.println(returnData);
               }
               RestException restException = JsonUtils.stringToObject(returnData, RestException.class);
               if(null != restException)
               {
                   returnCode = restException.getCode();
               }
           }
       }
       finally
       {
           IOUtils.closeQuietly(in);
           IOUtils.closeQuietly(stream);
           openurl.disconnect();
       }
       Assert.assertEquals(expected, returnCode);
    }
    private static void assertHttpStatus(HttpURLConnection openurl, boolean showResult, int expectedStatusCode) throws Exception 
    {
        int returnCode = openurl.getResponseCode();
        if(showResult)
        {
            InputStream stream = null;
            BufferedReader in = null;
            try
            {
                stream = openurl.getInputStream();
                in = new BufferedReader(new InputStreamReader(stream));
                System.out.println("Return data is------------------------------------------");
                System.out.println(in.readLine());
            }
           catch(Exception e)
           {
               stream = openurl.getErrorStream();
               if(null == stream)
               {
                   System.err.println("There is not any return data.");
                   e.printStackTrace();
               }
               else
               {
                   in = new BufferedReader(new InputStreamReader(stream));
                   System.out.println("Error return data is------------------------------------------");
                   System.err.println(in.readLine());
               }
           }
           finally
           {
               IOUtils.closeQuietly(in);
               IOUtils.closeQuietly(stream);
               openurl.disconnect();
           }
        }
        Assert.assertEquals(expectedStatusCode, returnCode);
    }

    
    
}
