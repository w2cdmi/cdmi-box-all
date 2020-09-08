package com.huawei.sharedrive.app.test.openapi.share;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.openapi.domain.node.FilePreUploadResponse;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.openapi.domain.teamspace.RestTeamSpaceInfo;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

import pw.cdmi.core.utils.RandomGUID;

/**
 * 添加共享人测试类
 * 
 */
public class ShareAPIAddShareTest extends BaseAPITest
{
    /**
     * 存储共享关系数据
     */
    private static final String SHARE_DATA = "testData/share/addShare.txt";
    
    private RestFolderInfo foldInfo;
    
    @Test
    public void testNormal() throws Exception
    {
        foldInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5() + "Share", 0L);
        buildUrl(userId1, foldInfo.getId());
        String body = MyFileUtils.getDataFromFile(SHARE_DATA, "normal");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testNormalEditor() throws Exception
    {
        foldInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5() + "Share", 0L);
        buildUrl(userId1, foldInfo.getId());
        String body = MyFileUtils.getDataFromFile(SHARE_DATA, "normalEditor");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testInvalidRole() throws Exception
    {
        foldInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5() + "Share", 0L);
        buildUrl(userId1, foldInfo.getId());
        String body = MyFileUtils.getDataFromFile(SHARE_DATA, "invalidRole");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assert400(connection, showResult);
    }
    
    @Test
    public void testEmptyRole() throws Exception
    {
        foldInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5() + "Share", 0L);
        buildUrl(userId1, foldInfo.getId());
        String body = MyFileUtils.getDataFromFile(SHARE_DATA, "emptyRole");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testEmptyMessage() throws Exception
    {
        foldInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5() + "Share", 0L);
        buildUrl(userId1, foldInfo.getId());
        String body = MyFileUtils.getDataFromFile(SHARE_DATA, "emptyMessage");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testMessageContainSymbol() throws Exception
    {
        foldInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5() + "Share", 0L);
        buildUrl(userId1, foldInfo.getId());
        String body = MyFileUtils.getDataFromFile(SHARE_DATA, "messageContainSymbol");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testBadRequest() throws Exception
    {
        foldInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5() + "Share", 0L);
        buildUrl(-3434l, foldInfo.getId());
        String body = MyFileUtils.getDataFromFile(SHARE_DATA, "normal");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.BAD_REQUEST, showResult);
    }
    
    @Test
    public void testNoSuchItem() throws Exception
    {
        buildUrl(userId1, 8855l);
        String body = MyFileUtils.getDataFromFile(SHARE_DATA, "normal");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_ITEM, showResult);
    }
    
    @Test
    public void testUnauthorized() throws Exception
    {
        foldInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5() + "Share", 0L);
        buildUrl(userId1, foldInfo.getId());
        String body = MyFileUtils.getDataFromFile(SHARE_DATA, "normal");
        HttpURLConnection connection = getConnectionWithUnauthToken(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.TOKENUNAUTHORIZED, showResult);
    }
    
    // DTS2014111006696
    @Test
    public void testNoUser() throws Exception
    {
        foldInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5() + "Share", 0L);
        buildUrl(userId1, foldInfo.getId());
        String body = MyFileUtils.getDataFromFile(SHARE_DATA, "noUser");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_USER, showResult);
    }
    
    @Test
    public void testInvalidParam() throws Exception
    {
        foldInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5() + "Share", 0L);
        buildUrl(userId1, foldInfo.getId());
        String body = MyFileUtils.getDataFromFile(SHARE_DATA, "invalidParam");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testContainChildFile() throws Exception
    {
        // DTS2014100903602
        RestFolderInfo folderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5() + "FATHER", 0L);
        FilePreUploadResponse fileResponse = uploadFile("D:/testExcel.xlsx", userId1, folderInfo.getId());
        uploadFile("D:/testExcel1.xlsx", userId1, folderInfo.getId());
        uploadFile("D:/testExcel2.xlsx", userId1, folderInfo.getId());
        uploadFile("D:/testExcel3.xlsx", userId1, folderInfo.getId());
        System.out.println("To Delete file id :" + fileResponse.getFileId());
        buildUrl(userId1, folderInfo.getId());
        String body = MyFileUtils.getDataFromFile(SHARE_DATA, "normal");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testInvalidSpaceStatus() throws Exception
    {
        buildUrl(userId1, 999L);
        String body = MyFileUtils.getDataFromFile(SHARE_DATA, "normal");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_SPACE_STATUS, showResult);
    }
    
    @Test
    public void testAddShareToTeamSpace() throws Exception
    {
        // DTS2014092502016
        FilePreUploadResponse file = uploadFile("D:/testTeamSpace.txt", userId1, 0L);
        RestTeamSpaceInfo spaceInfo = createTeamSpace(MyTestUtils.getTestUserToken2());
        buildUrl(userId1, file.getFileId());
        String body = MyFileUtils.getDataFromFile(SHARE_DATA, "teamSpace");
        body = body.replaceAll("#spaceId#", "" + spaceInfo.getId());
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_USER, showResult);
    }
    
    // DTS2014103101793
    @Test
    public void testTeamSpaceAddToOther() throws Exception
    {
        RestTeamSpaceInfo spaceInfo = createTeamSpace(MyTestUtils.getTestUserToken1());
        FilePreUploadResponse file = uploadFile("D:/testTeamSpace.txt", spaceInfo.getId(), 0L);
        buildUrl(spaceInfo.getId(), file.getFileId());
        String body = MyFileUtils.getDataFromFile(SHARE_DATA, "teamSpace");
        body = body.replaceAll("#spaceId#", "" + userId2);
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.FORBIDDEN_OPER, showResult);
    }
    
    @Test
    public void testFileNormal() throws Exception
    {
        FilePreUploadResponse file = uploadFile("D:/testTeamSpace.txt", userId1, 0L);
        buildUrl(userId1, file.getFileId());
        System.out.println("File id:" + file.getFileId());
        String body = MyFileUtils.getDataFromFile(SHARE_DATA, "normal");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testForbidden() throws Exception
    {
        foldInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5() + "Share", 0L);
        buildUrl(userId2, foldInfo.getId());
        String body = MyFileUtils.getDataFromFile(SHARE_DATA, "normal");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.FORBIDDEN_OPER, showResult);
    }
    
    @Test
    public void testShareFileAndDeleteParent() throws Exception
    {
        RestFolderInfo folderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5() + "Parent", 0L);
        System.out.println(folderInfo.getId());
        FilePreUploadResponse fileResponse = uploadFile("D:/junitTest.jpg", folderInfo.getId());
        buildUrl(userId1, fileResponse.getFileId());
        String body = MyFileUtils.getDataFromFile(SHARE_DATA, "normal");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assert200(connection, showResult);
        
    }
    
    private void buildUrl(Long ownerId, Long nodeId)
    {
        url = MyTestUtils.SERVER_URL_UFM_V2 + "shareships/" + ownerId + "/" + nodeId;
    }
    
}
