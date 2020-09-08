package com.huawei.sharedrive.app.test.openapi.node;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.test.openapi.FileBaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

/**
 * 节点搜索测试类
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2014-7-14
 * @see
 * @since
 */
public class NodeAPISearchTest extends FileBaseAPITest
{
    private static final String TEST_DATA = "testData/node/nodeSearch.txt";
    
    public NodeAPISearchTest()
    {
        url = buildUrl(userId1);
    }
    
    @Test
    public void testBlankName() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "blankName");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testNormal() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "normal");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testUnauthorize() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "normal");
        HttpURLConnection connection = getConnectionWithUnauthToken(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.TOKENUNAUTHORIZED, showResult);
    }
    
    @Test
    public void testAllParameter() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "allParameter");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testOnlyOffset() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "onlyOffset");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testInvalidOffset() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "invalidOffset");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testOnlyLimit() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "onlyLimit");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testInvalidLimit() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "invalidLimit");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testOnlyOrder() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "onlyOrder");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testInvalidOrderField() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "invalidOrderField");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testInvalidOrderDirection() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "invalidOrderDirection");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testOnlyThumbnail() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "onlyThumbnail");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testThumbnailNumOverLimit() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "thumbnailNumOverLimit");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testInvalidThumbnailWidth() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "invalidThumbnailWidth");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testInvalidThumbnailHeight() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "invalidThumbnailHeigth");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    private String buildUrl(Long ownerId)
    {
        return MyTestUtils.SERVER_URL_UFM_V2 + "nodes/" + ownerId + "/search";
    }
}
