package com.huawei.sharedrive.app.test.openapi.node;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.test.openapi.FileBaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

/**
 * 节点重命名及设置同步状态测试类
 * 
 * @author  t90006461
 * @version  CloudStor CSE Service Platform Subproject, 2014-7-7
 * @see  
 * @since  
 */
public class NodeAPIRenameAndSetSyncTest extends FileBaseAPITest
{
    private static final String TEST_DATA = "testData/node/nodeRenameAndSetSync.txt";
    
    private Long nodeId;
    
    public NodeAPIRenameAndSetSyncTest()
    {
        RestFolderInfo folderInfo = createRandomFolder();
        nodeId = folderInfo.getId();
        url = buildUrl(userId1, nodeId);
    }
    
    @Test
    public void testOnlyRename() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "onlyRename");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testUnauthorized () throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "onlyRename");
        HttpURLConnection connection = getConnectionWithUnauthToken(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.TOKENUNAUTHORIZED, showResult);
    }
    
    @Test
    public void testInvalidOwnerId () throws Exception
    {
        String url = buildUrl(-1l, nodeId);
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "onlyRename");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testInvalidFolderId () throws Exception
    {
        String url = buildUrl(userId1, -1L);
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "onlyRename");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testOnlySetSync() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "onlySetSync");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testRenameAndSetSync() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "renameAndSetSync");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testSetSycnForSubFolder() throws Exception
    {
        RestFolderInfo subFolder = createFolder(userId1, "subFolder", nodeId);
        String url = buildUrl(userId1, subFolder.getId());
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "renameAndSetSync");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.FORBIDDEN_OPER, showResult);
    }
    
    
    @Test
    public void testEmptyName() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "emptyName");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testLongName() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "longName");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testNameContainSlash() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "nameContainSlash");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testNameContainBackSlash() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "nameContainBackSlash");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testNameStartWithPeriod () throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "nameStartWithPeriod");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testNameEndtWithPeriod () throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "nameEndWithPeriod");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
 
    private String buildUrl(Long ownerId, Long folderId)
    {
        return MyTestUtils.SERVER_URL_UFM_V2 + "nodes/" + ownerId + "/" + folderId;
    }
}
