package com.huawei.sharedrive.app.test.openapi.region;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

public class RegionAPIListTest extends BaseAPITest
{
    private String buildUrl()
    {
        return MyTestUtils.SERVER_URL_UFM_V2 +"regions";
    }
    
    @Test
    public void testNormal() throws Exception
    {
        String url = buildUrl();
        String dateStr = MyTestUtils.getDateString();
        HttpURLConnection connection = getConnection(url, METHOD_GET, MyTestUtils.getAccountAuthorization(dateStr), dateStr, null);
        MyResponseUtils.assert200(connection, showResult);
    }
}
