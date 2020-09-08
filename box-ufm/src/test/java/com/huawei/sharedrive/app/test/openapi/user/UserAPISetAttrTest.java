package com.huawei.sharedrive.app.test.openapi.user;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.openapi.domain.user.SetUserAttributeRequest;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

import pw.cdmi.core.utils.JsonUtils;

public class UserAPISetAttrTest extends BaseAPITest
{
    
    private boolean showResult = true;
    
    @Test
    public void testUserTokenAuth() throws Exception
    {
        buildUrl(userId1);

        SetUserAttributeRequest request = new SetUserAttributeRequest();
        request.setName("messageNotice");
        request.setValue("enable");
        
        HttpURLConnection connection = getConnection(url, METHOD_PUT, JsonUtils.toJson(request));
        MyResponseUtils.assert200(connection, showResult);
        
    }
    
    @Test
    public void testAppAccountAuth() throws Exception
    {
        buildUrl(userId1);

        SetUserAttributeRequest request = new SetUserAttributeRequest();
        request.setName("messageNotice");
        request.setValue("enable");
        
        String dateString = MyTestUtils.getDateString();
        String authString = MyTestUtils.getAccountAuthorization(dateString);
        HttpURLConnection connection = getConnection(url, METHOD_PUT,authString, dateString, JsonUtils.toJson(request));
        MyResponseUtils.assert200(connection, showResult);
        
    }
    
    @Test
    public void testForbiden() throws Exception
    {
        buildUrl(userId2);

        SetUserAttributeRequest request = new SetUserAttributeRequest();
        request.setName("messageNotice");
        request.setValue("disable");
        
        HttpURLConnection connection = getConnection(url, METHOD_PUT, JsonUtils.toJson(request));
        MyResponseUtils.assertReturnCode(connection, ErrorCode.FORBIDDEN_OPER, showResult);
        
    }
    
    @Test
    public void testNoSuchUser() throws Exception
    {
        buildUrl(99999L);

        SetUserAttributeRequest request = new SetUserAttributeRequest();
        request.setName("messageNotice");
        request.setValue("enable");
        
        String dateString = MyTestUtils.getDateString();
        String authString = MyTestUtils.getAccountAuthorization(dateString);
        HttpURLConnection connection = getConnection(url, METHOD_PUT,authString, dateString, JsonUtils.toJson(request));
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_USER, showResult);
    }
    
    @Test
    public void testInvalidName() throws Exception
    {
        buildUrl(userId1);

        SetUserAttributeRequest request = new SetUserAttributeRequest();
        request.setName("invalidAttrName");
        request.setValue("enable");
        
        HttpURLConnection connection = getConnection(url, METHOD_PUT, JsonUtils.toJson(request));
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testInvalidValue() throws Exception
    {
        buildUrl(userId1);

        SetUserAttributeRequest request = new SetUserAttributeRequest();
        request.setName("messageNotice");
        request.setValue("enable21");
        
        HttpURLConnection connection = getConnection(url, METHOD_PUT, JsonUtils.toJson(request));
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
        
    }
    
    private void buildUrl(long userId)
    {
        url = MyTestUtils.SERVER_URL_UFM_V2 + "users/" + userId + "/attributes";
    }
    
}
