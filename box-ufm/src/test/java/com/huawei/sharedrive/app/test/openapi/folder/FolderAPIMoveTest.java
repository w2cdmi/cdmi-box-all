package com.huawei.sharedrive.app.test.openapi.folder;

import java.net.HttpURLConnection;
import java.util.Date;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.openapi.domain.node.NodeMoveRequest;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.test.openapi.FileBaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

import pw.cdmi.core.utils.JsonUtils;
import pw.cdmi.core.utils.RandomGUID;

/**
 * 文件夹移动测试类
 * 
 * @author  t90006461
 * @version  CloudStor CSE Service Platform Subproject, 2014-7-11
 * @see  
 * @since  
 */
public class FolderAPIMoveTest extends FileBaseAPITest
{
    private Long srcFolderId;

    private Long destParentId;
    
    @Test
    public void testNormal() throws Exception
    {
        RestFolderInfo   destFolder = createRandomFolder(); 
        RestFolderInfo srcFolder = createFolder(userId1, new RandomGUID().getValueAfterMD5()+"SRC", 0L, new Date().getTime(), new Date().getTime());
        destParentId = destFolder.getId();
      
        srcFolderId = srcFolder.getId();
        url =  buildUrl(userId1, srcFolderId);
        String body = generalBody(userId1, destParentId, true);
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testUnauthorized() throws Exception
    {
        RestFolderInfo destFolder = createRandomFolder();
        destParentId = destFolder.getId();
        RestFolderInfo srcFolder = createRandomFolder();
        srcFolderId = srcFolder.getId();
        url =  buildUrl(userId1, srcFolderId);
        String body = generalBody(userId1, destParentId, true);
        HttpURLConnection connection = getConnectionWithUnauthToken(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.TOKENUNAUTHORIZED, showResult);
    }
    
    @Test
    public void testInvalidOwnerId() throws Exception
    {
        RestFolderInfo destFolder = createRandomFolder();
        destParentId = destFolder.getId();
        RestFolderInfo srcFolder = createRandomFolder();
        srcFolderId = srcFolder.getId();
        url =  buildUrl(userId1, srcFolderId);
        String url = buildUrl(-1L, srcFolderId);
        String body = generalBody(userId1, destParentId, true);
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testInvalidFolderId() throws Exception
    {
        RestFolderInfo destFolder = createRandomFolder();
        destParentId = destFolder.getId();
        RestFolderInfo srcFolder = createRandomFolder();
        srcFolderId = srcFolder.getId();
        url =  buildUrl(userId1, srcFolderId);
        String url = buildUrl(userId1, -1L);
        String body = generalBody(userId1, destParentId, true);
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testNoSuchSource() throws Exception
    {
        RestFolderInfo destFolder = createRandomFolder();
        destParentId = destFolder.getId();
        RestFolderInfo srcFolder = createRandomFolder();
        srcFolderId = srcFolder.getId();
        url =  buildUrl(userId1, srcFolderId);
        long srcFolderId = 99999999;
        String url = buildUrl(userId1, srcFolderId);
        String body = generalBody(userId1, destParentId, true);
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_SOURCE, showResult);
    }
    
    @Test
    public void testNoSuchDest() throws Exception
    {
        RestFolderInfo destFolder = createRandomFolder();
        destParentId = destFolder.getId();
        RestFolderInfo srcFolder = createRandomFolder();
        srcFolderId = srcFolder.getId();
        url =  buildUrl(userId1, srcFolderId);
        String body = generalBody(userId1, 99999L, true);
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_DEST, showResult);
    }
    
    @Test
    public void testRepeatName() throws Exception
    {
        String folderName = "testRepeatNameFolder";
        createFolder(userId1, folderName, destParentId);
        RestFolderInfo srcFolder = createFolder(userId1, folderName, 0L);
        
        String url = buildUrl(userId1, srcFolder.getId());
        String body = generalBody(userId1, destParentId, false);
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.FILES_CONFLICT, showResult);
    }
    
    @Test
    public void testSubFolderConflict() throws Exception
    {
        RestFolderInfo destFolder = createRandomFolder();
        destParentId = destFolder.getId();
        RestFolderInfo srcFolder = createRandomFolder();
        srcFolderId = srcFolder.getId();
        url =  buildUrl(userId1, srcFolderId);
        RestFolderInfo subFolder = createFolder(userId1, "subFolder", srcFolderId);
        String body = generalBody(userId1, subFolder.getId(), true);
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.SUB_FOLDER_CONFILICT, showResult);
    }
    
    @Test
    public void testSameParentConflict() throws Exception
    {
        RestFolderInfo destFolder = createRandomFolder();
        destParentId = destFolder.getId();
        RestFolderInfo srcFolder = createRandomFolder();
        srcFolderId = srcFolder.getId();
        url =  buildUrl(userId1, srcFolderId);
        RestFolderInfo folderInfo = createFolder(userId1, "testSameParentConflict", srcFolderId);
        String url = buildUrl(userId1, folderInfo.getId());
        String body = generalBody(userId1, srcFolderId, true);
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.SAME_PARENT_CONFILICT, showResult);
    }
    
    @Test
    public void testMoveFromOtherUser() throws Exception
    {
        // 用户2创建文件夹
        String folderName = new RandomGUID().getValueAfterMD5();
        RestFolderInfo srcFolderInfo = createFolder(userId2, folderName, 0L, MyTestUtils.getTestUserToken2());
        String url = buildUrl(userId2, srcFolderInfo.getId());
        
        // 将用户2文件夹移动至用户1根目录下
        String body = generalBody(userId1, 0L, true);
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.FORBIDDEN_OPER, showResult);
    }
    
    @Test
    public void testInvalidSpaceStatus() throws Exception
    {
//        RestFolderInfo folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5() + "Father", 0L);
//        RestFolderInfo folderInfo2 = createFolder(userId1, new RandomGUID().getValueAfterMD5() + "Father", 0L);
//        System.out.println(folderInfo1.getId()+"==" + folderInfo2.getId());
//        
        String url = buildUrl(userId1,802L);
        String body = generalBody(userId1, 803L, true);
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_SPACE_STATUS, showResult);
    }
    
    private String buildUrl(Long ownerId, Long srcFolderId)
    {
        return MyTestUtils.SERVER_URL_UFM_V2 + "folders/" + ownerId + "/" + srcFolderId + "/move";
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
