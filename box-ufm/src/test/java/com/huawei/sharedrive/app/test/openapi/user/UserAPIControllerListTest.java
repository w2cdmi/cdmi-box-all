package com.huawei.sharedrive.app.test.openapi.user;

import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

public class UserAPIControllerListTest
{
    private static final String DATA_LIST_USER="testData/users/userList.txt";
    
    @Test
    public void TestNormal() throws Exception
    {
        String urlString = buildUrl();
        URL url = null;
        url = new URL(urlString);
        String bodyStr = MyFileUtils.getDataFromFile(DATA_LIST_USER, "normal");
        System.out.println(bodyStr);
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
    public void NoBody() throws Exception
    {
        String urlString = buildUrl();
        URL url = null;
        url = new URL(urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        openurl.setRequestMethod("POST");
        openurl.setRequestProperty("Content-type", "application/json");
        String dateStr = MyTestUtils.getDateString();
        openurl.setRequestProperty("Authorization", MyTestUtils.getAppAuthorization(dateStr));
        openurl.setRequestProperty("Date", dateStr);
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        MyResponseUtils.assert200(openurl, true);
    }
    
    @Test
    public void UnAuthorization() throws Exception
    {
        String urlString = buildUrl();
        URL url = null;
        url = new URL(urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        openurl.setRequestMethod("POST");
        openurl.setRequestProperty("Content-type", "application/json");
        String dateStr = MyTestUtils.getDateString();
        openurl.setRequestProperty("Authorization", MyTestUtils.getAppAuthorization(dateStr) + 1);
        openurl.setRequestProperty("Date", dateStr);
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.TOKENUNAUTHORIZED,true);
    }
    
    @Test
    public void LimitNegative() throws Exception
    {
        String urlString = buildUrl();
        URL url = null;
        url = new URL(urlString);
        String bodyStr = MyFileUtils.getDataFromFile(DATA_LIST_USER, "limitNegative");
        System.out.println(bodyStr);
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
    public void LimitOver1000() throws Exception
    {
        String urlString = buildUrl();
        URL url = null;
        url = new URL(urlString);
        String bodyStr = MyFileUtils.getDataFromFile(DATA_LIST_USER, "limitOver1000");
        System.out.println(bodyStr);
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
    public void OffsetNegative() throws Exception
    {
        String urlString = buildUrl();
        URL url = null;
        url = new URL(urlString);
        String bodyStr = MyFileUtils.getDataFromFile(DATA_LIST_USER, "offsetNegative");
        System.out.println(bodyStr);
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
    public void NoContainDirect() throws Exception
    {
        String urlString = buildUrl();
        URL url = null;
        url = new URL(urlString);
        String bodyStr = MyFileUtils.getDataFromFile(DATA_LIST_USER, "noContainDirect");
        System.out.println(bodyStr);
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
    public void NoContainField() throws Exception
    {
        String urlString = buildUrl();
        URL url = null;
        url = new URL(urlString);
        String bodyStr = MyFileUtils.getDataFromFile(DATA_LIST_USER, "noContainField");
        System.out.println(bodyStr);
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
    
    private String buildUrl()
    {
        return MyTestUtils.SERVER_URL_UFM_V2 + "users/items";
    }
}
