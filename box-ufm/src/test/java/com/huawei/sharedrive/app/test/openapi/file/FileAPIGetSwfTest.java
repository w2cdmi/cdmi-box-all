package com.huawei.sharedrive.app.test.openapi.file;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.openapi.domain.node.FilePreUploadResponse;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.share.domain.INodeLink;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

import pw.cdmi.core.utils.RandomGUID;

public class FileAPIGetSwfTest extends BaseAPITest
{
    
    @Test
    public void testNormal() throws Exception
    {
        RestFolderInfo folderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        FilePreUploadResponse fileResponse = uploadFile("D:\\junitTest.docx", folderInfo.getId());
        url = buildUrl(userId1, fileResponse.getFileId());
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testAuthWithLink() throws Exception
    {
        RestFolderInfo folderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        FilePreUploadResponse fileResponse = uploadFile("D:\\junitTest.jpg", folderInfo.getId());
        INodeLink link = createLinkByExpireTime(userId1, fileResponse.getFileId(), 1459845193374L);
        String dateStr = MyTestUtils.getDateString();
        String authorization = MyTestUtils.getLinkAuthorization(link.getId(),
            link.getPlainAccessCode(),
            dateStr);
        url = buildUrl(userId1, fileResponse.getFileId());
        HttpURLConnection connection = getConnection(url, METHOD_GET, authorization, dateStr, null);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testAuthWithLinkAgain() throws Exception
    {
        // RestFolderInfo folderInfo = createFolder(userId1, new
        // RandomGUID().getValueAfterMD5(), 0L);
        // FilePreUploadResponse fileResponse = uploadFile("D:\\junitTest.jpg",
        // folderInfo.getId());
        // url = buildUrl(userId1, fileResponse.getFileId());
        // HttpURLConnection connection = getConnection(url, METHOD_GET);
        // MyResponseUtils.assert200(connection, showResult);
        //
        // INodeLink link = createLinkByExpireTime(userId1, fileResponse.getFileId(),
        // 1459845193374L);
        String dateStr = MyTestUtils.getDateString();
        String authorization = MyTestUtils.getLinkAuthorization("2hfip049", "!x8UsI!L", dateStr);
        url = buildUrl(90L, 1L);
        HttpURLConnection connection1 = getConnection(url, METHOD_GET, authorization, dateStr, null);
        MyResponseUtils.assert200(connection1, showResult);
    }
    
    @Test
    public void testNoContainType() throws Exception
    {
        RestFolderInfo folderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        FilePreUploadResponse fileResponse = uploadFile("D:\\user_terminal.sql", folderInfo.getId());
        url = buildUrl(userId1, fileResponse.getFileId());
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_FILE_TYPE, showResult);
    }
    
    private String buildUrl(Long ownerId, Long fileId)
    {
        return MyTestUtils.SERVER_URL_UFM_V2 + "files/" + ownerId + "/" + fileId + "/swfUrl";
    }
    
}
