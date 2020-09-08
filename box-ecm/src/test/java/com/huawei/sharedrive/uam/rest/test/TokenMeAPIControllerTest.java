package com.huawei.sharedrive.uam.rest.test;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import org.junit.Test;

import com.huawei.sharedrive.uam.exception.ErrorCode;
import com.huawei.sharedrive.uam.openapi.domain.RestTokenCreateResponse;
import com.huawei.sharedrive.uam.rest.test.util.Constants;
import com.huawei.sharedrive.uam.rest.test.util.MyResponseUtils;
import com.huawei.sharedrive.uam.rest.test.util.MyTestUtils;

import pw.cdmi.common.util.signature.SignatureUtils;
import pw.cdmi.core.utils.DateUtils;

public class TokenMeAPIControllerTest
{
    private static final boolean showResult = true;
    
    // 列举用户相关请求体
    private static final String TEST_NORMAL_LIST_USERS = "testData/testNormalListAdUsers.txt";
    
    private static final String TEST_INVALID_APPID_LIST_USERS = "testData/testInvalidAppIdListUsers.txt";
    
    private static final String TEST_INVALID_LIMIT_LIST_USERS = "testData/testInvalidLimitListAdUsers.txt";
    
    private static final String TEST_INVALID_OFFSET_LIST_USERS = "testData/testInvalidOffsetListUsers.txt";
    
    private static final String TEST_INVALID_TYPE_LIST_USERS = "testData/testInvalidtypeListUsers.txt";
    
    private static final String TEST_NOT_EXIST_LIST_USERS = "testData/testNotExistListAdUsers.txt";
    
    // ldap用户开户相关请求体
    private static final String TEST_CREATED_CREATE_LDAPUSER = "testData/testCreatedCreateLdapUser.txt";
    
    private static final String TEST_INVALID_CREATE_LDAPUSER = "testData/testInvalidCreateLdapUser.txt";
    
    private static final String TEST_NORMAL_CREATE_LDAPUSER = "testData/testNormalCreateLdapUser.txt";
    
    private static final String TEST_NOTEXIST_CREATE_LDAPUSER = "testData/testNotExistCreateLdapUser.txt";
    
    private static final String TEST_NULL_CREATE_LDAPUSER = "testData/testNullLdapUser.txt";
    
    private static final String TEST_UPDATE_NORMAL_USER = "testData/testUpdateNormalUser.txt";
    
    private static final String TEST_CREATE_NORMAL_USER = "testData/testCreateNormalUser.txt";
    
    private static final String ACCESS_APP_KEY = "c45bf8b5f76ae1bef4229d31096aa946";
    
    private static final String SECRET_APP_KEY = "26ceadeb92d94e84e484eac0bd2c6e8f";
    
    /**
     * 获取用户详情
     */
    @Test
    public void getNormalUserInfo()
    {
        HttpURLConnection openurl = null;
        try
        {
            RestTokenCreateResponse restTokenCreateResponse = MyTestUtils.login();
            
            String urlString = Constants.SERVER_RUL + "/api/v2/users/me";
            URL url = null;
            url = new URL(urlString);
            System.out.println("url is " + urlString);
            openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestProperty("Authorization", restTokenCreateResponse.getToken());
            openurl.setRequestMethod("GET");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.connect();
            MyResponseUtils.assert200(openurl, showResult);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            openurl.disconnect();
        }
    }
    
    /**
     * 未登录情况下获取用户详情
     */
    @Test
    public void getErrorUserInfo()
    {
        HttpURLConnection openurl = null;
        try
        {
            
            String urlString = Constants.SERVER_RUL + "/api/v2/users/me";
            URL url = null;
            url = new URL(urlString);
            System.out.println("url is " + urlString);
            openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestProperty("Authorization", "xxx");
            openurl.setRequestMethod("GET");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.connect();
            MyResponseUtils.assertReturnCode(openurl, ErrorCode.LOGINUNAUTHORIZED, showResult);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            openurl.disconnect();
        }
    }
    
    /**
     * 正常列举AD用户
     */
    @Test
    public void listNormalAdUsers()
    {
        HttpURLConnection openurl = null;
        try
        {
            RestTokenCreateResponse restTokenCreateResponse = MyTestUtils.login();
            
            String urlString = Constants.SERVER_RUL + "/api/v2/users/search";
            URL url = null;
            url = new URL(urlString);
            System.out.println("url is " + urlString);
            openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestProperty("Authorization", restTokenCreateResponse.getToken());
            openurl.setRequestMethod("POST");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            openurl.connect();
            String bodyStr = UserRestTestUtils.getDataFromFile(TEST_NORMAL_LIST_USERS);
            openurl.getOutputStream().write(bodyStr.getBytes());
            MyResponseUtils.assert200(openurl, showResult);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            openurl.disconnect();
        }
    }
    
    /**
     * 未登录的用户列举AD用户
     */
    @Test
    public void listUnNormalAdUsers()
    {
        HttpURLConnection openurl = null;
        try
        {
            String urlString = Constants.SERVER_RUL + "/api/v2/users/search";
            URL url = null;
            url = new URL(urlString);
            System.out.println("url is " + urlString);
            openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestProperty("Authorization", "");
            openurl.setRequestMethod("POST");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            openurl.connect();
            String bodyStr = UserRestTestUtils.getDataFromFile(TEST_NORMAL_LIST_USERS);
            openurl.getOutputStream().write(bodyStr.getBytes());
            MyResponseUtils.assert200(openurl, showResult);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            openurl.disconnect();
        }
    }
    
    /**
     * 传输错误的appId列举用户
     */
    @Test
    public void listInvalidAppIdUsers()
    {
        HttpURLConnection openurl = null;
        try
        {
            RestTokenCreateResponse restTokenCreateResponse = MyTestUtils.login();
            
            String urlString = Constants.SERVER_RUL + "/api/v2/users/search";
            URL url = null;
            url = new URL(urlString);
            System.out.println("url is " + urlString);
            openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestProperty("Authorization", restTokenCreateResponse.getToken());
            openurl.setRequestMethod("POST");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            openurl.connect();
            String bodyStr = UserRestTestUtils.getDataFromFile(TEST_INVALID_APPID_LIST_USERS);
            openurl.getOutputStream().write(bodyStr.getBytes());
            MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            openurl.disconnect();
        }
    }
    
    /**
     * 传输错误的limit列举AD用户
     */
    @Test
    public void listInvalidLimitUsers()
    {
        HttpURLConnection openurl = null;
        try
        {
            RestTokenCreateResponse restTokenCreateResponse = MyTestUtils.login();
            
            String urlString = Constants.SERVER_RUL + "/api/v2/users/search";
            URL url = null;
            url = new URL(urlString);
            System.out.println("url is " + urlString);
            openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestProperty("Authorization", restTokenCreateResponse.getToken());
            openurl.setRequestMethod("POST");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            openurl.connect();
            String bodyStr = UserRestTestUtils.getDataFromFile(TEST_INVALID_LIMIT_LIST_USERS);
            openurl.getOutputStream().write(bodyStr.getBytes());
            MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            openurl.disconnect();
        }
    }
    
    /**
     * 传输错误的offset列举AD用户
     */
    @Test
    public void listInvalidOffsetUsers()
    {
        HttpURLConnection openurl = null;
        try
        {
            RestTokenCreateResponse restTokenCreateResponse = MyTestUtils.login();
            
            String urlString = Constants.SERVER_RUL + "/api/v2/users/search";
            URL url = null;
            url = new URL(urlString);
            System.out.println("url is " + urlString);
            openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestProperty("Authorization", restTokenCreateResponse.getToken());
            openurl.setRequestMethod("POST");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            openurl.connect();
            String bodyStr = UserRestTestUtils.getDataFromFile(TEST_INVALID_OFFSET_LIST_USERS);
            openurl.getOutputStream().write(bodyStr.getBytes());
            MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            openurl.disconnect();
        }
    }
    
    /**
     * 传输错误的类型列举用户
     */
    @Test
    public void listInvalidTypeUsers()
    {
        HttpURLConnection openurl = null;
        try
        {
            RestTokenCreateResponse restTokenCreateResponse = MyTestUtils.login();
            
            String urlString = Constants.SERVER_RUL + "/api/v2/users/search";
            URL url = null;
            url = new URL(urlString);
            System.out.println("url is " + urlString);
            openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestProperty("Authorization", restTokenCreateResponse.getToken());
            openurl.setRequestMethod("POST");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            openurl.connect();
            String bodyStr = UserRestTestUtils.getDataFromFile(TEST_INVALID_TYPE_LIST_USERS);
            openurl.getOutputStream().write(bodyStr.getBytes());
            MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            openurl.disconnect();
        }
    }
    
    /**
     * 列举不存在的AD用户
     */
    @Test
    public void listInvalidNotExistUsers()
    {
        HttpURLConnection openurl = null;
        try
        {
            RestTokenCreateResponse restTokenCreateResponse = MyTestUtils.login();
            
            String urlString = Constants.SERVER_RUL + "/api/v2/users/search";
            URL url = null;
            url = new URL(urlString);
            System.out.println("url is " + urlString);
            openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestProperty("Authorization", restTokenCreateResponse.getToken());
            openurl.setRequestMethod("POST");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            openurl.connect();
            String bodyStr = UserRestTestUtils.getDataFromFile(TEST_NOT_EXIST_LIST_USERS);
            openurl.getOutputStream().write(bodyStr.getBytes());
            MyResponseUtils.assert200(openurl, showResult);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            openurl.disconnect();
        }
    }
    
    /**
     * ldap用户正常开户
     */
    @Test
    public void createNormalLdapuser()
    {
        HttpURLConnection openurl = null;
        try
        {
            RestTokenCreateResponse restTokenCreateResponse = MyTestUtils.login();
            
            String urlString = Constants.SERVER_RUL + "/api/v2/users/ldapuser";
            URL url = null;
            url = new URL(urlString);
            System.out.println("url is " + urlString);
            openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestProperty("Authorization", restTokenCreateResponse.getToken());
            openurl.setRequestMethod("POST");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            openurl.connect();
            String bodyStr = UserRestTestUtils.getDataFromFile(TEST_NORMAL_CREATE_LDAPUSER);
            openurl.getOutputStream().write(bodyStr.getBytes());
            MyResponseUtils.assert201(openurl, showResult);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            openurl.disconnect();
        }
    }
    
    /**
     * 未登录用户ldap用户正常开户
     */
    @Test
    public void createUnNormalLdapuser()
    {
        HttpURLConnection openurl = null;
        try
        {
            String urlString = Constants.SERVER_RUL + "/api/v2/users/ldapuser";
            URL url = null;
            url = new URL(urlString);
            System.out.println("url is " + urlString);
            openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestProperty("Authorization", "");
            openurl.setRequestMethod("POST");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            openurl.connect();
            String bodyStr = UserRestTestUtils.getDataFromFile(TEST_NORMAL_CREATE_LDAPUSER);
            openurl.getOutputStream().write(bodyStr.getBytes());
            MyResponseUtils.assertReturnCode(openurl, ErrorCode.LOGINUNAUTHORIZED, showResult);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            openurl.disconnect();
        }
    }
    
    /**
     * 未登录用户ldap用户正常开户
     */
    @Test
    public void createCreatedLdapuser()
    {
        HttpURLConnection openurl = null;
        try
        {
            RestTokenCreateResponse restTokenCreateResponse = MyTestUtils.login();
            String urlString = Constants.SERVER_RUL + "/api/v2/users/ldapuser";
            URL url = null;
            url = new URL(urlString);
            System.out.println("url is " + urlString);
            openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestProperty("Authorization", restTokenCreateResponse.getToken());
            openurl.setRequestMethod("POST");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            openurl.connect();
            String bodyStr = UserRestTestUtils.getDataFromFile(TEST_CREATED_CREATE_LDAPUSER);
            openurl.getOutputStream().write(bodyStr.getBytes());
            MyResponseUtils.assertReturnCode(openurl, ErrorCode.EXIST_USER_CONFLICT, showResult);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            openurl.disconnect();
        }
    }
    
    /**
     * 登录用户名不正常，ldap用户开户
     */
    @Test
    public void createInvalidLdapuser()
    {
        HttpURLConnection openurl = null;
        try
        {
            RestTokenCreateResponse restTokenCreateResponse = MyTestUtils.login();
            
            String urlString = Constants.SERVER_RUL + "/api/v2/users/ldapuser";
            URL url = null;
            url = new URL(urlString);
            System.out.println("url is " + urlString);
            openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestProperty("Authorization", restTokenCreateResponse.getToken());
            openurl.setRequestMethod("POST");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            openurl.connect();
            String bodyStr = UserRestTestUtils.getDataFromFile(TEST_INVALID_CREATE_LDAPUSER);
            openurl.getOutputStream().write(bodyStr.getBytes());
            MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            openurl.disconnect();
        }
    }
    
    /**
     * 用户不存在，ldap用户开户
     */
    @Test
    public void createNotExistLdapuser()
    {
        HttpURLConnection openurl = null;
        try
        {
            RestTokenCreateResponse restTokenCreateResponse = MyTestUtils.login();
            
            String urlString = Constants.SERVER_RUL + "/api/v2/users/ldapuser";
            URL url = null;
            url = new URL(urlString);
            System.out.println("url is " + urlString);
            openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestProperty("Authorization", restTokenCreateResponse.getToken());
            openurl.setRequestMethod("POST");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            openurl.connect();
            String bodyStr = UserRestTestUtils.getDataFromFile(TEST_NOTEXIST_CREATE_LDAPUSER);
            openurl.getOutputStream().write(bodyStr.getBytes());
            MyResponseUtils.assertReturnCode(openurl, ErrorCode.NO_SUCH_USER, showResult);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            openurl.disconnect();
        }
    }
    
    /**
     * 用户为空，ldap用户开户
     */
    @Test
    public void createNullLdapuser()
    {
        HttpURLConnection openurl = null;
        try
        {
            RestTokenCreateResponse restTokenCreateResponse = MyTestUtils.login();
            
            String urlString = Constants.SERVER_RUL + "/api/v2/users/ldapuser";
            URL url = null;
            url = new URL(urlString);
            System.out.println("url is " + urlString);
            openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestProperty("Authorization", restTokenCreateResponse.getToken());
            openurl.setRequestMethod("POST");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            openurl.connect();
            String bodyStr = UserRestTestUtils.getDataFromFile(TEST_NULL_CREATE_LDAPUSER);
            openurl.getOutputStream().write(bodyStr.getBytes());
            MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            openurl.disconnect();
        }
    }
    
    /**
     * 用户为空，ldap用户开户
     */
    @Test
    public void updateUser()
    {
        HttpURLConnection openurl = null;
        try
        {
            RestTokenCreateResponse restTokenCreateResponse = MyTestUtils.login();
            
            String urlString = Constants.SERVER_RUL + "/api/v2/users/29253";
            // String urlString = Constants.SERVER_RUL + "/api/v2/users/292450";
            URL url = null;
            url = new URL(urlString);
            System.out.println("url is " + urlString);
            openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestProperty("Authorization", restTokenCreateResponse.getToken());
            openurl.setRequestMethod("PUT");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            openurl.connect();
            String bodyStr = UserRestTestUtils.getDataFromFile(TEST_UPDATE_NORMAL_USER);
            openurl.getOutputStream().write(bodyStr.getBytes());
            MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            openurl.disconnect();
        }
    }
    
    /**
     * 用户为空，ldap用户开户
     */
    @Test
    public void createUser()
    {
        HttpURLConnection openurl = null;
        try
        {
            
            String urlString = Constants.SERVER_RUL + "/api/v2/users";
            // String urlString = Constants.SERVER_RUL + "/api/v2/users/292450";
            URL url = null;
            url = new URL(urlString);
            System.out.println("url is " + urlString);
            openurl = (HttpURLConnection) url.openConnection();
            getAppAtuhHeader(openurl, ACCESS_APP_KEY, SECRET_APP_KEY);
            openurl.setRequestMethod("POST");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            openurl.connect();
            String bodyStr = UserRestTestUtils.getDataFromFile(TEST_CREATE_NORMAL_USER);
            openurl.getOutputStream().write(bodyStr.getBytes());
            MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            openurl.disconnect();
        }
    }
    /**
     * 用户为空，ldap用户开户
     */
    @Test
    public void getUserByApp()
    {
        HttpURLConnection openurl = null;
        try
        {
            
            String urlString = Constants.SERVER_RUL + "/api/v2/users/29259";
            // String urlString = Constants.SERVER_RUL + "/api/v2/users/292450";
            URL url = null;
            url = new URL(urlString);
            System.out.println("url is " + urlString);
            openurl = (HttpURLConnection) url.openConnection();
            getAppAtuhHeader(openurl, ACCESS_APP_KEY, SECRET_APP_KEY);
            openurl.setRequestMethod("GET");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.connect();
            MyResponseUtils.assert200(openurl, showResult);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            openurl.disconnect();
        }
    }
    @Test
    public void getUserByToken()
    {
        HttpURLConnection openurl = null;
        try
        {
            RestTokenCreateResponse restTokenCreateResponse = MyTestUtils.login();
            String urlString = Constants.SERVER_RUL + "/api/v2/users/29259";
            // String urlString = Constants.SERVER_RUL + "/api/v2/users/292450";
            URL url = null;
            url = new URL(urlString);
            System.out.println("url is " + urlString);
            openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestProperty("Authorization", restTokenCreateResponse.getToken());
            openurl.setRequestMethod("GET");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.connect();
            MyResponseUtils.assert200(openurl, showResult);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            openurl.disconnect();
        }
    }
    private void getAppAtuhHeader(HttpURLConnection openurl, String accessKeyId, String appSecretKey)
    {
        Date now = new Date();
        String dateStr = DateUtils.dataToString(DateUtils.RFC822_DATE_FORMAT, now, null);
        String appSignatureKey = SignatureUtils.getSignature(appSecretKey, dateStr);
        String authorization = "app," + accessKeyId + ',' + appSignatureKey;
        openurl.setRequestProperty("Authorization", authorization);
        openurl.setRequestProperty("Date", dateStr);
    }
}
