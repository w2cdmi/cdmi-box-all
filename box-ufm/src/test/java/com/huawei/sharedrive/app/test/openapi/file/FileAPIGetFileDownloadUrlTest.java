package com.huawei.sharedrive.app.test.openapi.file;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.openapi.domain.node.FilePreUploadResponse;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.share.domain.INodeLink;
import com.huawei.sharedrive.app.test.openapi.FileBaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

import pw.cdmi.core.utils.RandomGUID;

/**
 * 获取文件下载地址
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2014-7-7
 * @see
 * @since
 */
public class FileAPIGetFileDownloadUrlTest extends FileBaseAPITest
{
    @Test
    public void testNormal() throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        url = buildUrl(userId1, response.getFileId());
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testUnauthorized() throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        url = buildUrl(userId1, response.getFileId());
        HttpURLConnection connection = getConnectionWithUnauthToken(url, METHOD_GET, null);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.TOKENUNAUTHORIZED, showResult);
    }
    
    @Test
    public void testAuthWithLink() throws Exception
    {
        RestFolderInfo folderInfo = createRandomFolder();
        FilePreUploadResponse fileInfo = uploadFile(MyFileUtils.getDataFromFile(INIT_DATA, "filePath1"),
            folderInfo.getId());
        INodeLink link = createLinkByExpireTime(userId1, fileInfo.getFileId(), null);
        String dateStr = MyTestUtils.getDateString();
        String authorization = MyTestUtils.getLinkAuthorization(link.getId(),
            link.getPlainAccessCode(),
            dateStr);
        String url = buildUrl(userId1, fileInfo.getFileId());
        HttpURLConnection connection = getConnection(url,
            METHOD_GET,
            authorization,
            MyTestUtils.getTestUserToken1(),
            dateStr);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testInvalidFileId() throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        url = buildUrl(userId1, response.getFileId());
        String url = buildUrl(userId1, -1L);
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testNoSuchFile() throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        url = buildUrl(userId1, response.getFileId());
        String url = buildUrl(userId1, 999999L);
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_FILE, showResult);
    }
    
    @Test
    public void testNoSuchFile1() throws Exception
    {
        RestFolderInfo folderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0l);
        url = buildUrl(userId1, folderInfo.getId());
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_FILE, showResult);
    }
    
    @Test
    public void testInvalidSpaceStatus() throws Exception
    {
        // FilePreUploadResponse response = uploadFile();
        // System.out.println(response.getFileId());
        url = buildUrl(userId1, 864);
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_SPACE_STATUS, showResult);
    }
    
    private String buildUrl(long ownerId, long fileId)
    {
        return MyTestUtils.SERVER_URL_UFM_V2 + "files/" + ownerId + "/" + fileId + "/url";
    }
}
