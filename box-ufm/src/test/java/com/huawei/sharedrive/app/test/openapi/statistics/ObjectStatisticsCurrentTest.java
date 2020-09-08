package com.huawei.sharedrive.app.test.openapi.statistics;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

public class ObjectStatisticsCurrentTest extends BaseAPITest
{
    
    private static final String FILE = "testData/statistics/ObjectStatisticsCurrentTest.txt";
    
    
    private static final String url = "statistics/objects/current";
    
    @Test
    public void testNormal() throws Exception
    {
        String testUrl = MyTestUtils.SERVER_URL_UFM_V2 + url;
        String body  = MyFileUtils.getDataFromFile(FILE, "normal");
        HttpURLConnection connection = getConnectionWithStatistics(testUrl, METHOD_POST,body);
        MyResponseUtils.assert200(connection, showResult);
    }
}
