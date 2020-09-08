package com.huawei.sharedrive.uam.rest.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.apache.http.util.TextUtils;
import org.junit.Test;

import com.huawei.sharedrive.uam.exception.ErrorCode;
import com.huawei.sharedrive.uam.openapi.domain.RestPageWxMpRequest;
import com.huawei.sharedrive.uam.openapi.domain.RestTokenCreateResponse;
import com.huawei.sharedrive.uam.openapi.domain.RestTokenRefreshResponse;
import com.huawei.sharedrive.uam.openapi.domain.RestUserLoginCreateRequest;
import com.huawei.sharedrive.uam.rest.test.util.Constants;
import com.huawei.sharedrive.uam.rest.test.util.MyResponseUtils;
import com.huawei.sharedrive.uam.rest.test.util.MyTestUtils;
import com.huawei.sharedrive.uam.test.AbstractSpringTest;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.utils.JsonUtils;

public class TokenAPIControllerTest 
{
    private static final String TEST_NORMAL_USER = "testData/testNormalLogin.txt";
    
    private static final String TEST_WRONG_USER = "testData/testWrongLogin.txt";
    
    private static final String TEST_APPID_USER = "testData/testAppIdLogin.txt";
    
    private static final String TEST_INVALID_PARAM_USER_1 = "testData/testInvalidParamLogin1.txt";
    
    private static final String TEST_INVALID_PARAM_USER_2 = "testData/testInvalidParamLogin2.txt";
    
    private static final String TEST_INVALID_PARAM_USER_3 = "testData/testInvalidParamLogin2.txt";
    
    private static final String TEST_INVALID_PARAM_USER_4 = "testData/testInvalidParamLogin2.txt";
    
    private static final boolean showResult = true;
    
	
    
    @Test
    public void testNormalLogin() throws Exception
    {
        
        String urlString = Constants.SERVER_RUL + "/api/v2/token";
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
        }
        finally
        {
            openurl.disconnect();
        }
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    @Test
    public void testWrongLogin() throws Exception
    {
        String urlString = Constants.SERVER_RUL + "/api/v2/token";
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
            String bodyStr = UserRestTestUtils.getDataFromFile(TEST_WRONG_USER);
            openurl.getOutputStream().write(bodyStr.getBytes());
        }
        finally
        {
            openurl.disconnect();
        }
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.LOGINUNAUTHORIZED, showResult);
    }
    
    @Test
    public void testLock() throws Exception
    {
        testNormalLogin();
        // 错误登录5次
        for (int i = 0; i < 5; i++)
        {
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        testWrongLogin();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        Thread.sleep(2000);
        testUserLocked();
        Thread.sleep(330000);
        // 正常登录
        testNormalLogin();
    }
    
    @Test
    public void testLock2() throws Exception
    {
        testNormalLogin();
        // 错误登录4次
        for (int i = 0; i < 4; i++)
        {
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        testWrongLogin();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        Thread.sleep(330000);
        // 再错误登录一次
        testWrongLogin();
        // 正常登录
        testNormalLogin();
    }
    
    @Test
    public void testUserLocked() throws Exception
    {
        String urlString = Constants.SERVER_RUL + "/api/v2/token";
        URL url = null;
        url = new URL(urlString);
        System.out.println("url is " + urlString);
        HttpURLConnection openurl = null;
        String bodyStr = "";
        // 正常登录
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
        // MyResponseUtils.assert200(openurl, showResult);
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.USERLOCKED, showResult);
    }
    
    @Test
    public void testUserLocked1() throws Exception
    {
        String urlString = Constants.SERVER_RUL + "/api/v2/token";
        URL url = null;
        url = new URL(urlString);
        System.out.println("url is " + urlString);
        HttpURLConnection openurl = null;
        String bodyStr = "";
        // 正常登录
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
        // 错误登录4次
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
            MyResponseUtils.assertReturnCode(openurl, ErrorCode.LOGINUNAUTHORIZED, showResult);
        }
        // 第5次正常登录
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
    
    /**
     * 测试错误的客户端类型
     * 
     * @throws Exception
     */
    @Test
    public void testInvalidDeviceType() throws Exception
    {
        String urlString = Constants.SERVER_RUL + "/api/v2/token";
        URL url = null;
        url = new URL(urlString);
        System.out.println("url is " + urlString);
        HttpURLConnection openurl = null;
        String bodyStr = "";
        try
        {
            openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestProperty("x-device-type", "xxx");
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
        }
        finally
        {
            openurl.disconnect();
        }
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    /**
     * 测试空的客户端类型
     * 
     * @throws Exception
     */
    @Test
    public void testInvalidDeviceType1() throws Exception
    {
        String urlString = Constants.SERVER_RUL + "/api/v2/token";
        URL url = null;
        url = new URL(urlString);
        System.out.println("url is " + urlString);
        HttpURLConnection openurl = null;
        String bodyStr = "";
        openurl = (HttpURLConnection) url.openConnection();
        try
        {
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
        }
        finally
        {
            openurl.disconnect();
        }
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    /**
     * 测试空的device os
     * 
     * @throws Exception
     */
    @Test
    public void testInvalidDeviceOS() throws Exception
    {
        String urlString = Constants.SERVER_RUL + "/api/v2/token";
        URL url = null;
        url = new URL(urlString);
        System.out.println("url is " + urlString);
        HttpURLConnection openurl = null;
        String bodyStr = "";
        openurl = (HttpURLConnection) url.openConnection();
        try
        {
            openurl.setRequestProperty("x-device-type", "ios");
            openurl.setRequestProperty("x-device-sn", "111");
            openurl.setRequestProperty("x-device-name", "xiaomi-one");
            openurl.setRequestProperty("x-client-version", "V1.1");
            openurl.setRequestMethod("POST");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            openurl.connect();
            bodyStr = UserRestTestUtils.getDataFromFile(TEST_NORMAL_USER);
            openurl.getOutputStream().write(bodyStr.getBytes());
        }
        finally
        {
            openurl.disconnect();
        }
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    /**
     * 测试空的device name
     * 
     * @throws Exception
     */
    @Test
    public void testInvalidDeviceName() throws Exception
    {
        String urlString = Constants.SERVER_RUL + "/api/v2/token";
        URL url = null;
        url = new URL(urlString);
        System.out.println("url is " + urlString);
        HttpURLConnection openurl = null;
        String bodyStr = "";
        openurl = (HttpURLConnection) url.openConnection();
        try
        {
            openurl.setRequestProperty("x-device-type", "ios");
            openurl.setRequestProperty("x-device-sn", "111");
            openurl.setRequestProperty("x-device-os", "MIUI 2.3.5");
            openurl.setRequestProperty("x-client-version", "V1.1");
            openurl.setRequestMethod("POST");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            openurl.connect();
            bodyStr = UserRestTestUtils.getDataFromFile(TEST_NORMAL_USER);
            openurl.getOutputStream().write(bodyStr.getBytes());
        }
        finally
        {
            openurl.disconnect();
        }
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    /**
     * 测试空的client version
     * 
     * @throws Exception
     */
    @Test
    public void testInvalidClientVersion() throws Exception
    {
        String urlString = Constants.SERVER_RUL + "/api/v2/token";
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
            openurl.setRequestMethod("POST");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            openurl.connect();
            String bodyStr = UserRestTestUtils.getDataFromFile(TEST_NORMAL_USER);
            openurl.getOutputStream().write(bodyStr.getBytes());
        }
        finally
        {
            openurl.disconnect();
        }
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    /**
     * 测试 两个不同appId的用户登录
     * 
     * @throws Exception
     */
    @Test
    public void testAppIdLogin() throws Exception
    {
        String urlString = Constants.SERVER_RUL + "/api/v2/token";
        URL url = null;
        url = new URL(urlString);
        System.out.println("url is " + urlString);
        HttpURLConnection openurl = null;
        String bodyStr = "";
        try
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
        }
        finally
        {
            openurl.disconnect();
        }
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
    
    /**
     * 测试正常的刷新token
     * 
     * @throws Exception
     */
    @Test
    public void testNormalRefreshToken() throws Exception
    {
        // 登录
        RestTokenCreateResponse restTokenCreateResponse = MyTestUtils.login();
        restTokenCreateResponse.setRefreshToken("OneBox/c08560d7a896091b061a88f22bbe7785");
        restTokenCreateResponse.setToken("OneBox/d0e8da893a36cb88e5befda73fd78ff3");
        String urlString = Constants.SERVER_RUL + "/api/v2/token";
        URL url = null;
        url = new URL(urlString);
        System.out.println("url is " + urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        try
        {
            openurl.setRequestProperty("Authorization", restTokenCreateResponse.getRefreshToken());
            openurl.setRequestMethod("PUT");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.connect();
        }
        finally
        {
            openurl.disconnect();
        }
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    /**
     * 测试错误的刷新token
     * 
     * @throws Exception
     */
    @Test
    public void testErrorRefreshToken() throws Exception
    {
        // 登录
        RestTokenCreateResponse restTokenCreateResponse = MyTestUtils.login();
        
        String urlString = Constants.SERVER_RUL + "/api/v2/token";
        URL url = null;
        url = new URL(urlString);
        System.out.println("url is " + urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        try
        {
            openurl.setRequestProperty("Authorization", restTokenCreateResponse.getToken());
            openurl.setRequestMethod("PUT");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.connect();
        }
        finally
        {
            openurl.disconnect();
        }
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.LOGINUNAUTHORIZED, showResult);
    }
    
    /**
     * 测试正常的删除token
     * 
     * @throws Exception
     */
    @Test
    public void testNormalDelToken() throws Exception
    {
        // 登录
        RestTokenCreateResponse restTokenCreateResponse = MyTestUtils.login();
        
        String urlString = Constants.SERVER_RUL + "/api/v2/token";
        URL url = null;
        url = new URL(urlString);
        System.out.println("url is " + urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        try
        {
            openurl.setRequestProperty("Authorization", restTokenCreateResponse.getToken());
            openurl.setRequestMethod("DELETE");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.connect();
        }
        finally
        {
            openurl.disconnect();
        }
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    /**
     * 测试错误的删除token
     * 
     * @throws Exception
     */
    @Test
    public void testErrorlDelToken() throws Exception
    {
        // 登录
        RestTokenCreateResponse restTokenCreateResponse = MyTestUtils.login();
        
        String urlString = Constants.SERVER_RUL + "/api/v2/token";
        URL url = null;
        url = new URL(urlString);
        System.out.println("url is " + urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        try
        {
            openurl.setRequestProperty("Authorization", restTokenCreateResponse.getToken());
            openurl.setRequestMethod("DELETE");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.connect();
        }
        finally
        {
            openurl.disconnect();
        }
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.LOGINUNAUTHORIZED, showResult);
    }
    
    /**
     * 登录成功后，刷新token，再使用刷新后的token删除token
     * 
     * @throws Exception
     */
    @Test
    public void testNormalRefreshDelToken() throws Exception
    {
        // 登录
        RestTokenCreateResponse restTokenCreateResponse = MyTestUtils.login();
        // 刷新token
        RestTokenRefreshResponse restTokenRefreshResponse = MyTestUtils.refreshToken(restTokenCreateResponse.getRefreshToken());
        
        String urlString = Constants.SERVER_RUL + "/api/v2/token";
        URL url = null;
        url = new URL(urlString);
        System.out.println("url is " + urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        try
        {
            openurl.setRequestProperty("Authorization", restTokenRefreshResponse.getToken());
            openurl.setRequestMethod("DELETE");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.connect();
        }
        finally
        {
            openurl.disconnect();
        }
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    /**
     * 登录成功后，刷新token，再使用刷新前的token删除token
     * 
     * @throws Exception
     */
    @Test
    public void testErrorRefreshDelToken() throws Exception
    {
        // 登录
        RestTokenCreateResponse restTokenCreateResponse = MyTestUtils.login();
        // 刷新token
        MyTestUtils.refreshToken(restTokenCreateResponse.getRefreshToken());
        
        String urlString = Constants.SERVER_RUL + "/api/v2/token";
        URL url = null;
        url = new URL(urlString);
        System.out.println("url is " + urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        try
        {
            openurl.setRequestProperty("Authorization", restTokenCreateResponse.getToken());
            openurl.setRequestMethod("DELETE");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.connect();
        }
        finally
        {
            openurl.disconnect();
        }
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.LOGINUNAUTHORIZED, showResult);
    }
    
    /**
     * 密码为空校验
     * 
     * @throws Exception
     */
    private void testPasswordParamLogin() throws Exception
    {
        String urlString = Constants.SERVER_RUL + "/api/v2/token";
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
            String bodyStr = UserRestTestUtils.getDataFromFile(TEST_INVALID_PARAM_USER_1);
            openurl.getOutputStream().write(bodyStr.getBytes());
        }
        finally
        {
            openurl.disconnect();
        }
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    /**
     * 用户名为空校验
     * 
     * @throws Exception
     */
    private void testLoginNameParamLogin() throws Exception
    {
        String urlString = Constants.SERVER_RUL + "/api/v2/token";
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
            String bodyStr = UserRestTestUtils.getDataFromFile(TEST_INVALID_PARAM_USER_2);
            openurl.getOutputStream().write(bodyStr.getBytes());
        }
        finally
        {
            openurl.disconnect();
        }
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    /**
     * 密码超长校验
     * 
     * @throws Exception
     */
    private void testPasswordLengthLogin() throws Exception
    {
        String urlString = Constants.SERVER_RUL + "/api/v2/token";
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
            String bodyStr = UserRestTestUtils.getDataFromFile(TEST_INVALID_PARAM_USER_3);
            openurl.getOutputStream().write(bodyStr.getBytes());
        }
        finally
        {
            openurl.disconnect();
        }
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    /**
     * 用户超长校验
     * 
     * @throws Exception
     */
    private void testLoginNameLengthLogin() throws Exception
    {
        String urlString = Constants.SERVER_RUL + "/api/v2/token";
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
            String bodyStr = UserRestTestUtils.getDataFromFile(TEST_INVALID_PARAM_USER_4);
            openurl.getOutputStream().write(bodyStr.getBytes());
        }
        finally
        {
            openurl.disconnect();
        }
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testwxMpLogin(){
    	
    	String urlString = Constants.SERVER_RUL + "/api/v2/token/wxmp";
		String result = "";
		BufferedReader reader = null;
		try {
			URL url = null;
			url = new URL(urlString);
			HttpURLConnection openurl = null;
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
			// 往服务器里面发送数据
		    String bodyStr = UserRestTestUtils.getDataFromFile("/workspace/storbox-uam/src/test/resource/testData/testWxmpLogin1.txt");
            openurl.getOutputStream().write(bodyStr.getBytes());

			if (openurl.getResponseCode() == 200) {
				reader = new BufferedReader(new InputStreamReader(openurl.getInputStream()));
				result = reader.readLine();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println(result);
		
//		testGetWxMpInfo();
    }
    
    
    @Test
    public void testGetWxMpInfo(){
    	
    	String urlString = Constants.SERVER_RUL + "/api/v2/wxmp/authCode/info";
		String result = "";
		BufferedReader reader = null;
		try {
			URL url = null;
			url = new URL(urlString);
			HttpURLConnection openurl = null;
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
			// 往服务器里面发送数据
		    String bodyStr = UserRestTestUtils.getDataFromFile("/workspace/storbox-uam/src/test/resource/testData/testWxmpLogin1.txt");
            openurl.getOutputStream().write(bodyStr.getBytes());
            System.out.println(openurl.getResponseCode());
            System.out.println(openurl.getResponseMessage());
			if (openurl.getResponseCode() == 200) {
				reader = new BufferedReader(new InputStreamReader(openurl.getInputStream()));
				result = reader.readLine();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println(result);
    }
    
    
    @Test
    public void testInviterByMe(){
    	
    	String urlString = Constants.SERVER_RUL + "/api/v2/wxmp/authCode/inviterByMe";
		String result = "";
		BufferedReader reader = null;
		try {
			URL url = null;
			url = new URL(urlString);
			HttpURLConnection openurl = null;
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
			// 往服务器里面发送数据
//		    String bodyStr = UserRestTestUtils.getDataFromFile("/workspace/storbox-uam/src/test/resource/testData/testWxmpLogin1.txt");
			RestPageWxMpRequest restPageWxMpRequest = new RestPageWxMpRequest();
			restPageWxMpRequest.setCode("code");
			restPageWxMpRequest.setEncryptedData("EncryptedData");
			restPageWxMpRequest.setIv("iv");
			restPageWxMpRequest.setMpId("mpId");
			List<Order> orderList = new ArrayList<>();
			Order order=new Order();
			order.setDesc(true);
			order.setField("createdAt");
			order.setDirection("desc");
			orderList.add(order);
			Limit limit = new Limit();
			limit.setLength(20);
			limit.setOffset(0L);
			restPageWxMpRequest.setOrderList(orderList);
			restPageWxMpRequest.setLimit(limit);
		    String bodyStr = JsonUtils.toJson(restPageWxMpRequest);
            openurl.getOutputStream().write(bodyStr.getBytes());
            System.out.println(openurl.getResponseCode());
            System.out.println(openurl.getResponseMessage());
            
			if (openurl.getResponseCode() == 200) {
				reader = new BufferedReader(new InputStreamReader(openurl.getInputStream()));
				result = reader.readLine();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println(result);
    }
}
