package com.huawei.sharedrive.app.test.openapi.folder;

import java.net.HttpURLConnection;

import com.huawei.sharedrive.app.share.domain.INodeLink;
import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.test.openapi.FileBaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

import pw.cdmi.core.utils.RandomGUID;

/**
 * 目录列举测试类
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2014-7-14
 * @see
 * @sincer  
 */
public class FolderAPIListTest extends FileBaseAPITest
{
    private static final String TEST_DATA = "testData/folder/folderList.txt";
    
    private Long folderId;
    
    public FolderAPIListTest()
    {
        folderId = Long.parseLong(MyFileUtils.getDataFromFile(TEST_DATA, "folderId"));
        url = buildUrl(userId1, folderId);
    }
    
    @Test
    public void testNormal() throws Exception
    {
        HttpURLConnection connection = getConnection(url, METHOD_POST);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testAuthWithLink() throws Exception
    {
        RestFolderInfo folderInfo = createRandomFolder();
        INodeLink link = createLink(userId1, folderInfo.getId());
        String dateStr = MyTestUtils.getDateString();
        String authorization = MyTestUtils.getLinkAuthorization(link.getId(), link.getPlainAccessCode(), dateStr);
        String url = buildUrl(userId1, folderInfo.getId());
        HttpURLConnection connection = getConnection(url, METHOD_POST, authorization,null, MyTestUtils.getDateString(), null);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testAuthWithLinkCode() throws Exception
    {
        RestFolderInfo folderInfo = createRandomFolder();
        INodeLink link = createObjectLink(userId1, folderInfo.getId());
        String dateStr = MyTestUtils.getDateString();
        String authorization = MyTestUtils.getLinkAuthorization(link.getId(), link.getPlainAccessCode(), dateStr);
        String url = buildUrl(userId1, folderInfo.getId());
        HttpURLConnection connection = getConnection(url, METHOD_POST, authorization,null, MyTestUtils.getDateString(), null);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testUnauthorize() throws Exception
    {
        HttpURLConnection connection = getConnectionWithUnauthToken(url, METHOD_POST, null);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.TOKENUNAUTHORIZED, showResult);
    }
    
    @Test
    public void testAllParameter() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "allParameter");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testOnlyOffset() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "onlyOffset");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testInvalidOffset() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "invalidOffset");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testOnlyLimit() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "onlyLimit");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testInvalidLimit() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "invalidLimit");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testOnlyOrder() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "onlyOrder");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testInvalidOrderField() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "invalidOrderField");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testInvalidOrderDirection() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "invalidOrderDirection");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testOnlyThumbnail() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "onlyThumbnail");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testThumbnailNumOverLimit() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "thumbnailNumOverLimit");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testInvalidThumbnailWidth() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "invalidThumbnailWidth");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testInvalidThumbnailHeight() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "invalidThumbnailHeigth");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testNoSuchFolder() throws Exception
    {
        String url = buildUrl(userId1, 99999L);
        HttpURLConnection connection = getConnection(url, METHOD_POST);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_FOLDER, showResult);
    }
    
    @Test
    public void testInvalidSpaceStatus() throws Exception
    {
        HttpURLConnection connection = getConnection(url, METHOD_POST);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_SPACE_STATUS,showResult );
    }
    
    /**
     * 列举已经删除的文件夹
     * @throws Exception
     */
    @Test
    public void testListDeleteFolder() throws Exception
    {
        RestFolderInfo folderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5()+"Father", 0L);
        createFolder(userId1, new RandomGUID().getValueAfterMD5(), folderInfo.getId());
        deleteNode(userId1, folderInfo.getId());
        String url = buildUrl(userId1, folderInfo.getId());
        HttpURLConnection connection = getConnection(url, METHOD_POST);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_FOLDER, showResult);
    }

    private String buildUrl(Long ownerId, Long parentId)
    {
        return MyTestUtils.SERVER_URL_UFM_V2 + "folders/" + ownerId + "/" + parentId + "/items";
    }
}
