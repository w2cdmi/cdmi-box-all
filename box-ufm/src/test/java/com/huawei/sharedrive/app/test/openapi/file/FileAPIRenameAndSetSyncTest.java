package com.huawei.sharedrive.app.test.openapi.file;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.openapi.domain.node.FilePreUploadResponse;
import com.huawei.sharedrive.app.test.openapi.FileBaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

/**
 * 文件重命名及设置同步状态测试类
 * 
 * @author  t90006461
 * @version  CloudStor CSE Service Platform Subproject, 2014-7-7
 * @see  
 * @since  
 */
public class FileAPIRenameAndSetSyncTest extends FileBaseAPITest
{
    private static final String TEST_DATA = "testData/file/fileRenameAndSetSync.txt";
    
    private Long fileId;
    
    @Test
    public void testOnlyRename() throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        fileId = response.getFileId();
        url = MyTestUtils.SERVER_URL_UFM_V2 + "files/" + userId1 + "/" + fileId;
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "onlyRename");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testOnlySetSync() throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        fileId = response.getFileId();
        url = MyTestUtils.SERVER_URL_UFM_V2 + "files/" + userId1 + "/" + fileId;
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "onlySetSync");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testUnauthorized () throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        fileId = response.getFileId();
        url = MyTestUtils.SERVER_URL_UFM_V2 + "files/" + userId1 + "/" + fileId;
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "onlyRename");
        HttpURLConnection connection = getConnectionWithUnauthToken(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.TOKENUNAUTHORIZED, showResult);
    }
    
    @Test
    public void testRenameAndSetSync() throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        fileId = response.getFileId();
        url = MyTestUtils.SERVER_URL_UFM_V2 + "files/" + userId1 + "/" + fileId;
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "renameAndSetSync");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testRepeatName() throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        fileId = response.getFileId();
        url = MyTestUtils.SERVER_URL_UFM_V2 + "files/" + userId1 + "/" + fileId;
        
//        String repeatFolderName = MyFileUtils.getDataFromFile(TEST_DATA, "repeatFolderName");
//        createFolder(userId1, repeatFolderName, 0L);
        
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "repeatName");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.FILES_CONFLICT, showResult);
    }
    
    @Test
    public void testEmptyName() throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        fileId = response.getFileId();
        url = MyTestUtils.SERVER_URL_UFM_V2 + "files/" + userId1 + "/" + fileId;
        
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "emptyName");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testLongName() throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        fileId = response.getFileId();
        url = MyTestUtils.SERVER_URL_UFM_V2 + "files/" + userId1 + "/" + fileId;
        
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "longName");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testNameContainSlash() throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        fileId = response.getFileId();
        url = MyTestUtils.SERVER_URL_UFM_V2 + "files/" + userId1 + "/" + fileId;
        
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "nameContainSlash");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testNameContainBackSlash() throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        fileId = response.getFileId();
        url = MyTestUtils.SERVER_URL_UFM_V2 + "files/" + userId1 + "/" + fileId;
        
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "nameContainBackSlash");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testNameStartWithPeriod () throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        fileId = response.getFileId();
        url = MyTestUtils.SERVER_URL_UFM_V2 + "files/" + userId1 + "/" + fileId;
        
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "nameStartWithPeriod");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testNameEndtWithPeriod () throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        fileId = response.getFileId();
        url = MyTestUtils.SERVER_URL_UFM_V2 + "files/" + userId1 + "/" + fileId;
        
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "nameEndWithPeriod");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testInvalidSpaceStatus() throws Exception
    {
//        FilePreUploadResponse response = uploadFile();
//        fileId = response.getFileId();
//        System.out.println(fileId);
        url = MyTestUtils.SERVER_URL_UFM_V2 + "files/" + userId1 + "/" + 861;
        
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "nameEndWithPeriod");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
}
