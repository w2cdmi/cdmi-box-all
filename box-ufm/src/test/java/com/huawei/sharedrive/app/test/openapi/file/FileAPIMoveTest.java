package com.huawei.sharedrive.app.test.openapi.file;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.openapi.domain.node.FilePreUploadResponse;
import com.huawei.sharedrive.app.openapi.domain.node.NodeMoveRequest;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.test.openapi.FileBaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

import pw.cdmi.core.utils.JsonUtils;

/**
 * 文件移动测试类
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2014-7-7
 * @see
 * @since
 */
public class FileAPIMoveTest extends FileBaseAPITest
{
    private Long destParentId;
    
    private Long fileId;
    
    @Test
    public void testNormal() throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        fileId = response.getFileId();
        url = buildUrl(userId1, fileId);
        RestFolderInfo folderInfo = createRandomFolder();
        destParentId = folderInfo.getId();
        String body = generalBody(userId1, destParentId, true);
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testUnauthorized() throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        fileId = response.getFileId();
        url = buildUrl(userId1, fileId);
        RestFolderInfo folderInfo = createRandomFolder();
        destParentId = folderInfo.getId();
        String body = generalBody(userId1, destParentId, true);
        HttpURLConnection connection = getConnectionWithUnauthToken(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.TOKENUNAUTHORIZED, showResult);
    }
    
    @Test
    public void testInvalidDestFolderId() throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        fileId = response.getFileId();
        url = buildUrl(userId1, fileId);
        RestFolderInfo folderInfo = createRandomFolder();
        destParentId = folderInfo.getId();
        String body = generalBody(userId1, -1L, true);
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testInvalidOwnerId() throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        fileId = response.getFileId();
        url = buildUrl(userId1, fileId);
        RestFolderInfo folderInfo = createRandomFolder();
        destParentId = folderInfo.getId();
        String url = buildUrl(999999L, fileId);
        String body = generalBody(userId1, destParentId, true);
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_SOURCE, showResult);
    }
    
    
    @Test
    public void testInvalidSrcFileId() throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        fileId = response.getFileId();
        url = buildUrl(userId1, fileId);
        RestFolderInfo folderInfo = createRandomFolder();
        destParentId = folderInfo.getId();
        String url = buildUrl(userId1, -1L);
        String body = generalBody(userId1, destParentId, true);
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testNoSuchSource() throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        fileId = response.getFileId();
        url = buildUrl(userId1, fileId);
        RestFolderInfo folderInfo = createRandomFolder();
        destParentId = folderInfo.getId();
        String url = buildUrl(userId1, 999999L);
        String body = generalBody(userId1, destParentId, true);
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_SOURCE, showResult);
    }
    
    @Test
    public void testNoSuchDest() throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        fileId = response.getFileId();
        url = buildUrl(userId1, fileId);
        RestFolderInfo folderInfo = createRandomFolder();
        destParentId = folderInfo.getId();
        String body = generalBody(userId1, 999999L, true);
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_DEST, showResult);
    }
    
    @Test
    public void testRepeatName() throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        fileId = response.getFileId();
        url = buildUrl(userId1, fileId);
        RestFolderInfo folderInfo = createRandomFolder();
        destParentId = folderInfo.getId();
        // 移动文件后上传相同文件, 再移动到相同目录
        String body = generalBody(userId1, destParentId, true);
        HttpURLConnection connection1 = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assert200(connection1, showResult);
        
        
        FilePreUploadResponse response1 = uploadFile();
        String url = buildUrl(userId1, response1.getFileId());
        body = generalBody(userId1, destParentId, false);
        HttpURLConnection connection2 = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection2, ErrorCode.FILES_CONFLICT, showResult);
    }
    
    @Test
    public void testSameParent() throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        fileId = response.getFileId();
        url = buildUrl(userId1, fileId);
        RestFolderInfo folderInfo = createRandomFolder();
        destParentId = folderInfo.getId();
        String body = generalBody(userId1, fileParentId, true);
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.SAME_PARENT_CONFILICT, showResult);
    }
    
    @Test
    public void testInvalidSpaceStatus() throws Exception
    {
//        FilePreUploadResponse response = uploadFile();
//        fileId = response.getFileId();
//        RestFolderInfo folderInfo = createRandomFolder();
//        destParentId = folderInfo.getId();
//        System.out.println("FileId:"+fileId + ",DestParentId:" + destParentId);
        url = buildUrl(userId1, 921L);
        String body = generalBody(userId1, 925L, true);
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_SPACE_STATUS, showResult);
    }
    
    private String buildUrl(long ownerId, long fileId)
    {
        return MyTestUtils.SERVER_URL_UFM_V2 + "files/" + ownerId + "/" + fileId + "/move";
    }
    
    private String generalBody(Long destOwnerId, Long destParentId, Boolean autoRename)
    {
        NodeMoveRequest request = new NodeMoveRequest();
        request.setDestOwnerId(destOwnerId);
        request.setDestParent(destParentId);
        request.setAutoRename(autoRename);
        return JsonUtils.toJson(request);
    }
}
