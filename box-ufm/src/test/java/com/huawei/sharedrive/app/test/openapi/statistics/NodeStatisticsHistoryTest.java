package com.huawei.sharedrive.app.test.openapi.statistics;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

public class NodeStatisticsHistoryTest extends BaseAPITest
{
    
    private static final String FILE = "testData/statistics/FileStatisticsHistoryTest.txt";
    
    private static final String url = "statistics/nodes/history";
    
    @Test
    public void testDay() throws Exception
    {
        String testUrl = MyTestUtils.SERVER_URL_UFM_V2 + url;
        String body  = MyFileUtils.getDataFromFile(FILE, "day");
        HttpURLConnection connection = getConnectionWithStatistics(testUrl, METHOD_POST,body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testWeek() throws Exception
    {
        String testUrl = MyTestUtils.SERVER_URL_UFM_V2 + url;
        String body  = MyFileUtils.getDataFromFile(FILE, "week");
        HttpURLConnection connection = getConnectionWithStatistics(testUrl, METHOD_POST,body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testMonth() throws Exception
    {
        String testUrl = MyTestUtils.SERVER_URL_UFM_V2 + url;
        String body  = MyFileUtils.getDataFromFile(FILE, "month");
        HttpURLConnection connection = getConnectionWithStatistics(testUrl, METHOD_POST,body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testSeason() throws Exception
    {
        String testUrl = MyTestUtils.SERVER_URL_UFM_V2 + url;
        String body  = MyFileUtils.getDataFromFile(FILE, "season");
        HttpURLConnection connection = getConnectionWithStatistics(testUrl, METHOD_POST,body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testYear() throws Exception
    {
        String testUrl = MyTestUtils.SERVER_URL_UFM_V2 + url;
        String body  = MyFileUtils.getDataFromFile(FILE, "year");
        HttpURLConnection connection = getConnectionWithStatistics(testUrl, METHOD_POST,body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testErrorInterval() throws Exception
    {
        String testUrl = MyTestUtils.SERVER_URL_UFM_V2 + url;
        String body  = MyFileUtils.getDataFromFile(FILE, "errorInterval");
        HttpURLConnection connection = getConnectionWithStatistics(testUrl, METHOD_POST,body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
}
