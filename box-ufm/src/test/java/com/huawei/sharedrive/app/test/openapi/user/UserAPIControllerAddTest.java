package com.huawei.sharedrive.app.test.openapi.user;

import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

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
public class UserAPIControllerAddTest
{
    private static final String ADD_USER = "testData/users/addUser.txt";
    
    private static String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "users";
    
    private boolean showResult = true;
    
    /**
     * 创建错误邮箱用户
     * @throws Exception 
     */
    @Test
    public void testAddErrorEmailUsers() throws Exception
    {
        URL url = new URL(urlString);
        System.out.println(urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        openurl.setRequestMethod("POST");
        openurl.setRequestProperty("Content-type", "application/json");
        String dateStr = MyTestUtils.getDateString();
        openurl.setRequestProperty("Authorization", MyTestUtils.getAppAuthorization(dateStr));
        openurl.setRequestProperty("Date", dateStr);
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        String bodyStr = MyFileUtils.getDataFromFile(ADD_USER, "errorEmail");
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    /**
     * 创建错误存储区域用户
     * @throws Exception 
     */
    @Test
    public void testAddErrorRegionUsers() throws Exception
    {
        URL url = new URL(urlString);
        System.out.println(urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        openurl.setRequestMethod("POST");
        openurl.setRequestProperty("Content-type", "application/json");
        String dateStr = MyTestUtils.getDateString();
        openurl.setRequestProperty("Authorization", MyTestUtils.getAppAuthorization(dateStr));
        openurl.setRequestProperty("Date", dateStr);
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        String bodyStr = MyFileUtils.getDataFromFile(ADD_USER, "errorRegion");
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.NO_SUCH_REGION, showResult);
    }
    
    /**
     * 创建已存在用户
     * @throws Exception 
     */
    @Test
    public void testAddExistUsers() throws Exception
    {
        URL url = new URL(urlString);
        System.out.println(urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        openurl.setRequestMethod("POST");
        openurl.setRequestProperty("Content-type", "application/json");
        String dateStr = MyTestUtils.getDateString();
        openurl.setRequestProperty("Authorization", MyTestUtils.getAppAuthorization(dateStr));
        openurl.setRequestProperty("Date", dateStr);
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        String bodyStr = MyFileUtils.getDataFromFile(ADD_USER, "existUser");
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.CONFLICT_USER, showResult);
    }
    
    /**
     * 创建错误登录名用户
     * @throws Exception 
     */
    @Test
    public void testAddLongLoginname() throws Exception
    {
        URL url = new URL(urlString);
        System.out.println(urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        openurl.setRequestMethod("POST");
        openurl.setRequestProperty("Content-type", "application/json");
        String dateStr = MyTestUtils.getDateString();
        openurl.setRequestProperty("Authorization", MyTestUtils.getAppAuthorization(dateStr));
        openurl.setRequestProperty("Date", dateStr);
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        String bodyStr = MyFileUtils.getDataFromFile(ADD_USER, "longLoginName");
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    /**
     * 创建错误登录名用户
     * @throws Exception 
     */
    @Test
    public void testAddLongName() throws Exception
    {
        URL url = new URL(urlString);
        System.out.println(urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        openurl.setRequestMethod("POST");
        openurl.setRequestProperty("Content-type", "application/json");
        String dateStr = MyTestUtils.getDateString();
        openurl.setRequestProperty("Authorization", MyTestUtils.getAppAuthorization(dateStr));
        openurl.setRequestProperty("Date", dateStr);
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        String bodyStr = MyFileUtils.getDataFromFile(ADD_USER, "longName");
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    /**
     * 创建错误授权用户
     * @throws Exception 
     */
    @Test
    public void testBadAuthorUsers() throws Exception
    {
        
        URL url = new URL(urlString);
        System.out.println(urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        openurl.setRequestMethod("POST");
        openurl.setRequestProperty("Content-type", "application/json");
        String dateStr = MyTestUtils.getDateString();
        openurl.setRequestProperty("Authorization", MyTestUtils.getAppAuthorization(dateStr) + 1);
        openurl.setRequestProperty("Date", dateStr);
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        String bodyStr = MyFileUtils.getDataFromFile(ADD_USER, "successData");
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.TOKENUNAUTHORIZED, showResult);
    }
  
    /**
     * 创建成功用户
     * @throws Exception 
     */
    @Test
    public void testSuccessAddUser() throws Exception
    {
        
        URL url = new URL(urlString);
        System.out.println(urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        openurl.setRequestMethod("POST");
        openurl.setRequestProperty("Content-type", "application/json");
        String dateStr = MyTestUtils.getDateString();
        openurl.setRequestProperty("Authorization", MyTestUtils.getAppAuthorization(dateStr));
        openurl.setRequestProperty("Date", dateStr);
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        String bodyStr = MyFileUtils.getDataFromFile(ADD_USER, "successData");
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assert201(openurl, showResult);
    }
    
    @Test
    public void testAccountSuccessAddUser() throws Exception
    {
        
        URL url = new URL(urlString);
        System.out.println(urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        openurl.setRequestMethod("POST");
        openurl.setRequestProperty("Content-type", "application/json");
        String dateStr = MyTestUtils.getDateString();
        openurl.setRequestProperty("Authorization", MyTestUtils.getAppAuthorization(dateStr));
        openurl.setRequestProperty("Date", dateStr);
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        String bodyStr = MyFileUtils.getDataFromFile(ADD_USER, "successData");
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assert201(openurl, showResult);
    }
    
    @Test
    public void testSpaceQuoteNegative() throws Exception
    {

        URL url = new URL(urlString);
        System.out.println(urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        openurl.setRequestMethod("POST");
        openurl.setRequestProperty("Content-type", "application/json");
        String dateStr = MyTestUtils.getDateString();
        String authorization = MyTestUtils.getAccountAuthorization(dateStr);
        
        openurl.setRequestProperty("Authorization",authorization );
        openurl.setRequestProperty("Date", dateStr);
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        String bodyStr = MyFileUtils.getDataFromFile(ADD_USER, "negativeSpaceQuote");
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testDescription() throws Exception
    {

        URL url = new URL(urlString);
        System.out.println(urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        openurl.setRequestMethod("POST");
        openurl.setRequestProperty("Content-type", "application/json");
        String dateStr = MyTestUtils.getDateString();
        String authorization = MyTestUtils.getAccountAuthorization(dateStr);
        
        openurl.setRequestProperty("Authorization",authorization );
        openurl.setRequestProperty("Date", dateStr);
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        String bodyStr = MyFileUtils.getDataFromFile(ADD_USER, "testDescription");
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
}