package com.huawei.sharedrive.app.test.openapi.folder;

import java.net.HttpURLConnection;
import java.util.Date;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.openapi.domain.node.FilePreUploadResponse;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.share.domain.INodeLink;
import com.huawei.sharedrive.app.test.openapi.FileBaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

import pw.cdmi.core.utils.RandomGUID;

/**
 *  获取文件夹信息测试类
 * 
 * @author  t90006461
 * @version  CloudStor CSE Service Platform Subproject, 2014-7-14
 * @see  
 * @since  
 */
public class FolderAPIGetFolderInfoTest extends FileBaseAPITest
{
    
    private Long folderId;
    
    @Test
    public void testNormal() throws Exception
    {
        RestFolderInfo folderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5()+"SRC", 0L, new Date().getTime(), new Date().getTime());
        folderId = folderInfo.getId();
        url = MyTestUtils.SERVER_URL_UFM_V2 + "folders/" + userId1 + "/" + folderId;
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testUnauthorize() throws Exception
    {
        RestFolderInfo folderInfo = createRandomFolder();
        folderId = folderInfo.getId();
        url = MyTestUtils.SERVER_URL_UFM_V2 + "folders/" + userId1 + "/" + folderId;
        HttpURLConnection connection = getConnectionWithUnauthToken(url, METHOD_GET, null);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.TOKENUNAUTHORIZED, showResult);
    }
    
    @Test
    public void testAuthWithLink() throws Exception
    {
        RestFolderInfo folderInfo = createRandomFolder();
        INodeLink link = createLinkByExpireTime(userId1, folderInfo.getId(), null);
        String dateStr = MyTestUtils.getDateString();
        String authorization = MyTestUtils.getLinkAuthorization(link.getId(), link.getPlainAccessCode(), dateStr);
        String url = buildUrl(userId1, folderInfo.getId());
        HttpURLConnection connection = getConnection(url, METHOD_GET, authorization,null, dateStr, null);
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
        HttpURLConnection connection = getConnection(url, METHOD_GET, authorization,null, dateStr, null);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testInvalidOwnerId() throws Exception
    {
        RestFolderInfo folderInfo = createRandomFolder();
        folderId = folderInfo.getId();
        url = MyTestUtils.SERVER_URL_UFM_V2 + "folders/" + userId1 + "/" + folderId;
        String url = buildUrl(-1L, folderId);
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testInvalidFolderId() throws Exception
    {
        String url = buildUrl(userId1, -1L);
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testNoSuchFolder() throws Exception
    {
        String url = buildUrl(userId1, 999999L);
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_FOLDER, showResult);
    }
    
    @Test
    public void testNoSuchFolder1() throws Exception
    {
        RestFolderInfo folderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        FilePreUploadResponse fileResponse = uploadFile(folderInfo.getId());
        String url = buildUrl(userId1, fileResponse.getFileId());
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_FOLDER, showResult);
    }
    
    @Test
    public void testInvalidSpaceStatus() throws Exception
    {
//        RestFolderInfo folderInfo = createRandomFolder();
//        System.out.println(folderInfo.getId());
        url = MyTestUtils.SERVER_URL_UFM_V2 + "folders/" + userId1 + "/" + 824;
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_SPACE_STATUS, showResult);
    }
    
    private String buildUrl(Long ownerId, Long folderId)
    {
        return MyTestUtils.SERVER_URL_UFM_V2 + "folders/" + ownerId + "/" + folderId;
    }
    
}
