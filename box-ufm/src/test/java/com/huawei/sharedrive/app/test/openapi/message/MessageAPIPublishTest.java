package com.huawei.sharedrive.app.test.openapi.message;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.openapi.domain.message.MessagePublishRequest;
import com.huawei.sharedrive.app.test.openapi.FileBaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

import pw.cdmi.core.utils.JsonUtils;

public class MessageAPIPublishTest extends FileBaseAPITest
{
    private static final String TEST_DATA = "testData/message/publish.txt";
    
    public MessageAPIPublishTest()
    {
        url = buildUrl();
    }
    
    private String buildUrl()
    {
        return MyTestUtils.SERVER_URL_UFM_V2 + "messages/publish";
    }
    
    @Test
    public void testNormal() throws Exception
    {
        MessagePublishRequest request = new MessagePublishRequest();
        request.setCreatedAt(System.currentTimeMillis());
        request.setExpiredAt(System.currentTimeMillis());
        request.setId(1L);
        request.setProviderId(1L);
        request.setProviderName("tx");
        request.setProviderUsername("test");
        request.setType("system");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("title", "title");
        params.put("content", "content");
        params.put("announcementId", 12);
        request.setParams(params);
        String dateStr = MyTestUtils.getDateString();
        HttpURLConnection connection = getConnection(url, METHOD_PUT, "", dateStr, JsonUtils.toJson(request));
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testCreateAtMoreThanExpiredAt() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "CreateAtMoreThanExpiredAt");
        String url = buildUrl();
        String dateStr = MyTestUtils.getDateString();
        HttpURLConnection connection = getConnection(url,
            METHOD_PUT,
            MyTestUtils.getTestUserToken1(),
            dateStr,
            body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
}
