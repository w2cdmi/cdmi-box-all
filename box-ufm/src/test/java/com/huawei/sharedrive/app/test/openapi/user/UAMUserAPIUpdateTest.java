package com.huawei.sharedrive.app.test.openapi.user;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.test.domain.user.RestUserUpdateRequest;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

import pw.cdmi.core.utils.JsonUtils;

public class UAMUserAPIUpdateTest extends BaseAPITest
{
    
    private boolean showResult = true;
    
    @Test
    public void testUpdateStatus() throws Exception
    {
        bulidUrl(userId1);

        RestUserUpdateRequest request = new RestUserUpdateRequest();
        request.setStatus("enable");
        
        String date = MyTestUtils.getDateString();
        String auth = MyTestUtils.getUAMAccountAuthorization(date);
        HttpURLConnection connection = getConnection(url, METHOD_PUT, auth, date, JsonUtils.toJson(request));
        MyResponseUtils.assert200(connection, showResult);
        
    }
    
    private void bulidUrl(long userId)
    {
        url = MyTestUtils.SERVER_URL_UAM_V2 + "users/" + userId;
    }
    
    
}
