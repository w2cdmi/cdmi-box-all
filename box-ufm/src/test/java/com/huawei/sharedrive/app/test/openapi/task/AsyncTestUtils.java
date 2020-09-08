package com.huawei.sharedrive.app.test.openapi.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

import com.huawei.sharedrive.app.openapi.domain.task.ResponseGetTask;
import com.huawei.sharedrive.app.openapi.restv2.task.status.AsyncTaskStatus;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;
import com.huawei.sharedrive.app.test.openapi.common.RestException;
import com.huawei.sharedrive.app.test.openapi.share.JSonUtils;

import junit.framework.Assert;
import pw.cdmi.core.utils.DateUtils;

public class AsyncTestUtils
{
    
    /**
     * 循环获取测试结果
     * 
     * @param urlString
     * @param expectedStr
     * @throws MalformedURLException
     * @throws IOException
     * @throws ProtocolException
     * @throws Exception
     * @throws InterruptedException
     */
    public static void loopGetTestResult(String urlString, String expectedStr, boolean showResult)
        throws MalformedURLException, IOException, ProtocolException, Exception, InterruptedException
    {
        boolean isContinue = true;
        while (isContinue)
        {
            URL url = new URL(urlString);
            System.out.println("url is " + urlString);
            HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestMethod("GET");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.setRequestProperty("Authorization", MyTestUtils.getTestUserToken1());
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            openurl.connect();
            isContinue = AsyncTestUtils.assertStatus(openurl, expectedStr, showResult);
            if (isContinue)
            {
                Thread.currentThread();
                Thread.sleep(1 * 1000);
            }
        }
    }
    
    /**
     * 判断状态是否期望状态 * @param openurl
     * 
     * @param expectedStatus
     * @return true 表示任务还在执行，false表示任务执行完毕
     * @throws Exception
     */
    public static boolean assertStatus(HttpURLConnection openurl, String expectedStatus, boolean showResult)
        throws Exception
    {
        ResponseGetTask resp = getResult(openurl,expectedStatus, showResult);
        if(resp!= null)
        {
            if (resp.getStatus().equalsIgnoreCase(AsyncTaskStatus.DOING))
            {
                System.out.println("Task is doing, please waiting..."
                    + DateUtils.format(new Date(), DateUtils.DATE_FORMAT_MS_PATTERN));
                return true;
            }
            Assert.assertEquals(resp.getStatus(), expectedStatus);
        }
        return false;
    }
    
    public static String addTask(String urlString, String fileName, String dataTag, boolean showResult)
        throws Exception
    {
        URL url = new URL(urlString);
        System.out.println("url is " + urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        openurl.setRequestMethod("PUT");
        openurl.setRequestProperty("Content-type", "application/json");
        openurl.setRequestProperty("Authorization", MyTestUtils.getTestUserToken1());
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        
        String bodyStr = MyFileUtils.getDataFromFile(fileName, dataTag);
        
        openurl.getOutputStream().write(bodyStr.getBytes());
        String rspStr = MyResponseUtils.getSuccessResponseString(openurl);
        if (showResult)
        {
            System.out.println(rspStr);
        }
        if (rspStr.contains("\"id\"") && rspStr.contains("\"type\""))
        {
            int idPos = rspStr.indexOf("\"id\"");
            int typePos = rspStr.indexOf("\"type\"");
            String temp = rspStr.substring(idPos + "\"id\"".length() + 2, typePos - 2);
            return temp;
        }
        return null;
    }
    
    public static String addTask(String urlString, String bodyStr, boolean showResult, String token ,String date)
        throws Exception
    {
        URL url = new URL(urlString);
        System.out.println("url is " + urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        openurl.setRequestMethod("PUT");
        openurl.setRequestProperty("Content-type", "application/json");
        openurl.setRequestProperty("Authorization", token);
        if(date != null)
        {
            openurl.setRequestProperty("Date", date);
        }
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        
        openurl.getOutputStream().write(bodyStr.getBytes());
        String rspStr = MyResponseUtils.getSuccessResponseString(openurl);
        if (showResult)
        {
            System.out.println(rspStr);
        }
        if (rspStr.contains("\"id\"") && rspStr.contains("\"type\""))
        {
            int idPos = rspStr.indexOf("\"id\"");
            int typePos = rspStr.indexOf("\"type\"");
            String temp = rspStr.substring(idPos + "\"id\"".length() + 2, typePos - 2);
            return temp;
        }
        return null;
    }
    
    public static String addTask(String urlString, String fileName, String dataTag, boolean showResult,
        Calendar date, String plainAccessKey) throws Exception
    {
        URL url = new URL(urlString);
        System.out.println("url is " + urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        String dateStr = MyTestUtils.getDateString(date);
        String signature = MyTestUtils.getLinkSignature(plainAccessKey, dateStr);
        openurl.setRequestMethod("PUT");
        openurl.setRequestProperty("Content-type", "application/json");
        openurl.setRequestProperty("Date", dateStr);
        openurl.setRequestProperty("Authorization", MyTestUtils.getTestUserToken1());
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        
        String bodyStr = MyFileUtils.getDataFromFile(fileName, dataTag);
        bodyStr = bodyStr.replace("$plainAccessCode", signature);
        openurl.getOutputStream().write(bodyStr.getBytes());
        String rspStr = MyResponseUtils.getSuccessResponseString(openurl);
        if (showResult)
        {
            System.out.println(rspStr);
        }
        if (rspStr.contains("\"id\"") && rspStr.contains("\"type\""))
        {
            int idPos = rspStr.indexOf("\"id\"");
            int typePos = rspStr.indexOf("\"type\"");
            String temp = rspStr.substring(idPos + "\"id\"".length() + 2, typePos - 2);
            return temp;
        }
        return null;
    }
    
    private static ResponseGetTask getResult(HttpURLConnection openurl,String expectedStatus, boolean showResult) throws Exception
    {
        InputStream stream = null;
        BufferedReader in = null;
        String result = null;
        String returnCode = null;
        try
        {
            stream = openurl.getInputStream();
            in = new BufferedReader(new InputStreamReader(stream));
            String returnData = in.readLine();
            RestException restException = JSonUtils.stringToObject(returnData, RestException.class);
            if (null != restException)
            {
                returnCode = restException.getCode();
                if (showResult)
                {
                    System.out.println("Return:"+returnData);
                }
                Assert.assertEquals(expectedStatus, returnCode);
                
                return null;
            }
        }
        catch (Exception e)
        {
            stream = openurl.getErrorStream();
            if (null == stream)
            {
                if (showResult)
                {
                    System.err.println(e.getMessage());
                    return null;
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
                    Assert.assertEquals(expectedStatus, returnCode);
                    return null;
                }
            }
        }
        return JSonUtils.stringToObject(result, ResponseGetTask.class);
    }
}
