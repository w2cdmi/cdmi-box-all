package com.huawei.sharedrive.app.test.openapi.account;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

public class AccountAPICreateTest extends BaseAPITest
{
    private static final String DATA_CREATE="testData/account/createAccount.txt";
    
    private String buildUrl()
    {
        return MyTestUtils.SERVER_URL_UFM_V2 +"accounts";
    }
    
    @Test
    public void testNormal() throws Exception
    {
        String url = buildUrl();
        String body = MyFileUtils.getDataFromFile(DATA_CREATE, "normal");
        String dateStr = MyTestUtils.getDateString();
        String authorization = MyTestUtils.getAppSystemAuthorization(dateStr);
        System.out.println(body);
        System.out.println(authorization);
        HttpURLConnection connection = getConnection(url,
            METHOD_POST,
            authorization,
            dateStr,
            body);
        MyResponseUtils.assert201(connection, showResult);
    }
    
}
