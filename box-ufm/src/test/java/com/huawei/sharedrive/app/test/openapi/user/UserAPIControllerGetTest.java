package com.huawei.sharedrive.app.test.openapi.user;

import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

public class UserAPIControllerGetTest
{
    
    
    private boolean showResult = true;
    
    
    private String getUrl() throws Exception
    {
        return MyTestUtils.SERVER_URL_UFM_V2 + "users" + "/" + MyTestUtils.getTestCloudUserId1();
    }
    
    private String getNonexistUserUrl() throws Exception
    {
        return MyTestUtils.SERVER_URL_UFM_V2 + "users" + "/" + 1;
    }
    
    /**
     * 获取用户
     * @throws Exception 
     */
    @Test
    public void testBadAuthor() throws Exception
    {
        String urlString = getUrl();
        URL url = null;
        url = new URL(urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        openurl.setRequestMethod("GET");
        openurl.setRequestProperty("Content-type", "application/json");
        String dateStr = MyTestUtils.getDateString();
        openurl.setRequestProperty("Authorization", MyTestUtils.getAppAuthorization(dateStr) + 1);
        openurl.setRequestProperty("Date", dateStr);
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.TOKENUNAUTHORIZED,showResult);
    }
    
    /**
     * 获取用户
     * @throws Exception 
     */
    @Test
    public void testGetUsers() throws Exception
    {
        String urlString = getUrl();
        URL url = null;
        url = new URL(urlString);
        System.out.println(url);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        openurl.setRequestMethod("GET");
        openurl.setRequestProperty("Content-type", "application/json");
        String dateStr = MyTestUtils.getDateString();
        openurl.setRequestProperty("Authorization", MyTestUtils.getTestUserToken1());
        openurl.setRequestProperty("Date", dateStr);
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    /**
     * 获取用户
     * @throws Exception 
     */
    @Test
    public void testNonexistUser() throws Exception
    {
        String urlString = getNonexistUserUrl();
        System.out.println(urlString);
        URL url = null;
        url = new URL(urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        openurl.setRequestMethod("GET");
        openurl.setRequestProperty("Content-type", "application/json");
        String dateStr = MyTestUtils.getDateString();
        openurl.setRequestProperty("Authorization", MyTestUtils.getAppAuthorization(dateStr));
        openurl.setRequestProperty("Date", dateStr);
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.NO_SUCH_USER,showResult);
    }
    
}
