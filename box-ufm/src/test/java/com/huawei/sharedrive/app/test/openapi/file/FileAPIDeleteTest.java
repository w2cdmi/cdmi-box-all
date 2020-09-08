package com.huawei.sharedrive.app.test.openapi.file;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.openapi.domain.node.FilePreUploadResponse;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.test.openapi.FileBaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

import pw.cdmi.core.utils.RandomGUID;

/**
 * 删除文件测试类
 * 
 * @author  t90006461
 * @version  CloudStor CSE Service Platform Subproject, 2014-7-7
 * @see  
 * @since  
 */
public class FileAPIDeleteTest extends FileBaseAPITest
{
    private long fileId;
    
    @Test
    public void testNormal() throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        fileId = response.getFileId();
        url = buildUrl(userId1, fileId);
        HttpURLConnection connection = getConnection(url, METHOD_DELETE);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testDelete() throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        fileId = response.getFileId();
        url = buildUrl(userId1, fileId);
        HttpURLConnection connection = getConnection(url, METHOD_DELETE);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testUnauthorized() throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        fileId = response.getFileId();
        url = buildUrl(userId1, fileId);
        HttpURLConnection connection = getConnectionWithUnauthToken(url, METHOD_DELETE, null);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.TOKENUNAUTHORIZED, showResult);
    }
    
    @Test
    public void testInvalidFileId() throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        fileId = response.getFileId();
        String url = buildUrl(userId1, -1L);
        HttpURLConnection connection = getConnection(url, METHOD_DELETE);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testNoSuchFile() throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        fileId = response.getFileId();
        String url = buildUrl(userId1, 999999L);
        HttpURLConnection connection = getConnection(url, METHOD_DELETE);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_FILE, showResult);
    }
    
    @Test
    public void testNoSuchFile1() throws Exception
    {
        RestFolderInfo folderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        String url = buildUrl(userId1, folderInfo.getId());
        HttpURLConnection connection = getConnection(url, METHOD_DELETE);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_FILE, showResult);
    }
    
    @Test
    public void testInvalidSpaceStatus() throws Exception
    {
//        FilePreUploadResponse response = uploadFile();
//        fileId = response.getFileId();
//        System.out.println(fileId);
        String url = buildUrl(userId1, 881);
        HttpURLConnection connection = getConnection(url, METHOD_DELETE);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_SPACE_STATUS, showResult);
    }
    
    
    private String buildUrl(long ownerId, long fileId)
    {
        return MyTestUtils.SERVER_URL_UFM_V2 + "files/" + ownerId + "/" + fileId;
    }
    
    
}
