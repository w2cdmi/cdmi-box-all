package com.huawei.sharedrive.app.test.openapi.folder;

import java.net.HttpURLConnection;
import java.util.Date;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.openapi.domain.node.LinkForCopyRequest;
import com.huawei.sharedrive.app.openapi.domain.node.NodeCopyRequest;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.share.domain.INodeLink;
import com.huawei.sharedrive.app.test.openapi.FileBaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

import pw.cdmi.core.utils.JsonUtils;
import pw.cdmi.core.utils.RandomGUID;

/**
 * 文件夹复制测试类
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2014-7-11
 * @see
 * @since
 */
public class FolderAPICopyTest extends FileBaseAPITest
{
    
    private static final String LINK_CREATE_DATA = "testData/link/create.txt";
    
    private Long destParentId;
    
    private Long folderId;
    
    @Test
    public void testNormal() throws Exception
    {
        RestFolderInfo srcFolder = createFolder(userId1, new RandomGUID().getValueAfterMD5()+"SRC", 0L, new Date().getTime(), new Date().getTime());
        folderId = srcFolder.getId();
        url = buildUrl(userId1, folderId);
        RestFolderInfo destFolder = createRandomFolder();
        destParentId = destFolder.getId();
        String body = generalBody(userId1, destParentId, true);
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testUnauthorized() throws Exception
    {
        RestFolderInfo srcFolder = createRandomFolder();
        folderId = srcFolder.getId();
        url = buildUrl(userId1, folderId);
        RestFolderInfo destFolder = createRandomFolder();
        destParentId = destFolder.getId();
        String body = generalBody(userId1, destParentId, true);
        HttpURLConnection connection = getConnectionWithUnauthToken(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.TOKENUNAUTHORIZED, showResult);
    }
    
    @Test
    public void testInvalidFolderId() throws Exception
    {
        RestFolderInfo srcFolder = createRandomFolder();
        folderId = srcFolder.getId();
        url = buildUrl(userId1, folderId);
        RestFolderInfo destFolder = createRandomFolder();
        destParentId = destFolder.getId();
        String url = buildUrl(userId1, -1L);
        String body = generalBody(userId1, destParentId, true);
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testInvalidOwnerId() throws Exception
    {
        RestFolderInfo srcFolder = createRandomFolder();
        folderId = srcFolder.getId();
        url = buildUrl(userId1, folderId);
        RestFolderInfo destFolder = createRandomFolder();
        destParentId = destFolder.getId();
        String url = buildUrl(userId2, folderId);
        String body = generalBody(userId1, destParentId, true);
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_SOURCE, showResult);
    }
    
    @Test
    public void testNoSuchSource() throws Exception
    {
        RestFolderInfo destFolder = createRandomFolder();
        destParentId = destFolder.getId();
        long srcFolderId = 99999999;
        String url = buildUrl(userId1, srcFolderId);
        String body = generalBody(userId1, destParentId, true);
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_SOURCE, showResult);
    }
    
    @Test
    public void testNoSuchDest() throws Exception
    {
        RestFolderInfo srcFolder = createRandomFolder();
        folderId = srcFolder.getId();
        url = buildUrl(userId1, folderId);
        String body = generalBody(userId1, 99999L, true);
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_DEST, showResult);
    }
    
    @Test
    public void testRepeatName() throws Exception
    {
        RestFolderInfo srcFolder = createRandomFolder();
        folderId = srcFolder.getId();
        url = buildUrl(userId1, folderId);
        RestFolderInfo destFolder = createRandomFolder();
        destParentId = destFolder.getId();
        // 将同一个文件夹往相同目录复制两次
        String body = generalBody(userId1, destParentId, true);
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assert200(connection, showResult);
        
        body = generalBody(userId1, destParentId, false);
        connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.FILES_CONFLICT, showResult);
    }
    
    @Test
    public void testSubFolderConflict() throws Exception
    {
        RestFolderInfo srcFolder = createRandomFolder();
        folderId = srcFolder.getId();
        url = buildUrl(userId1, folderId);
        RestFolderInfo destFolder = createRandomFolder();
        destParentId = destFolder.getId();
        // 创建源文件夹的子文件夹
        RestFolderInfo subFolder = createFolder(userId1, "subFoderForSrc", folderId);
        String body = generalBody(userId1, subFolder.getId(), true);
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.SUB_FOLDER_CONFILICT, showResult);
    }
    
    @Test
    public void testSameParentConflict() throws Exception
    {
        RestFolderInfo srcFolder = createRandomFolder();
        folderId = srcFolder.getId();
        url = buildUrl(userId1, folderId);
        RestFolderInfo subFolder = createFolder(userId1, "testSameParent", folderId);
        String url = buildUrl(userId1, subFolder.getId());
        String body = generalBody(userId1, folderId, true);
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.SAME_PARENT_CONFILICT, showResult);
    }
    
    @Test
    public void testLinkNoPlainAccessCode() throws Exception
    {
        // 待复制的文件夹
        RestFolderInfo folderInfoOne = createFolder(userId1, new RandomGUID().getValueAfterMD5() + "Copy", 0l);
        
        // 装被复制的文件夹
        RestFolderInfo folderInfoTwo = createFolder(userId1,
            new RandomGUID().getValueAfterMD5() + "Contain",
            0l);
     // 创建外链 无painAccessCode
        INodeLink linkV2 = createLinkByPlianAccessCode(userId1, folderInfoOne.getId(), null);
        NodeCopyRequest request = new NodeCopyRequest();
        request.setDestOwnerId(userId1);
//        LinkForCopyRequest link = new LinkForCopyRequest();
//        link.setLinkCode(linkV2.getId());
//        request.setLink(link);
        request.setDestParent(folderInfoTwo.getId());
        request.setAutoRename(true);
        String body = JsonUtils.toJson(request);
        System.out.println("Request Body:" + body);
        
        url = buildUrl(userId1, folderInfoOne.getId());
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    //DTS2014070308909   用户1与用户2间复制文件夹
    @Test
    public void testLinkNoPlainAccessCodeToAnotherUser() throws Exception
    {
        RestFolderInfo folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5() +"SRC", 0L);
        RestFolderInfo folderInfo2 = createFolder(userId2, new RandomGUID().getValueAfterMD5() +"DESC", 0L, MyTestUtils.getTestUserToken2());
        System.out.println("创建无提取码外链=============>");
        // 创建外链 无painAccessCode
        INodeLink linkV2 = createLinkByPlianAccessCode(userId1, folderInfo1.getId(), null);
        NodeCopyRequest request = new NodeCopyRequest();
        request.setDestOwnerId(userId2);
        LinkForCopyRequest link = new LinkForCopyRequest();
        link.setLinkCode(linkV2.getId());
        request.setLink(link);
        request.setDestParent(folderInfo2.getId());
        request.setAutoRename(true);
        String body = JsonUtils.toJson(request);
        System.out.println("Request Body:" + body);
        
        url = buildUrl(userId1, folderInfo1.getId());
        HttpURLConnection connection = getConnection(url, METHOD_PUT, MyTestUtils.getTestUserToken2(), body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testOutOfQuota() throws Exception
    {
        // 需要测试人员手动修改user1的spaceQuota
        RestFolderInfo srcFolder = createRandomFolder();
        folderId = srcFolder.getId();
        url = buildUrl(userId1, folderId);
        RestFolderInfo destFolder = createRandomFolder();
        destParentId = destFolder.getId();
        buildUrl(userId1, folderId);
        String body = generalBody(userId1, destParentId, true);
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.EXCEED_SPACE_QUOTA, showResult);
    }
    
    // 需改用户的status状态
    @Test
    public void testInvalidSpaceStatus() throws Exception
    {
        url = buildUrl(userId1, 299L);
        String body = generalBody(userId1, 400L, true);
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_SPACE_STATUS, showResult);
    }
    
    @Test
    public void testRepeatNameConflict() throws Exception
    {
        String name = new RandomGUID().getValueAfterMD5() + "SRC";
        RestFolderInfo srcFolderInfo = createFolder(userId1, name, 0L);
        RestFolderInfo destFolderInfo = createFolder(userId1,
            new RandomGUID().getValueAfterMD5() + "DEST",
            0L);
        
        // 在目标文件创建与源文件同名的文件夹
        createFolder(userId1, name, destFolderInfo.getId());
        url = buildUrl(userId1, srcFolderInfo.getId());
        String body = generalBody(userId1, destFolderInfo.getId(), false);
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.FILES_CONFLICT, showResult);
    }
    
    // 目标文件未源文件的子文件
    @Test
    public void testSubfolderConflict() throws Exception
    {
        RestFolderInfo srcFolderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5() + "SRC", 0L);
        
        RestFolderInfo destFolderInfo = createFolder(userId1,
            new RandomGUID().getValueAfterMD5() + "Child",
            srcFolderInfo.getId());
        url = buildUrl(userId1, srcFolderInfo.getId());
        String body = generalBody(userId1, destFolderInfo.getId(), false);
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.SUB_FOLDER_CONFILICT, showResult);
    }
    
    // 目标文件未源文件的父文件
    @Test
    public void testSameParentConflict1() throws Exception
    {
        RestFolderInfo destFolderInfo = createFolder(userId1,
            new RandomGUID().getValueAfterMD5() + "DEST",
            0L);
        RestFolderInfo srcFolderInfo = createFolder(userId1,
            new RandomGUID().getValueAfterMD5() + "SRC",
            destFolderInfo.getId());
        url = buildUrl(userId1, srcFolderInfo.getId());
        String body = generalBody(userId1, destFolderInfo.getId(), true);
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.SAME_PARENT_CONFILICT, showResult);
    }
    
    @Test
    public void testPlainCode() throws Exception
    {
        RestFolderInfo srcFolderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5() +"SRC", 0L);
        RestFolderInfo destFolderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5() +"DEST", 0L);
        String urlLink = MyTestUtils.SERVER_URL_UFM_V2 + "links/" + userId1 + "/" + srcFolderInfo.getId();
        
        String body = MyFileUtils.getDataFromFile(LINK_CREATE_DATA, "noContainPlianAccessCode");
        HttpURLConnection connection = getConnection(urlLink, METHOD_POST, body);
        MyResponseUtils.assert201(connection, showResult);
        
    }
    
    @Test
    public void testContainAll() throws Exception
    {
        RestFolderInfo srcFolder = createFolder(userId1, new RandomGUID().getValueAfterMD5() +"SRC", 0L);
        folderId = srcFolder.getId();
        url = buildUrl(userId1, folderId);
        RestFolderInfo destFolder = createFolder(userId1, new RandomGUID().getValueAfterMD5() +"DEST", 0L);
        destParentId = destFolder.getId();
        INodeLink linkV2 = createLinkByPlianAccessCode(userId1, destParentId, "srcToDsds");
        NodeCopyRequest request = new NodeCopyRequest();
        request.setDestOwnerId(userId1);
        request.setDestParent(destParentId);
        request.setAutoRename(true);
        String dateStr = MyTestUtils.getDateString();
        LinkForCopyRequest linkCopy = new LinkForCopyRequest();
        linkCopy.setPlainAccessCode(MyTestUtils.getLinkSignature(linkV2.getPlainAccessCode(), dateStr));
        linkCopy.setLinkCode(linkV2.getId());
        request.setLink(linkCopy);
        String body = JsonUtils.toJson(request);
        System.out.println(body);
        HttpURLConnection connection = getConnection(url, METHOD_PUT, MyTestUtils.getTestUserToken1(),null, dateStr , body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    private String buildUrl(Long ownerId, Long srcFolderId)
    {
        return MyTestUtils.SERVER_URL_UFM_V2 + "folders/" + ownerId + "/" + srcFolderId + "/copy";
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
