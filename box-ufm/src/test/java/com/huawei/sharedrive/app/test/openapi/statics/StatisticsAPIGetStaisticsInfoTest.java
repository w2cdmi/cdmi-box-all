package com.huawei.sharedrive.app.test.openapi.statics;

import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

public class StatisticsAPIGetStaisticsInfoTest
{
    private static final String STATICS_USEDSPACE = "testData/statics/statisticsSpace.txt";
    
    @Test
    public void testTypeTotal() throws Exception
    {
        String urlString = buildUrl("Onebox");
        URL url = null;
        url = new URL(urlString);
        System.out.println("url is :" + url);
        String bodyStr = MyFileUtils.getDataFromFile(STATICS_USEDSPACE, "typeTotal");
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        openurl.setRequestMethod("POST");
        openurl.setRequestProperty("Content-type", "application/json");
        String dateStr = MyTestUtils.getDateString();
        openurl.setRequestProperty("Authorization", MyTestUtils.getAppAuthorization(dateStr));
        openurl.setRequestProperty("Date", dateStr);
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assert200(openurl, true);
    }
    
    @Test
    public void testTypeUser() throws Exception
    {
        String urlString = buildUrl("Onebox");
        URL url = null;
        url = new URL(urlString);
        System.out.println("url is :" + url);
        String bodyStr = MyFileUtils.getDataFromFile(STATICS_USEDSPACE, "typeUser");
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        openurl.setRequestMethod("POST");
        openurl.setRequestProperty("Content-type", "application/json");
        String dateStr = MyTestUtils.getDateString();
        openurl.setRequestProperty("Authorization", MyTestUtils.getAppAuthorization(dateStr));
        openurl.setRequestProperty("Date", dateStr);
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assert200(openurl, true);
    }
    
    @Test
    public void testTypeTeamSpace() throws Exception
    {
        String urlString = buildUrl("Onebox");
        URL url = null;
        url = new URL(urlString);
        System.out.println("url is :" + url);
        String bodyStr = MyFileUtils.getDataFromFile(STATICS_USEDSPACE, "typeTeamSpace");
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        openurl.setRequestMethod("POST");
        openurl.setRequestProperty("Content-type", "application/json");
        String dateStr = MyTestUtils.getDateString();
        openurl.setRequestProperty("Authorization", MyTestUtils.getAppAuthorization(dateStr));
        openurl.setRequestProperty("Date", dateStr);
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assert200(openurl, true);
    }
    
    @Test
    public void testErrorType() throws Exception
    {
        String urlString = buildUrl("Onebox");
        URL url = null;
        url = new URL(urlString);
        System.out.println("url is :" + url);
        String bodyStr = MyFileUtils.getDataFromFile(STATICS_USEDSPACE, "errorType");
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        openurl.setRequestMethod("POST");
        openurl.setRequestProperty("Content-type", "application/json");
        String dateStr = MyTestUtils.getDateString();
        openurl.setRequestProperty("Authorization", MyTestUtils.getAppAuthorization(dateStr));
        openurl.setRequestProperty("Date", dateStr);
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, true);
    }
    
    @Test
    public void testTypeNull() throws Exception
    {
        String urlString = buildUrl("Onebox");
        URL url = null;
        url = new URL(urlString);
        System.out.println("url is :" + url);
        String bodyStr = MyFileUtils.getDataFromFile(STATICS_USEDSPACE, "typeNull");
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        openurl.setRequestMethod("POST");
        openurl.setRequestProperty("Content-type", "application/json");
        String dateStr = MyTestUtils.getDateString();
        openurl.setRequestProperty("Authorization", MyTestUtils.getAppAuthorization(dateStr));
        openurl.setRequestProperty("Date", dateStr);
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, true);
    }
    
    @Test
    public void testUnAuthorized() throws Exception
    {
        String urlString = buildUrl("Onebox");
        URL url = null;
        url = new URL(urlString);
        System.out.println("url is :" + url);
        String bodyStr = MyFileUtils.getDataFromFile(STATICS_USEDSPACE, "typeTeamSpace");
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        openurl.setRequestMethod("POST");
        openurl.setRequestProperty("Content-type", "application/json");
        String dateStr = MyTestUtils.getDateString();
        openurl.setRequestProperty("Authorization", MyTestUtils.getAppAuthorization(dateStr) + "sdsfsdf");
        openurl.setRequestProperty("Date", dateStr);
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.TOKENUNAUTHORIZED, true);
    }
    
    @Test
    public void testErrorAppId() throws Exception
    {
        String urlString = buildUrl("ErrorAppId");
        URL url = null;
        url = new URL(urlString);
        System.out.println("url is :" + url);
        String bodyStr = MyFileUtils.getDataFromFile(STATICS_USEDSPACE, "typeTotal");
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        openurl.setRequestMethod("POST");
        openurl.setRequestProperty("Content-type", "application/json");
        String dateStr = MyTestUtils.getDateString();
        openurl.setRequestProperty("Authorization", MyTestUtils.getAppAuthorization(dateStr));
        openurl.setRequestProperty("Date", dateStr);
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.TOKENUNAUTHORIZED, true);
    }
    
    private String buildUrl(String appId)
    {
        return MyTestUtils.SERVER_URL_UFM_V2 + "statistics/" +appId +"/info";
    }
}
