package pw.cdmi.box.disk.test.openapi.user;

import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

import pw.cdmi.box.disk.test.openapi.Constants;
import pw.cdmi.box.disk.test.utils.MyResponseUtils;
import pw.cdmi.box.disk.test.utils.UserRestTestUtils;
import pw.cdmi.core.exception.ErrorCode;

public class UserLoginV2Test
{
    private static final String TEST_NORMAL_USER = "testData/V2/testNormalLogin.txt";
    
    private static final String TEST_WRONG_USER = "testData/V2/testWrongLogin.txt";
    
    private static final String TEST_APPID_USER = "testData/V2/testAppIdLogin.txt";
    
    private static final String TEST_INVALID_PARAM_USER_1 = "testData/V2/testInvalidParamLogin1.txt";
    
    private static final String TEST_INVALID_PARAM_USER_2 = "testData/V2/testInvalidParamLogin2.txt";
    
    private static final String TEST_INVALID_PARAM_USER_3 = "testData/V2/testInvalidParamLogin2.txt";
    
    private static final String TEST_INVALID_PARAM_USER_4 = "testData/V2/testInvalidParamLogin2.txt";
    
    private static final boolean showResult = true;
    
    @Test
    public void testNormalLogin() throws Exception
    {
        String urlString = Constants.SERVER_RUL + "/api/v2/login";
        URL url = null;
        url = new URL(urlString);
        System.out.println("url is " + urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
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
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    @Test
    public void testWrongLogin() throws Exception
    {
        String urlString = Constants.SERVER_RUL + "/api/v2/login";
        URL url = null;
        url = new URL(urlString);
        System.out.println("url is " + urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
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
        String bodyStr = UserRestTestUtils.getDataFromFile(TEST_WRONG_USER);
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.LOGIN_UNAUTHORIZED, showResult);
    }
    
    @Test
    public void testUserLocked() throws Exception
    {
        String urlString = Constants.SERVER_RUL + "/api/v2/login";
        URL url = null;
        url = new URL(urlString);
        System.out.println("url is " + urlString);
        HttpURLConnection openurl = null;
        String bodyStr = "";
        openurl = (HttpURLConnection) url.openConnection();
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
        bodyStr = UserRestTestUtils.getDataFromFile(TEST_NORMAL_USER);
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assert200(openurl, showResult);
        for (int i = 0; i < 4; i++)
        {
            openurl = (HttpURLConnection) url.openConnection();
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
            bodyStr = UserRestTestUtils.getDataFromFile(TEST_WRONG_USER);
            openurl.getOutputStream().write(bodyStr.getBytes());
            MyResponseUtils.assertReturnCode(openurl, ErrorCode.LOGIN_UNAUTHORIZED, showResult);
        }
        openurl = (HttpURLConnection) url.openConnection();
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
        bodyStr = UserRestTestUtils.getDataFromFile(TEST_WRONG_USER);
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.USER_LOCKED, showResult);
    }
    @Test
    public void testUserLocked1() throws Exception
    {
        String urlString = Constants.SERVER_RUL + "/api/v2/login";
        URL url = null;
        url = new URL(urlString);
        System.out.println("url is " + urlString);
        HttpURLConnection openurl = null;
        String bodyStr = "";
        openurl = (HttpURLConnection) url.openConnection();
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
        bodyStr = UserRestTestUtils.getDataFromFile(TEST_NORMAL_USER);
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assert200(openurl, showResult);
        for (int i = 0; i < 4; i++)
        {
            openurl = (HttpURLConnection) url.openConnection();
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
            bodyStr = UserRestTestUtils.getDataFromFile(TEST_WRONG_USER);
            openurl.getOutputStream().write(bodyStr.getBytes());
            MyResponseUtils.assertReturnCode(openurl, ErrorCode.LOGIN_UNAUTHORIZED, showResult);
        }
        openurl = (HttpURLConnection) url.openConnection();
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
        bodyStr = UserRestTestUtils.getDataFromFile(TEST_NORMAL_USER);
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assert200(openurl, showResult);
    }
    @Test
    public void testAppIdLogin() throws Exception
    {
        String urlString = Constants.SERVER_RUL + "/api/v2/login";
        URL url = null;
        url = new URL(urlString);
        System.out.println("url is " + urlString);
        HttpURLConnection openurl = null;
        String bodyStr = "";
        openurl = (HttpURLConnection) url.openConnection();
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
        bodyStr = UserRestTestUtils.getDataFromFile(TEST_NORMAL_USER);
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assert200(openurl, showResult);
        
        openurl = (HttpURLConnection) url.openConnection();
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
        bodyStr = UserRestTestUtils.getDataFromFile(TEST_APPID_USER);
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assert200(openurl, showResult);
    }
    @Test
    public void testInvalidParamLogin() throws Exception
    {
        testPasswordParamLogin();
        testLoginNameParamLogin();
        testPasswordLengthLogin();
        testLoginNameLengthLogin();
    }
    
    private void testPasswordParamLogin() throws Exception
    {
        String urlString = Constants.SERVER_RUL + "/api/v2/login";
        URL url = null;
        url = new URL(urlString);
        System.out.println("url is " + urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
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
        String bodyStr = UserRestTestUtils.getDataFromFile(TEST_INVALID_PARAM_USER_1);
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMETER, showResult);
    }
    
    private void testLoginNameParamLogin() throws Exception
    {
        String urlString = Constants.SERVER_RUL + "/api/v2/login";
        URL url = null;
        url = new URL(urlString);
        System.out.println("url is " + urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
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
        String bodyStr = UserRestTestUtils.getDataFromFile(TEST_INVALID_PARAM_USER_2);
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMETER, showResult);
    }
    
    private void testPasswordLengthLogin() throws Exception
    {
        String urlString = Constants.SERVER_RUL + "/api/v2/login";
        URL url = null;
        url = new URL(urlString);
        System.out.println("url is " + urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
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
        String bodyStr = UserRestTestUtils.getDataFromFile(TEST_INVALID_PARAM_USER_3);
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMETER, showResult);
    }
    
    private void testLoginNameLengthLogin() throws Exception
    {
        String urlString = Constants.SERVER_RUL + "/api/v2/login";
        URL url = null;
        url = new URL(urlString);
        System.out.println("url is " + urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
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
        String bodyStr = UserRestTestUtils.getDataFromFile(TEST_INVALID_PARAM_USER_4);
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMETER, showResult);
    }
}
