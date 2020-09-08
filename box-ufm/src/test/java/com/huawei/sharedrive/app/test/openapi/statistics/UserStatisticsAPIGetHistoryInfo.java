package com.huawei.sharedrive.app.test.openapi.statistics;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

public class UserStatisticsAPIGetHistoryInfo extends BaseAPITest
{
    
    private static final String FILE = "testData/statistics/getHistoryInfo.txt";
    
    private static final String url = "statistics/users/history";
    
    @Test
    public void testNormal() throws Exception
    {
        for(int i = 0 ; i < 50 ;i++)
        {
            System.out.println("=============>"+i);
            String testUrl = MyTestUtils.SERVER_URL_UFM_V2 + url;
            String body  = MyFileUtils.getDataFromFile(FILE, "normal");
            String dateStr = MyTestUtils.getDateString();
            String authorization = MyTestUtils.getSystemAuthorization(dateStr);
            HttpURLConnection connection = getConnection(testUrl, METHOD_POST, authorization, dateStr, body);
            MyResponseUtils.assert200(connection, showResult);
            System.out.println("=============>end");
        }
    }
    
    @Test
    public void testNormal1()
    {
    }
    
}
