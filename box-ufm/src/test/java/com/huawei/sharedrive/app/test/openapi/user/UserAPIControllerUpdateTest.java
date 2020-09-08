package com.huawei.sharedrive.app.test.openapi.user;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

/**
 * 创建存储用户测试类
 * 
 * @author l90003768
 *
 */
public class UserAPIControllerUpdateTest
{
    private static final String UPDATE_USER = "testData/users/updateUser.txt";
    
    private boolean showResult = true;
    
    private String getNoRegionIdUserUrl() throws Exception
    {
        return MyTestUtils.SERVER_URL_UFM_V2 + "users" + "/" + 1846;
    }
    
    private String getUser1Url() throws Exception
    {
        return MyTestUtils.SERVER_URL_UFM_V2 + "users" + "/" + MyTestUtils.getTestCloudUserId1();
    }
    
    private String getUser2Url() throws Exception
    {
        return MyTestUtils.SERVER_URL_UFM_V2 + "users" + "/" + 45;
    }
    
    /**
     * 测试用户名过长的用户
     * @throws Exception
     */
    @Test
    public void testLongLoginName() throws Exception
    {
        String urlString = getUser1Url();
        URL url = new URL(urlString);
        System.out.println(urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        openurl.setRequestMethod("PUT");
        openurl.setRequestProperty("Content-type", "application/json");
        String dateStr = MyTestUtils.getDateString();
        openurl.setRequestProperty("Authorization", MyTestUtils.getAppAuthorization(dateStr));
        openurl.setRequestProperty("Date", dateStr);
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        String bodyStr = MyFileUtils.getDataFromFile(UPDATE_USER, "longLoginName");
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    /**
     * 测试存在的用户
     * @throws Exception
     */
    @Test
    public void testSuccessUpdateUser() throws Exception
    {
        String urlString = getUser1Url();
        URL url = new URL(urlString);
        System.out.println(urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        openurl.setRequestMethod("PUT");
        openurl.setRequestProperty("Content-type", "application/json");
        String dateStr = MyTestUtils.getDateString();
        openurl.setRequestProperty("Authorization", MyTestUtils.getAccountAuthorization(dateStr));
        openurl.setRequestProperty("Date", dateStr);
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        String bodyStr = MyFileUtils.getDataFromFile(UPDATE_USER, "successData");
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    /**
     * 测试不存在的用户
     * @throws Exception
     */
    @Test
    public void testNonexistUser() throws Exception
    {
        String urlString = getUser2Url();
        URL url = new URL(urlString);
        System.out.println(urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        openurl.setRequestMethod("PUT");
        openurl.setRequestProperty("Content-type", "application/json");
        String dateStr = MyTestUtils.getDateString();
        openurl.setRequestProperty("Authorization", MyTestUtils.getAppAuthorization(dateStr));
        openurl.setRequestProperty("Date", dateStr);
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        String bodyStr = MyFileUtils.getDataFromFile(UPDATE_USER, "successData");
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.NO_SUCH_USER, showResult);
    }
    
    /**
     * 测试存在的用户
     * @throws Exception
     */
    @Test
    public void testExistUser() throws Exception
    {
        String urlString = getUser1Url();
        URL url = new URL(urlString);
        System.out.println(urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        openurl.setRequestMethod("PUT");
        openurl.setRequestProperty("Content-type", "application/json");
        String dateStr = MyTestUtils.getDateString();
        openurl.setRequestProperty("Authorization", MyTestUtils.getAppAuthorization(dateStr));
        openurl.setRequestProperty("Date", dateStr);
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        String bodyStr = MyFileUtils.getDataFromFile(UPDATE_USER, "existUser");
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.CONFLICT_USER, showResult);
    }
    
    /**
     * 测试授权码错误
     * @throws Exception
     */
    @Test
    public void testBadAuthorUser() throws Exception
    {
        String urlString = getUser1Url();
        URL url = new URL(urlString);
        System.out.println(urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        openurl.setRequestMethod("PUT");
        openurl.setRequestProperty("Content-type", "application/json");
        String dateStr = MyTestUtils.getDateString();
        openurl.setRequestProperty("Authorization", MyTestUtils.getAppAuthorization(dateStr) + 1);
        openurl.setRequestProperty("Date", dateStr);
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        String bodyStr = MyFileUtils.getDataFromFile(UPDATE_USER, "successData");
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.TOKENUNAUTHORIZED, showResult);
    }
    
    /**
     * 日期过期测试
     * @throws Exception
     */
    @Test
    public void testInvalidDate() throws Exception
    {
        String urlString = getUser1Url();
        URL url = new URL(urlString);
        System.out.println(urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        openurl.setRequestMethod("PUT");
        openurl.setRequestProperty("Content-type", "application/json");
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DAY_OF_WEEK, -1);
        String dateStr = MyTestUtils.getDateString(date);
        openurl.setRequestProperty("Authorization", MyTestUtils.getAppAuthorization(dateStr));
        openurl.setRequestProperty("Date", dateStr);
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        String bodyStr = MyFileUtils.getDataFromFile(UPDATE_USER, "successData");
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.TOKENUNAUTHORIZED, showResult);
    }
    
    /**
     * 存储区域成功测试
     * @throws Exception
     */
    @Test
    public void testChangeRegion() throws Exception
    {
        String urlString = getNoRegionIdUserUrl();
        URL url = new URL(urlString);
        System.out.println(urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        openurl.setRequestMethod("PUT");
        openurl.setRequestProperty("Content-type", "application/json");
        Calendar date = Calendar.getInstance();
        String dateStr = MyTestUtils.getDateString(date);
        openurl.setRequestProperty("Authorization", MyTestUtils.getAppAuthorization(dateStr));
        openurl.setRequestProperty("Date", dateStr);
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        String bodyStr = MyFileUtils.getDataFromFile(UPDATE_USER, "changeRegionSuccess");
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    
    /**
     * 存储区域成功测试
     * @throws Exception
     */
    @Test
    public void testInvalidRegion() throws Exception
    {
        String urlString = getNoRegionIdUserUrl();
        URL url = new URL(urlString);
        System.out.println(urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        openurl.setRequestMethod("PUT");
        openurl.setRequestProperty("Content-type", "application/json");
        Calendar date = Calendar.getInstance();
        String dateStr = MyTestUtils.getDateString(date);
        openurl.setRequestProperty("Authorization", MyTestUtils.getAppAuthorization(dateStr));
        openurl.setRequestProperty("Date", dateStr);
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        String bodyStr = MyFileUtils.getDataFromFile(UPDATE_USER, "invalidRegion");
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.NO_SUCH_REGION, showResult);
    }
    
    /**
     * 存储区域错误测试
     * @throws Exception
     */
    @Test
    public void testChangeConflictRegion() throws Exception
    {
        String urlString = getUser1Url();
        URL url = new URL(urlString);
        System.out.println(urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        openurl.setRequestMethod("PUT");
        openurl.setRequestProperty("Content-type", "application/json");
        Calendar date = Calendar.getInstance();
        String dateStr = MyTestUtils.getDateString(date);
        openurl.setRequestProperty("Authorization", MyTestUtils.getAppAuthorization(dateStr));
        openurl.setRequestProperty("Date", dateStr);
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        String bodyStr = MyFileUtils.getDataFromFile(UPDATE_USER, "changeConflictRegion");
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.CONFLICT_REGION, showResult);
    }
  
    @Test
    public void testNoLoginNameAndName() throws Exception
    {
        String urlString = getUser2Url();
        URL url = new URL(urlString);
        System.out.println(urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        openurl.setRequestMethod("PUT");
        openurl.setRequestProperty("Content-type", "application/json");
        Calendar date = Calendar.getInstance();
        String dateStr = MyTestUtils.getDateString(date);
        openurl.setRequestProperty("Authorization", MyTestUtils.getAppAuthorization(dateStr));
        openurl.setRequestProperty("Date", dateStr);
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        String bodyStr = MyFileUtils.getDataFromFile(UPDATE_USER, "noLoginNameAndName");
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    @Test
    public void testNoLoginName() throws Exception
    {
        String urlString = getUser2Url();
        URL url = new URL(urlString);
        System.out.println(urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        openurl.setRequestMethod("PUT");
        openurl.setRequestProperty("Content-type", "application/json");
        Calendar date = Calendar.getInstance();
        String dateStr = MyTestUtils.getDateString(date);
        openurl.setRequestProperty("Authorization", MyTestUtils.getAppAuthorization(dateStr));
        openurl.setRequestProperty("Date", dateStr);
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        String bodyStr = MyFileUtils.getDataFromFile(UPDATE_USER, "noLoginName");
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    
}
