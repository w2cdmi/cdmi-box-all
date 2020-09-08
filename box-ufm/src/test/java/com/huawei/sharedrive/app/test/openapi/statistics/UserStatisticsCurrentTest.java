package com.huawei.sharedrive.app.test.openapi.statistics;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.BaseConnection;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

public class UserStatisticsCurrentTest extends BaseAPITest
{
    
    private static final String FILE = "testData/statistics/userCurrentTest.txt";
    
    
    private static final String url = "statistics/users/current";
    
    @Test
    public void testNormal() throws Exception
    {
        String testUrl = MyTestUtils.SERVER_URL_UFM_V2 + url;
        String body  = MyFileUtils.getDataFromFile(FILE, "groupBy");
        System.out.println("request body: " + body);
        String dateString = MyTestUtils.getDateString();
        System.out.println("Request url : " + testUrl);
        HttpURLConnection connection = BaseConnection.getURLConnection(testUrl, METHOD_POST);
        connection.setRequestProperty("Content-type", "application/json");
        connection.setRequestProperty("Authorization", MyTestUtils.getSystemAuthorization(dateString));
        connection.setRequestProperty("Date", dateString);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        
        connection.getOutputStream().write(body.getBytes());
        
        MyResponseUtils.assert200(connection, showResult);
    }
}
