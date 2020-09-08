package com.huawei.sharedrive.app.test.openapi.message;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.openapi.domain.message.ListMessageRequest;
import com.huawei.sharedrive.app.test.openapi.FileBaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

import pw.cdmi.core.utils.JsonUtils;

public class MessageAPIListTest extends FileBaseAPITest
{
    
    @Test
    public void testAllParameter() throws Exception
    {
        buildUserMsgUrl();
        String body = generalRequestBody(0L, 10, 0L, "all");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testErrorStatus() throws Exception
    {
        buildUserMsgUrl();
        String body = generalRequestBody(0L, 10, 0L, "errorStatus");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testInvalidLimit() throws Exception
    {
        buildUserMsgUrl();
        String body = generalRequestBody(null, -10, null, null);
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testInvalidOffset() throws Exception
    {
        buildUserMsgUrl();
        String body = generalRequestBody(-5L, null, null, null);
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testNormal() throws Exception
    {
        buildUserMsgUrl();
        HttpURLConnection connection = getConnection(url, METHOD_POST);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testOnlyLimit() throws Exception
    {
        buildUserMsgUrl();
        String body = generalRequestBody(null, 10, null, null);
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testOnlyOffset() throws Exception
    {
        buildUserMsgUrl();
        String body = generalRequestBody(0L, null, null, null);
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testSystemMsg() throws Exception
    {
        buildSystemMsgUrl();
        HttpURLConnection connection = getConnection(url, METHOD_POST);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testUnauthorize() throws Exception
    {
        buildUserMsgUrl();
        HttpURLConnection connection = getConnectionWithUnauthToken(url, METHOD_POST, null);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.TOKENUNAUTHORIZED, showResult);
    }
    
    private void buildSystemMsgUrl()
    {
        url = MyTestUtils.SERVER_URL_UAM_V2 + "messages/items";
    }
    
    private void buildUserMsgUrl()
    {
        url = MyTestUtils.SERVER_URL_UFM_V2 + "messages/items";
    }
    
    private String generalRequestBody(Long offset, Integer limit, Long startId, String status)
    {
        ListMessageRequest request = new ListMessageRequest();
        request.setLimit(limit);
        request.setOffset(offset);
        request.setStartId(startId);
        request.setStatus(status);
        return JsonUtils.toJsonExcludeNull(request);
    }
}
