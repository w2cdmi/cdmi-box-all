package com.huawei.sharedrive.app.test.openapi.user;

import java.net.HttpURLConnection;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

public class UserAPIGetAttrTest extends BaseAPITest
{
    
    private boolean showResult = true;
    
    @Test
    public void testUserTokenAuth() throws Exception
    {
        buildUrl(userId1, "messageNotice");

        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testAccountAuth() throws Exception
    {
        buildUrl(userId1, "messageNotice");
        String dateStr = MyTestUtils.getDateString();
        String authStr = MyTestUtils.getAccountAuthorization(dateStr);
        HttpURLConnection connection = getConnection(url, METHOD_GET, authStr, dateStr, null);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testForbbiden() throws Exception
    {
        buildUrl(99999L, "messageNotice");

        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.FORBIDDEN_OPER, showResult);
    }
    
    @Test
    public void testNosuchUserWithAccountAuth() throws Exception
    {
        buildUrl(99999L, "messageNotice");
        String dateStr = MyTestUtils.getDateString();
        String authStr = MyTestUtils.getAccountAuthorization(dateStr);
        HttpURLConnection connection = getConnection(url, METHOD_GET, authStr, dateStr, null);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_USER, showResult);
    }
    
    
    @Test
    public void testGetAll() throws Exception
    {
        buildUrl(userId1, null);

        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assert200(connection, showResult);
        
    }
    
    @Test
    public void testForbiden() throws Exception
    {
        buildUrl(userId2, "messageNotice");
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.FORBIDDEN_OPER, showResult);
    }
    
    @Test
    public void testInvalidName() throws Exception
    {
        buildUrl(userId1, "messageNotice123");
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    private void buildUrl(long userId, String name)
    {
        url = MyTestUtils.SERVER_URL_UFM_V2 + "users/" + userId + "/attributes";
        if(StringUtils.isNotBlank(name))
        {
            url = url + "?name=" + name;
        }
    }
    
}
