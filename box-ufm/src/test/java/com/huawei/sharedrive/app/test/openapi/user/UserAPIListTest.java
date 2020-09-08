package com.huawei.sharedrive.app.test.openapi.user;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

/**
 * 用户列举测试类
 * 
 * @see
 * @since
 */
public class UserAPIListTest extends BaseAPITest
{
    private static final String TEST_DATA = "testData/users/userList.txt";
    
    public UserAPIListTest()
    {
        url = buildUrl();
    }
    
//    @Test
//    public void testNormal() throws Exception
//    {
//        HttpURLConnection connection = getConnection(url, METHOD_POST);
//        MyResponseUtils.assert200(connection, showResult);
//    }
    
    
    @Test
    public void testAllParameter() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "allParameter");
        HttpURLConnection connection = getConnectionWithAppAuth(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testOnlyOffset() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "onlyOffset");
        HttpURLConnection connection = getConnectionWithAppAuth(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testInvalidOffset() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "invalidOffset");
        HttpURLConnection connection = getConnectionWithAppAuth(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testOnlyLimit() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "onlyLimit");
        HttpURLConnection connection = getConnectionWithAppAuth(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testInvalidLimit() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "invalidLimit");
        HttpURLConnection connection = getConnectionWithAppAuth(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testOnlyOrder() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "onlyOrder");
        HttpURLConnection connection = getConnectionWithAppAuth(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testInvalidOrderField() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "invalidOrderField");
        HttpURLConnection connection = getConnectionWithAppAuth(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testInvalidOrderDirection() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "invalidOrderDirection");
        HttpURLConnection connection = getConnectionWithAppAuth(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    private String buildUrl()
    {
        return MyTestUtils.SERVER_URL_UFM_V2 + "users/items";
    }
}
