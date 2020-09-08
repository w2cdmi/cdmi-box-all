package com.huawei.sharedrive.app.test.openapi.account;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

public class AccountAPIModifyTest extends BaseAPITest
{
    private static final String DATA_CREATE="testData/account/createAccount.txt";
    
    private static final String DATA_MODIFY="testData/account/modifyAccount.txt";
    
    private String buildUrl(long accountId)
    {
        return MyTestUtils.SERVER_URL_UFM_V2 +"accounts/" + accountId ;
    }
    
    @Test
    public void testNormal() throws Exception
    {
        String url = buildUrl(6l);
        String body = MyFileUtils.getDataFromFile(DATA_MODIFY, "normal");
        String dateStr = MyTestUtils.getDateString();
        String authorization = MyTestUtils.getAppAuthorization(dateStr);
        authorization = authorization.replaceAll("app,", "account,");
        System.out.println(body);
        System.out.println(authorization);
        HttpURLConnection connection = getConnection(url,
            METHOD_PUT,
            authorization,
            dateStr,
            body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
}

