package com.huawei.sharedrive.app.test.openapi.share;

import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.openapi.domain.node.FilePreUploadResponse;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.openapi.domain.share.RestPutShareRequestV2;
import com.huawei.sharedrive.app.openapi.domain.teamspace.RestTeamSpaceInfo;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

import pw.cdmi.core.utils.RandomGUID;

/**
 * 
 * @author pWX231110
 * 
 */
public class ShareAPIDeleteShareUserTest extends BaseAPITest
{
    private static final String INIT_DATA = "testData/share/initData.txt";
    
    private RestFolderInfo folderInfo1;
    
    private Long shareUserId;
    
    private FilePreUploadResponse fileResponse;
    
    private String ADD_DATA = "testData/share/deleteTest.txt";
    
    @Test
    public void testNormal() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(INIT_DATA, "addShareMessage");
        RestPutShareRequestV2 shareObject = JSonUtils.stringToObject(body, RestPutShareRequestV2.class);
        shareUserId = shareObject.getSharedUserList().get(0).getId();
        folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        buildUrl(userId1, folderInfo1.getId());
        // 两个参数都有情况 62 
        url += "?userId=" + shareUserId + "&type=user";
        HttpURLConnection connection = getConnection(url, METHOD_DELETE);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testNormal1() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(INIT_DATA, "addShareMessage");
        RestPutShareRequestV2 shareObject = JSonUtils.stringToObject(body, RestPutShareRequestV2.class);
        shareUserId = shareObject.getSharedUserList().get(0).getId();
        folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        buildUrl(userId1, folderInfo1.getId());
        
        // 只有type参数情况
        url += "?type=user";
        HttpURLConnection connection = getConnection(url, METHOD_DELETE);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testNormal3() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(INIT_DATA, "addShareMessage");
        RestPutShareRequestV2 shareObject = JSonUtils.stringToObject(body, RestPutShareRequestV2.class);
        shareUserId = shareObject.getSharedUserList().get(0).getId();
        folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        buildUrl(userId1, folderInfo1.getId());
        // 无参数情况
        HttpURLConnection connection = getConnection(url, METHOD_DELETE);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testNodeIdNegative() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(INIT_DATA, "addShareMessage");
        RestPutShareRequestV2 shareObject = JSonUtils.stringToObject(body, RestPutShareRequestV2.class);
        shareUserId = shareObject.getSharedUserList().get(0).getId();
        folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        buildUrl(userId1, -55L);
        url += "?userId=" + shareUserId + "&type=user";
        HttpURLConnection connection = getConnection(url, METHOD_DELETE);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_ITEM, showResult);
    }
    
    @Test
    public void testOwnerIdNegative() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(INIT_DATA, "addShareMessage");
        RestPutShareRequestV2 shareObject = JSonUtils.stringToObject(body, RestPutShareRequestV2.class);
        shareUserId = shareObject.getSharedUserList().get(0).getId();
        folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        buildUrl(-55L, folderInfo1.getId());
        url += "?userId=" + shareUserId + "&type=user";
        HttpURLConnection connection = getConnection(url, METHOD_DELETE);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.BAD_REQUEST, showResult);
    }
    
    @Test
    public void testOwnerIdError() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(INIT_DATA, "addShareMessage");
        RestPutShareRequestV2 shareObject = JSonUtils.stringToObject(body, RestPutShareRequestV2.class);
        shareUserId = shareObject.getSharedUserList().get(0).getId();
        folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        buildUrl(1424L, folderInfo1.getId());
        url += "?userId=" + shareUserId + "&type=user";
        HttpURLConnection connection = getConnection(url, METHOD_DELETE);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_ITEM, showResult);
    }
    
    @Test
    public void testOwnerIdErrorAndUserIdNotContain() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(INIT_DATA, "addShareMessage");
        RestPutShareRequestV2 shareObject = JSonUtils.stringToObject(body, RestPutShareRequestV2.class);
        shareUserId = shareObject.getSharedUserList().get(0).getId();
        folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        buildUrl(1424L, folderInfo1.getId());
        url += "?type=user";
        HttpURLConnection connection = getConnection(url, METHOD_DELETE);
        MyResponseUtils.assertReturnCode(connection, "Error", showResult);
    }
    
    @Test
    public void testOwnerIdErrorAndTypeNotContain() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(INIT_DATA, "addShareMessage");
        RestPutShareRequestV2 shareObject = JSonUtils.stringToObject(body, RestPutShareRequestV2.class);
        shareUserId = shareObject.getSharedUserList().get(0).getId();
        folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        buildUrl(1424L, folderInfo1.getId());
        url += "?userId=" + shareUserId;
        HttpURLConnection connection = getConnection(url, METHOD_DELETE);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.FORBIDDEN_OPER, showResult);
    }
    
    @Test
    public void testOwnerIdErrorButUserIdAndTypeNotContain() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(INIT_DATA, "addShareMessage");
        RestPutShareRequestV2 shareObject = JSonUtils.stringToObject(body, RestPutShareRequestV2.class);
        shareUserId = shareObject.getSharedUserList().get(0).getId();
        folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        buildUrl(1424L, folderInfo1.getId());
        HttpURLConnection connection = getConnection(url, METHOD_DELETE);
        MyResponseUtils.assertReturnCode(connection, "Error", showResult);
    }
    
    @Test
    public void testUnauthorized() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(INIT_DATA, "addShareMessage");
        RestPutShareRequestV2 shareObject = JSonUtils.stringToObject(body, RestPutShareRequestV2.class);
        shareUserId = shareObject.getSharedUserList().get(0).getId();
        folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        buildUrl(userId1, folderInfo1.getId());
        url += "?userId=" + shareUserId + "&type=user";
        HttpURLConnection connection = getConnectionWithUnauthToken(url, METHOD_DELETE, null);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.LOGINUNAUTHORIZED, showResult);
    }
    
    @Test
    public void testTypeNotExist() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(INIT_DATA, "addShareMessage");
        RestPutShareRequestV2 shareObject = JSonUtils.stringToObject(body, RestPutShareRequestV2.class);
        shareUserId = shareObject.getSharedUserList().get(0).getId();
        folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        buildUrl(userId1, folderInfo1.getId());
        url += "?userId=" + shareUserId + "&type=2323";
        HttpURLConnection connection = getConnection(url, METHOD_DELETE);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void teestUseIdNegative() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(INIT_DATA, "addShareMessage");
        RestPutShareRequestV2 shareObject = JSonUtils.stringToObject(body, RestPutShareRequestV2.class);
        shareUserId = shareObject.getSharedUserList().get(0).getId();
        folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        buildUrl(userId1, folderInfo1.getId());
        url += "?userId=-55&type=user";
        HttpURLConnection connection = getConnection(url, METHOD_DELETE);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.BAD_REQUEST, showResult);
    }
    
    @Test
    public void testNoShareUser() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(INIT_DATA, "addShareMessage");
        RestPutShareRequestV2 shareObject = JSonUtils.stringToObject(body, RestPutShareRequestV2.class);
        shareUserId = shareObject.getSharedUserList().get(0).getId();
        folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        buildUrl(userId1, folderInfo1.getId());
        url += "?userId=9999&type=user";
        HttpURLConnection connection = getConnection(url, METHOD_DELETE);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_ITEM, showResult);
    }
    
    @Test
    public void testNotExistNode() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(INIT_DATA, "addShareMessage");
        RestPutShareRequestV2 shareObject = JSonUtils.stringToObject(body, RestPutShareRequestV2.class);
        shareUserId = shareObject.getSharedUserList().get(0).getId();
        folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        buildUrl(userId1, 9999L);
        url += "?userId=" + shareUserId + "&type=user";
        HttpURLConnection connection = getConnection(url, METHOD_DELETE);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_ITEM, showResult);
    }
    
    @Test
    public void testSharedToDeleteRelation() throws Exception
    {
        
        // 上传文件DTS2014092607522
        String filePath = "D:/junitTestShare.jpg";
        fileResponse = uploadFile(filePath, 0L);
        addShare(fileResponse.getFileId());
        // DTS2014092607522
        buildUrl(userId1, fileResponse.getFileId());
        url += "?userId=" + shareUserId + "&type=user";
        URL urlRes = new URL(url);
        System.out.println("Request url : " + url);
        HttpURLConnection connection = (HttpURLConnection) urlRes.openConnection();
        connection.setRequestMethod(METHOD_DELETE);
        connection.setRequestProperty("Content-type", "application/json");
        connection.setRequestProperty("Authorization", MyTestUtils.getTestUserToken2());
        connection.setDoInput(true);
        connection.setDoOutput(true);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testTeamSpaceAndUploadFile() throws Exception
    {
        
        // DTS2014092607053
        RestTeamSpaceInfo spaceInfo = createUserTeamSpace1();
        String filePath = "D:/testSpace.jpg";
        FilePreUploadResponse fileReponse = uploadFile(filePath, spaceInfo.getId(),0L);
        addShare(fileReponse.getFileId());
        url += "?userId=" + shareUserId + "&type=user";
        HttpURLConnection connection = getConnection(url, METHOD_DELETE);
        MyResponseUtils.assert200(connection, showResult);
        
    }
    
    private void addShare(Long id) throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "shareships/" + userId1 + "/" + id;
        String body = MyFileUtils.getDataFromFile(ADD_DATA, "normal");
        HttpURLConnection connection = getConnection(urlString, METHOD_PUT, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testInvalidSpaceStatus() throws Exception
    {
        buildUrl(userId1, 235L);
        HttpURLConnection connection = getConnection(url, METHOD_DELETE);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_ITEM, showResult);
    }
    
    @Test
    public void testForbidden() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(INIT_DATA, "addShareMessage");
        RestPutShareRequestV2 shareObject = JSonUtils.stringToObject(body, RestPutShareRequestV2.class);
        shareUserId = shareObject.getSharedUserList().get(0).getId();
        folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        buildUrl(1888L, folderInfo1.getId());
        
        url += "?userId=" + shareUserId + "&type=user";
        HttpURLConnection connection = getConnection(url, METHOD_DELETE);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.FORBIDDEN_OPER, showResult);
    }
    
    private void buildUrl(Long ownerId, Long nodeId)
    {
        url = MyTestUtils.SERVER_URL_UFM_V2 + "shareships/" + ownerId + "/" + nodeId;
    }
    
}
