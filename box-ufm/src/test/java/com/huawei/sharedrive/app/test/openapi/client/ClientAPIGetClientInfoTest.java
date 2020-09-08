package com.huawei.sharedrive.app.test.openapi.client;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.test.openapi.FileBaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;

import pw.cdmi.core.utils.JsonUtils;

/**
 * 获取客户端信息接口测试类
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2014-7-7
 * @see
 * @since
 */
public class ClientAPIGetClientInfoTest extends FileBaseAPITest
{
    
    @Test
    public void testNormal() throws Exception
    {
        url = buildUrl();
        String body = generalBody("pc");
        HttpURLConnection connection = getConnection(url, METHOD_GET, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    private String buildUrl()
    {
        return "http://127.0.0.1:8080/cloudapp/api/v2/client/info";
    }
    
    private String generalBody(String clientType)
    {
        GetClientInfoRequest request = new GetClientInfoRequest();
        request.setClientType("PC");
        return JsonUtils.toJson(request);
    }
    
    class GetClientInfoRequest{
        private String clientType;

        public String getClientType()
        {
            return clientType;
        }

        public void setClientType(String clientType)
        {
            this.clientType = clientType;
        }
        
    }
}
