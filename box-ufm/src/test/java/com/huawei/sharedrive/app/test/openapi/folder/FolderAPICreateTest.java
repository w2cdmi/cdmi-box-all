package com.huawei.sharedrive.app.test.openapi.folder;

import java.net.HttpURLConnection;
import java.util.Date;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.test.openapi.FileBaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

/**
 * 创建文件夹测试 类
 * 
 * @author  t90006461
 * @version  CloudStor CSE Service Platform Subproject, 2014-7-14
 * @see  
 * @since  
 */
public class FolderAPICreateTest extends FileBaseAPITest
{
    /**
     * 所有创建文件夹数据
     */
    private static final String TEST_DATA = "testData/folder/folderCreate.txt";
    
    public FolderAPICreateTest()
    {
        url = MyTestUtils.SERVER_URL_UFM_V2 + "folders/" + userId1;
    }
    
    @Test
    public void testNormal() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "normal");
        body = body.replaceAll("#contentCreatedAt#", new Date().getTime() +"");
        body = body.replaceAll("#contentModifiedAt#", new Date().getTime() +"");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert201(connection, showResult);
    }
    
    @Test
    public void testComputer() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "computer");
        body = body.replaceAll("#contentCreatedAt#", new Date().getTime() +"");
        body = body.replaceAll("#contentModifiedAt#", new Date().getTime() +"");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert201(connection, showResult);
    }
    
    @Test
    public void testGetFilter() throws Exception
    {
        url = MyTestUtils.SERVER_URL_UAM_V2 + "config?option=backupForbiddenRule";
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assert201(connection, showResult);
    }
    
    @Test
    public void testDisk() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "disk");
        body = body.replaceAll("#contentCreatedAt#", new Date().getTime() +"");
        body = body.replaceAll("#contentModifiedAt#", new Date().getTime() +"");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert201(connection, showResult);
    }
    @Test
    public void testEmptyName() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "emptyName");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testLongName() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "longName");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testNameContainSlash() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "nameContainSlash");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testNameContainBackSlash() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "nameContainBackSlash");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testNameStartWithPeriod() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "nameStartWithPeriod");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testNameEndWithPeriod() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "nameEndWithPeriod");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testRepeatName() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "repeatName");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert201(connection, showResult);
        connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.FILES_CONFLICT, showResult);
    }
    
    @Test
    public void testNoParent() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "noParent");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_PARENT, showResult);
    }
    
    @Test
    public void testInvalidSpaceStatus() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "normal");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_SPACE_STATUS, showResult);
    }
    
    @Test
    public void testUnauthorized () throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "normal");
        HttpURLConnection connection = getConnectionWithUnauthToken(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.TOKENUNAUTHORIZED, showResult);
    }
    
}
