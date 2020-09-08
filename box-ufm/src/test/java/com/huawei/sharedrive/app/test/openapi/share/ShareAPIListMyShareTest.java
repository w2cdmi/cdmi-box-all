package com.huawei.sharedrive.app.test.openapi.share;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.openapi.domain.node.FilePreUploadResponse;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

import pw.cdmi.core.utils.RandomGUID;

public class ShareAPIListMyShareTest extends BaseAPITest
{
    
    private static final String SHARE_DATA = "testData/share/addShare.txt";
    
    public ShareAPIListMyShareTest()
    {
        url = MyTestUtils.SERVER_URL_UFM_V2 + "shares/distributed";
    }
    
    /**
     * 所有创建文件夹数据
     */
    private static final String LIST_MYSHARE = "testData/share/listMyShare.txt";
    
    @Test
    public void testNormal() throws Exception
    {
//        RestFolderInfo folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5() +"ListMyShare", 0L);
//        RestFolderInfo folderInfo2 = createFolder(userId1, new RandomGUID().getValueAfterMD5() +"ListMyShare", 0L);
//        
//        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
//        addShare(userId1, folderInfo2.getId(), SHARE_ADD, "addShareMessage");
        String body = MyFileUtils.getDataFromFile(LIST_MYSHARE, "normal");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testNormalWithThumb() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(LIST_MYSHARE, "normalThumb");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testOnlyContainOffset() throws Exception
    {
        RestFolderInfo folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5() +"ListMyShare", 0L);
        RestFolderInfo folderInfo2 = createFolder(userId1, new RandomGUID().getValueAfterMD5() +"ListMyShare", 0L);
        
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, folderInfo2.getId(), SHARE_ADD, "addShareMessage");
        String body = MyFileUtils.getDataFromFile(LIST_MYSHARE, "onlyContainOffset");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testOnlyContainOffsetAndOffsetNegative() throws Exception
    {
        RestFolderInfo folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5() +"ListMyShare", 0L);
        RestFolderInfo folderInfo2 = createFolder(userId1, new RandomGUID().getValueAfterMD5() +"ListMyShare", 0L);
        
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, folderInfo2.getId(), SHARE_ADD, "addShareMessage");
        String body = MyFileUtils.getDataFromFile(LIST_MYSHARE, "onlyContainOffsetAndOffsetNegative");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testContainAll() throws Exception
    {
//        RestFolderInfo folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5() +"ListMyShare", 0L);
//        RestFolderInfo folderInfo2 = createFolder(userId1, new RandomGUID().getValueAfterMD5() +"ListMyShare", 0L);
//        
//        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
//        addShare(userId1, folderInfo2.getId(), SHARE_ADD, "addShareMessage");
        
        FilePreUploadResponse file = uploadFile("D:/junitTest.jpg", userId1, 0L);
//         设置共享关系
        addShare(userId1, file.getFileId(), SHARE_ADD, "addShareMessage1");
        String body = MyFileUtils.getDataFromFile(LIST_MYSHARE, "containAll");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testOnlyContainOrder() throws Exception
    {
        RestFolderInfo folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5() +"ListMyShare", 0L);
        RestFolderInfo folderInfo2 = createFolder(userId1, new RandomGUID().getValueAfterMD5() +"ListMyShare", 0L);
        
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, folderInfo2.getId(), SHARE_ADD, "addShareMessage");
        String body = MyFileUtils.getDataFromFile(LIST_MYSHARE, "onlyContainOrder");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testOnlyContainKeyWord() throws Exception
    {
        RestFolderInfo folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5() +"zhangsan", 0L);
        
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        String body = MyFileUtils.getDataFromFile(LIST_MYSHARE, "onlyContainKeyWord");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testNameDESC() throws Exception
    {
        RestFolderInfo folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5() +"ListMyShare", 0L);
        RestFolderInfo folderInfo2 = createFolder(userId1, new RandomGUID().getValueAfterMD5() +"ListMyShare", 0L);
        
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, folderInfo2.getId(), SHARE_ADD, "addShareMessage");
        String body = MyFileUtils.getDataFromFile(LIST_MYSHARE, "nameDESC");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testNameASC() throws Exception
    {
        RestFolderInfo folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5() +"ListMyShare", 0L);
        RestFolderInfo folderInfo2 = createFolder(userId1, new RandomGUID().getValueAfterMD5() +"ListMyShare", 0L);
        
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, folderInfo2.getId(), SHARE_ADD, "addShareMessage");
        String body = MyFileUtils.getDataFromFile(LIST_MYSHARE, "nameASC");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testSizeDESC() throws Exception
    {
        RestFolderInfo folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5() +"ListMyShare", 0L);
        RestFolderInfo folderInfo2 = createFolder(userId1, new RandomGUID().getValueAfterMD5() +"ListMyShare", 0L);
        FilePreUploadResponse fileResponse = uploadFile("D:\\junitTest.jpg", folderInfo1.getId());
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, folderInfo2.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, fileResponse.getFileId(), SHARE_ADD, "addShareMessage");
        String body = MyFileUtils.getDataFromFile(LIST_MYSHARE, "sizeDESC");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testSizeASC() throws Exception
    {
        RestFolderInfo folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5() +"ListMyShare", 0L);
        RestFolderInfo folderInfo2 = createFolder(userId1, new RandomGUID().getValueAfterMD5() +"ListMyShare", 0L);
        FilePreUploadResponse fileResponse = uploadFile("D:\\junitTest.jpg", folderInfo1.getId());
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, folderInfo2.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, fileResponse.getFileId(), SHARE_ADD, "addShareMessage");
        String body = MyFileUtils.getDataFromFile(LIST_MYSHARE, "sizeASC");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testModifiedAtDESC() throws Exception
    {
        RestFolderInfo folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5() +"ListMyShare", 0L);
        RestFolderInfo folderInfo2 = createFolder(userId1, new RandomGUID().getValueAfterMD5() +"ListMyShare", 0L);
        FilePreUploadResponse fileResponse = uploadFile("D:\\junitTest.jpg", folderInfo1.getId());
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, folderInfo2.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, fileResponse.getFileId(), SHARE_ADD, "addShareMessage");
        String body = MyFileUtils.getDataFromFile(LIST_MYSHARE, "modifiedAtDESC");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    //DTS2014102301470
    @Test
    public void testModifiedAtASC() throws Exception
    {
        RestFolderInfo folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5() +"ListMyShare", 0L);
        RestFolderInfo folderInfo2 = createFolder(userId1, new RandomGUID().getValueAfterMD5() +"ListMyShare", 0L);
        FilePreUploadResponse fileResponse = uploadFile("D:\\junitTest.jpg", folderInfo1.getId());
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, folderInfo2.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, fileResponse.getFileId(), SHARE_ADD, "addShareMessage");
        String body = MyFileUtils.getDataFromFile(LIST_MYSHARE, "modifiedAtASC");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testTypeDESC() throws Exception
    {
        RestFolderInfo folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5() +"ListMyShare", 0L);
        FilePreUploadResponse fileResponse = uploadFile("D:\\junitTest.jpg", 0L);
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, fileResponse.getFileId(), SHARE_ADD, "addShareMessage");
        String body = MyFileUtils.getDataFromFile(LIST_MYSHARE, "typeDESC");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testTypeASC() throws Exception
    {
        RestFolderInfo folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5() +"ListMyShare", 0L);
        FilePreUploadResponse fileResponse = uploadFile("D:\\junitTest.jpg", 0L);
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, fileResponse.getFileId(), SHARE_ADD, "addShareMessage");
        String body = MyFileUtils.getDataFromFile(LIST_MYSHARE, "typeASC");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testOwnerNameDESC() throws Exception
    {
        RestFolderInfo folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5() +"ListMyShare", 0L);
        FilePreUploadResponse fileResponse = uploadFile("D:\\junitTest.jpg", 0L);
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, fileResponse.getFileId(), SHARE_ADD, "addShareMessage");
        String body = MyFileUtils.getDataFromFile(LIST_MYSHARE, "ownerNameDESC");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testOwnerNameASC() throws Exception
    {
        RestFolderInfo folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5() +"ListMyShare", 0L);
        FilePreUploadResponse fileResponse = uploadFile("D:\\junitTest.jpg", 0L);
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, fileResponse.getFileId(), SHARE_ADD, "addShareMessage");
        String body = MyFileUtils.getDataFromFile(LIST_MYSHARE, "ownerNameASC");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testUnauthorized() throws Exception
    {
        RestFolderInfo folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5() +"ListMyShare", 0L);
        FilePreUploadResponse fileResponse = uploadFile("D:\\junitTest.jpg", 0L);
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, fileResponse.getFileId(), SHARE_ADD, "addShareMessage");
        String body = MyFileUtils.getDataFromFile(LIST_MYSHARE, "containAll");
        HttpURLConnection connection = getConnectionWithUnauthToken(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.LOGINUNAUTHORIZED, showResult);
    }
    
    @Test
    public void testInvalidSpaceStatus() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(LIST_MYSHARE, "containAll");
        HttpURLConnection connection = getConnectionWithUnauthToken(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_SPACE_STATUS, showResult);
    }
    
    //DTS2014102110174 
    @Test
    public void testCreateFolderAndShareFile() throws Exception
    {
        RestFolderInfo folderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5()+"Parent", 0L);
        FilePreUploadResponse fileResponse = uploadFile("D:/junitTest.jpg", folderInfo.getId());
        addShare(userId1, fileResponse.getFileId(), SHARE_DATA, "normal");
        String body = MyFileUtils.getDataFromFile(LIST_MYSHARE, "containAll");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testNoContainOrder() throws Exception
    {
        RestFolderInfo folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5() +"ListMyShare", 0L);
        RestFolderInfo folderInfo2 = createFolder(userId1, new RandomGUID().getValueAfterMD5() +"ListMyShare", 0L);
        FilePreUploadResponse fileResponse = uploadFile("D:\\junitTest.jpg", folderInfo1.getId());
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, folderInfo2.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, fileResponse.getFileId(), SHARE_ADD, "addShareMessage");
        String body = MyFileUtils.getDataFromFile(LIST_MYSHARE, "noContainOrder");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    //DTS2014102704957
    @Test
    public void testFahterDelete() throws Exception
    {
        RestFolderInfo folderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5() +"Father", 0L);
        FilePreUploadResponse fileResponse = uploadFile("D:/junitTest.jpg", folderInfo.getId());
        addShare(userId1, fileResponse.getFileId(), SHARE_DATA, "normal");
        deleteNode(userId1, folderInfo.getId());
        String body = MyFileUtils.getDataFromFile(LIST_MYSHARE, "containAll");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
}
