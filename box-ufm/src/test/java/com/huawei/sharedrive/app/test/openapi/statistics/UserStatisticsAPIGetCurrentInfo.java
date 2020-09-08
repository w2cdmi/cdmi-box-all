package com.huawei.sharedrive.app.test.openapi.statistics;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

public class UserStatisticsAPIGetCurrentInfo extends BaseAPITest
{
    private static final String DATA_GET ="testData/statistics/UserStatisticsGet.txt";
    
    private static final String url = "statistics/users/current";
    
    @Test
    public void testNormal() throws Exception
    {
        String testUrl = MyTestUtils.SERVER_URL_UFM_V2 + url;
        String body  = MyFileUtils.getDataFromFile(DATA_GET, "normal");
        HttpURLConnection connection = getConnectionWithStatistics(testUrl, METHOD_POST,body);
        MyResponseUtils.assert200(connection, showResult);
    }
}
