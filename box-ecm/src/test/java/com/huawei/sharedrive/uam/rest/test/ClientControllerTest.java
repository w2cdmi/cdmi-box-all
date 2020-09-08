package com.huawei.sharedrive.uam.rest.test;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;

import com.huawei.sharedrive.uam.openapi.domain.RestTokenCreateResponse;
import com.huawei.sharedrive.uam.openapi.domain.RestUserLoginCreateRequest;
import com.huawei.sharedrive.uam.rest.test.util.Constants;
import com.huawei.sharedrive.uam.rest.test.util.MyResponseUtils;
import com.huawei.sharedrive.uam.rest.test.util.MyTestUtils;

import pw.cdmi.core.restrpc.RestClient;

public class ClientControllerTest
{
    
    private static final boolean showResult = true;
    
    // 列举用户相关请求体
    private static final String TEST_NORMAL_LIST_USERS = "testData/getCheckCode.txt";
    
    @Test
    public void getCheckCode()
    {
        HttpURLConnection openurl = null;
        try
        {
            String urlString = Constants.SERVER_RUL + "/api/v2/pcloud/getCheckCode";
            URL url = null;
            url = new URL(urlString);
            System.out.println("url is " + urlString);
            openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestProperty("Authorization", "OneBox/a8b69bb0b74540ee9639a99e7ce4bae1");
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
