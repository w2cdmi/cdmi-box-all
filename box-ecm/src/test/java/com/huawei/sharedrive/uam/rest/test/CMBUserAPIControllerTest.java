package com.huawei.sharedrive.uam.rest.test;

import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

import com.huawei.sharedrive.uam.openapi.domain.RestTokenCreateResponse;
import com.huawei.sharedrive.uam.rest.test.util.Constants;
import com.huawei.sharedrive.uam.rest.test.util.MyResponseUtils;
import com.huawei.sharedrive.uam.rest.test.util.MyTestUtils;

public class CMBUserAPIControllerTest
{
    private static final boolean showResult = true;
    
    // 列举用户相关请求体
    private static final String TEST_NORMAL_LIST_USERS = "testData/testCMBUser.txt";
    
    /**
     * 获取用户详情
     */
    @Test
    public void getNormalUserInfo()
    {
        HttpURLConnection openurl = null;
        try
        {
            RestTokenCreateResponse restTokenCreateResponse = MyTestUtils.login();
            String urlString = Constants.SERVER_RUL + "/api/v2/cmb/user";
            URL url = null;
            url = new URL(urlString);
            System.out.println("url is " + urlString);
            openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestProperty("Authorization", restTokenCreateResponse.getToken());
            openurl.setRequestMethod("POST");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            openurl.connect();
            String bodyStr = UserRestTestUtils.getDataFromFile(TEST_NORMAL_LIST_USERS);
            openurl.getOutputStream().write(bodyStr.getBytes());
            MyResponseUtils.assert200(openurl, showResult);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            openurl.disconnect();
        }
    }
}
