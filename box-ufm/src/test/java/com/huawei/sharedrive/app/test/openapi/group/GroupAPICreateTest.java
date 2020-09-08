package com.huawei.sharedrive.app.test.openapi.group;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.openapi.domain.group.RestGroupRequest;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

import pw.cdmi.core.utils.JsonUtils;

public class GroupAPICreateTest extends BaseAPITest
{
    private static final String DATA_CREATE = "testData/group/create.txt";
    
    private String buildUrl()
    {
        return MyTestUtils.SERVER_URL_UFM_V2 + "groups";
    }
    
    @Test
    public void testNormal() throws Exception
    {
        String url = buildUrl();
        String body = MyFileUtils.getDataFromFile(DATA_CREATE, "normal");
        
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert201(connection, showResult);
    }
    
    @Test
    public void testNameOverLength() throws Exception
    {
        String url = buildUrl();
        String body = MyFileUtils.getDataFromFile(DATA_CREATE, "overLength");
        
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testCreatePrivate() throws Exception
    {
        String url = buildUrl();
        String body = MyFileUtils.getDataFromFile(DATA_CREATE, "private");
        
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert201(connection, showResult);
    }
    
    @Test
    public void testNameEmpty() throws Exception
    {
        String url = buildUrl();
        String body = MyFileUtils.getDataFromFile(DATA_CREATE, "n");
        
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testInvalidGroupName() throws Exception
    {
        String url = buildUrl();
        RestGroupRequest request = new RestGroupRequest();
        request.setName("12;");
        HttpURLConnection connection = getConnection(url, METHOD_POST, JsonUtils.toJson(request));
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testErrorType() throws Exception
    {
        String url = buildUrl();
        String body = MyFileUtils.getDataFromFile(DATA_CREATE, "errorType");
        
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testAppToken() throws Exception
    {
        String url = buildUrl();
        String body = MyFileUtils.getDataFromFile(DATA_CREATE, "normal");
        String dateStr = MyTestUtils.getDateString();
        HttpURLConnection connection = getConnection(url,
            METHOD_POST,
            MyTestUtils.getAccountAuthorization(dateStr),
            dateStr,
            body);
        MyResponseUtils.assert201(connection, showResult);
    }
    
    @Test
    public void testUnAuthorization() throws Exception
    {
        String url = buildUrl();
        String body = MyFileUtils.getDataFromFile(DATA_CREATE, "normal");
        HttpURLConnection connection = getConnectionWithUnauthToken(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.TOKENUNAUTHORIZED, showResult);
    }
    
    @Test
    public void testAppWithoutOwnedBy() throws Exception
    {
        String url = buildUrl();
        String body = MyFileUtils.getDataFromFile(DATA_CREATE, "withOutOwnedBy");
        String dateStr = MyTestUtils.getDateString();
        HttpURLConnection connection = getConnection(url,
            METHOD_POST,
            MyTestUtils.getAppAuthorization(dateStr),
            dateStr,
            body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
}
