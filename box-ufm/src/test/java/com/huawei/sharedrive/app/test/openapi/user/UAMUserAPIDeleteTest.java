package com.huawei.sharedrive.app.test.openapi.user;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

public class UAMUserAPIDeleteTest extends BaseAPITest
{
    
    private boolean showResult = true;
    
    @Test
    public void testNormal() throws Exception
    {
        bulidUrl(2L);

        String date = MyTestUtils.getDateString();
        String auth = MyTestUtils.getUAMAccountAuthorization(date);
        HttpURLConnection connection = getConnection(url, METHOD_DELETE, auth, date, null);
        MyResponseUtils.assert200(connection, showResult);
        
    }
    
    private void bulidUrl(long userId)
    {
        url = MyTestUtils.SERVER_URL_UAM_V2 + "users/" + userId;
    }
    
    
}
