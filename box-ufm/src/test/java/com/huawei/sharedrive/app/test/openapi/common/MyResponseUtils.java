package com.huawei.sharedrive.app.test.openapi.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.util.Date;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.test.openapi.share.JSonUtils;

public class MyResponseUtils
{
   
    private static final int CREATED = 201;
    
    private static final int SUCCESS = 200;

    private static final int BAD_REQUEST = 400;
    
    private static final int TEMPORARY_REDIRECT = 307;
    
    public static void assert200(HttpURLConnection openurl, boolean showResult) throws Exception
    {
        assertHttpStatus(openurl, showResult, SUCCESS);
    }
    
    public static void assert201(HttpURLConnection openurl, boolean showResult) throws Exception
    {
        assertHttpStatus(openurl, showResult, CREATED);
    }
    
    public static void assert400(HttpURLConnection openurl, boolean showResult) throws Exception
    {
        assertHttpStatus(openurl, showResult, BAD_REQUEST);
    }
    
    public static void getDitectResult(HttpURLConnection openurl, boolean showResult) throws Exception
    {
        InputStream stream = null;
        BufferedReader in = null;
        String returnCode = null;
        try
        {
            stream = openurl.getInputStream();
            File write = new File("d:/testDirect3.jpg");
            
            RandomAccessFile aFile = new RandomAccessFile(write,"rw");
            System.out.println("Return data is------------------------------------------");
            aFile.seek(0);
            byte[] buffer= new byte[1024];
            int length = 0;
            while(  ( length = stream.read(buffer, 0, 1024))>0)
            {
                aFile.write(buffer,0,length);
                for(int i=0;i<1024;i++)
                {
                    buffer[i]=0;
                }
            }
            stream.close();
            aFile.close();
        }catch(Exception e)
        {
            stream = openurl.getErrorStream();
            if (null == stream)
            {
                if (showResult)
                {
                    System.err.println(e.getMessage());
                }
                
            }
            else
            {
                in = new BufferedReader(new InputStreamReader(stream));
                String returnData = in.readLine();
                if (showResult)
                {
                    System.err.println(returnData);
                }
                RestException restException = JSonUtils.stringToObject(returnData, RestException.class);
                if (null != restException)
                {
                    returnCode = restException.getCode();
                    System.out.println("Exception:"+ returnCode);
                }
            }
        }
    }
    
    public static void assertReturnCode(HttpURLConnection connection, ErrorCode expected, boolean showResult) throws Exception 
    {
        assertReturnCode(connection, expected.getCode(), showResult);
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
            RestException restException = JSonUtils.stringToObject(returnData, RestException.class);
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
               if(showResult)
               {
                   System.err.println(e.getMessage());
               }
               
           }
           else
           {
               in = new BufferedReader(new InputStreamReader(stream));
               String returnData = in.readLine();
               if(showResult)
               {
                   System.err.println(returnData);
               }
               RestException restException = JSonUtils.stringToObject(returnData, RestException.class);
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
    /**
     * @param openurl
     * @return
     * @throws Exception
     */
    public static String getSuccessResponseString(HttpURLConnection openurl) throws Exception 
    {
        InputStream stream = null;
        BufferedReader in = null;
        try
        {
            stream = openurl.getInputStream();
            in = new BufferedReader(new InputStreamReader(stream));
            return in.readLine();
        }
       finally
       {
           IOUtils.closeQuietly(in);
           IOUtils.closeQuietly(stream);
           openurl.disconnect();
       }
    
    }

    /**
     * 获取成功返回的字符串
     * @param openurl
     * @param showResult
     * @param expectedStatusCode
     * @throws Exception
     */
    private static void assertHttpStatus(HttpURLConnection openurl, boolean showResult, int expectedStatusCode) throws Exception 
    {
        int returnCode = openurl.getResponseCode();
        long day=openurl.getDate();
        Date date = new Date(day);
        System.out.println(openurl.getHeaderField("server"));
        System.out.println(openurl.getHeaderField("Server"));
        System.out.println(openurl.getDate()+"   "+ date);
        System.out.println(openurl.getContentLength());
        System.out.println(openurl.getContentType());
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
                 
               }
               else
               {
                   in = new BufferedReader(new InputStreamReader(stream));
                   System.out.println("Error return data is------------------------------------------");
                   System.err.println(in.readLine());
               }
               e.printStackTrace();
           }
           finally
           {
               System.out.println(openurl.getContentLength());
               IOUtils.closeQuietly(in);
               IOUtils.closeQuietly(stream);
               openurl.disconnect();
           }
        }
        Assert.assertEquals(expectedStatusCode, returnCode);
    }
    
}
