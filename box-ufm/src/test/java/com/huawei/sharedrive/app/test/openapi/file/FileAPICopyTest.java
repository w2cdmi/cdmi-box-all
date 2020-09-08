package com.huawei.sharedrive.app.test.openapi.file;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.openapi.domain.node.FilePreUploadResponse;
import com.huawei.sharedrive.app.openapi.domain.node.LinkForCopyRequest;
import com.huawei.sharedrive.app.openapi.domain.node.NodeCopyRequest;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.share.domain.INodeLink;
import com.huawei.sharedrive.app.test.openapi.FileBaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

import pw.cdmi.core.utils.JsonUtils;
import pw.cdmi.core.utils.RandomGUID;

/**
 * 文件复制测试类
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2014-7-7
 * @see
 * @since
 */
public class FileAPICopyTest extends FileBaseAPITest
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
    public void testInvalidOwnerId() throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        fileId = response.getFileId();
        url = buildUrl(userId1, fileId);
        RestFolderInfo folderInfo = createRandomFolder();
        destParentId = folderInfo.getId();
        String body = generalBody(999999L, destParentId, true);
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_USER, showResult);
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
    public void testInvalidSrcFileId() throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        fileId = response.getFileId();
        RestFolderInfo folderInfo = createRandomFolder();
        destParentId = folderInfo.getId();
        String url = buildUrl(userId1, -1L);
        String body = generalBody(userId1, destParentId, true);
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_SOURCE, showResult);
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
        
        // 一个文件往相同目录复制两次
        String body = generalBody(userId1, destParentId, true);
        HttpURLConnection connection1 = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assert200(connection1, showResult);
        
        body = generalBody(userId1, destParentId, false);
        HttpURLConnection connection2 = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection2, ErrorCode.FILES_CONFLICT, showResult);
    }
    
    @Test
    public void testInvalidSpaceStatus() throws Exception
    {
        // FilePreUploadResponse response = uploadFile();
        // fileId = response.getFileId();
        // RestFolderInfo folderInfo = createRandomFolder();
        // destParentId = folderInfo.getId();
        // System.out.println("FileId:"+fileId+"," +"DestId:"+destParentId);
        url = buildUrl(userId1, 881L);
        String body = generalBody(userId1, 904L, true);
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_SPACE_STATUS, showResult);
    }
    
  //DTS2014070308909   用户1与用户2间复制文件夹
    @Test
    public void testLinkNoPlainAccessCodeToAnotherUser() throws Exception
    {
        RestFolderInfo folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5() +"SRCParent", 0L);
        FilePreUploadResponse fileReponse = uploadFile("D:/junitTest.jpg", folderInfo1.getId());
        RestFolderInfo folderInfo2 = createFolder(userId2,new RandomGUID().getValueAfterMD5() +"DEST", 0L);
        // 创建外链 无painAccessCode
        INodeLink linkV2 = createLinkByPlianAccessCode(userId1, fileReponse.getFileId(), null);
        NodeCopyRequest request = new NodeCopyRequest();
        request.setDestOwnerId(userId2);
        LinkForCopyRequest link = new LinkForCopyRequest();
        link.setLinkCode(linkV2.getId());
        request.setLink(link);
        request.setDestParent(folderInfo2.getId());
        request.setAutoRename(true);
        String body = JsonUtils.toJson(request);
        System.out.println("Request Body:" + body);
        
        url = buildUrl(userId1, fileReponse.getFileId());
        HttpURLConnection connection = getConnection(url, METHOD_PUT, MyTestUtils.getTestUserToken2(), body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testNoLinkCode() throws Exception
    {
        RestFolderInfo folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5() +"SRCParent", 0L);
        FilePreUploadResponse fileReponse = uploadFile("D:/junitTest.jpg", folderInfo1.getId());
        RestFolderInfo folderInfo2 = createFolder(userId2,new RandomGUID().getValueAfterMD5() +"DEST", 0L);
        NodeCopyRequest request = new NodeCopyRequest();
        request.setDestOwnerId(userId2);
        request.setDestParent(folderInfo2.getId());
        request.setAutoRename(true);
        String body = JsonUtils.toJson(request);
        System.out.println("Request Body:" + body);
        
        url = buildUrl(userId1, fileReponse.getFileId());
        HttpURLConnection connection = getConnection(url, METHOD_PUT, MyTestUtils.getTestUserToken2(), body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testContainAll() throws Exception
    {
        RestFolderInfo folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5() +"SRCParent", 0L);
        FilePreUploadResponse fileResponse = uploadFile("D:/junitTest.jpg", folderInfo1.getId());
        RestFolderInfo folderInfo2 = createFolder(userId1,new RandomGUID().getValueAfterMD5() +"DEST", 0L);
        INodeLink link = createLinkByPlianAccessCode(userId1, fileResponse.getFileId(), "sdfwsdsfr");
        NodeCopyRequest request = new NodeCopyRequest();
        request.setDestOwnerId(userId1);
        request.setDestParent(folderInfo2.getId());
        request.setAutoRename(true);
        LinkForCopyRequest linkCopy = new LinkForCopyRequest();
        String dateStr = MyTestUtils.getDateString();
        linkCopy.setLinkCode(link.getId());
        linkCopy.setPlainAccessCode(MyTestUtils.getLinkSignature(link.getPlainAccessCode(), dateStr));
        request.setLink(linkCopy);
        String body = JsonUtils.toJson(request);
        System.out.println(body);
        url = buildUrl(userId1, fileResponse.getFileId());
        HttpURLConnection connection = getConnection(url, METHOD_PUT, MyTestUtils.getTestUserToken1(),null, dateStr, body);
        MyResponseUtils.assert200(connection, showResult);
        
    }
    
    private String buildUrl(long ownerId, long fileId)
    {
        return MyTestUtils.SERVER_URL_UFM_V2 + "files/" + ownerId + "/" + fileId + "/copy";
    }
    
    private String generalBody(Long destOwnerId, Long destParentId, Boolean autoRename)
    {
        NodeCopyRequest request = new NodeCopyRequest();
        request.setDestOwnerId(destOwnerId);
        request.setDestParent(destParentId);
        request.setAutoRename(autoRename);
        return JsonUtils.toJson(request);
    }
}
