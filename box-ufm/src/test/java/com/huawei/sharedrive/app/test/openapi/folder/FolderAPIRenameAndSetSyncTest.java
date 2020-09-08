package com.huawei.sharedrive.app.test.openapi.folder;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.test.openapi.FileBaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

/**
 * 文件夹重命名及设置同步状态测试类
 * 
 * @author  t90006461
 * @version  CloudStor CSE Service Platform Subproject, 2014-7-7
 * @see  
 * @since  
 */
public class FolderAPIRenameAndSetSyncTest extends FileBaseAPITest
{
    private static final String TEST_DATA = "testData/folder/folderRenameAndSetSync.txt";
    
    private Long folderId;
    
    @Test
    public void testOnlyRename() throws Exception
    {
        RestFolderInfo folderInfo = createRandomFolder();
        folderId = folderInfo.getId();
        url = buildUrl(userId1, folderId);
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "onlyRename");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testUnauthorized () throws Exception
    {
        RestFolderInfo folderInfo = createRandomFolder();
        folderId = folderInfo.getId();
        url = buildUrl(userId1, folderId);
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "onlyRename");
        HttpURLConnection connection = getConnectionWithUnauthToken(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.TOKENUNAUTHORIZED, showResult);
    }
    
    @Test
    public void testInvalidOwnerId () throws Exception
    {
        RestFolderInfo folderInfo = createRandomFolder();
        folderId = folderInfo.getId();
        url = buildUrl(userId1, folderId);
        String url = buildUrl(-1l, folderId);
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "onlyRename");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testInvalidFolderId () throws Exception
    {
        createRandomFolder();
        url = buildUrl(userId1, folderId);
        String url = buildUrl(userId1, -1L);
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "onlyRename");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testOnlySetSync() throws Exception
    {
        RestFolderInfo folderInfo = createRandomFolder();
        folderId = folderInfo.getId();
        url = buildUrl(userId1, folderId);
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "onlySetSync");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testRenameAndSetSync() throws Exception
    {
        RestFolderInfo folderInfo = createRandomFolder();
        folderId = folderInfo.getId();
        url = buildUrl(userId1, folderId);
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "renameAndSetSync");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testEmptyName() throws Exception
    {
        RestFolderInfo folderInfo = createRandomFolder();
        folderId = folderInfo.getId();
        url = buildUrl(userId1, folderId);
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "emptyName");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testLongName() throws Exception
    {
        RestFolderInfo folderInfo = createRandomFolder();
        folderId = folderInfo.getId();
        url = buildUrl(userId1, folderId);
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "longName");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testNameContainSlash() throws Exception
    {
        RestFolderInfo folderInfo = createRandomFolder();
        folderId = folderInfo.getId();
        url = buildUrl(userId1, folderId);
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "nameContainSlash");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testNameContainBackSlash() throws Exception
    {
        RestFolderInfo folderInfo = createRandomFolder();
        folderId = folderInfo.getId();
        url = buildUrl(userId1, folderId);
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "nameContainBackSlash");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testNameStartWithPeriod () throws Exception
    {
        RestFolderInfo folderInfo = createRandomFolder();
        folderId = folderInfo.getId();
        url = buildUrl(userId1, folderId);
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "nameStartWithPeriod");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testNameEndtWithPeriod () throws Exception
    {
        RestFolderInfo folderInfo = createRandomFolder();
        folderId = folderInfo.getId();
        url = buildUrl(userId1, folderId);
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "nameEndWithPeriod");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testInvalidSpaceStatus() throws Exception
    {
//        RestFolderInfo folderInfo = createRandomFolder();
//        folderId = folderInfo.getId();
//        System.out.println(folderId);
        url = buildUrl(userId1, 772L);
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "onlyRename");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_SPACE_STATUS, showResult);
    }
 
    private String buildUrl(Long ownerId, Long folderId)
    {
        return MyTestUtils.SERVER_URL_UFM_V2 + "folders/" + ownerId + "/" + folderId;
    }
}
