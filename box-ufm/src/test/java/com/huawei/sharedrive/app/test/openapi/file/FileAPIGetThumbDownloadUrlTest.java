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

/**
 * 获取缩略图下载地址测试类
 * 
 * @author  t90006461
 * @version  CloudStor CSE Service Platform Subproject, 2014-7-7
 * @see  
 * @since  
 */
public class FileAPIGetThumbDownloadUrlTest extends FileBaseAPITest
{

    private static final String INIT_DATA = "testData/file/initData.txt";
    
    @Test
    public void testNormal() throws Exception
    {
        String filePath = MyFileUtils.getDataFromFile(INIT_DATA, "imageFilePath");
        FilePreUploadResponse response = uploadFile(filePath, 0L);
        url = buildUrl(userId1, response.getFileId());
        url = url + "?height=32&width=32"; 
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testUnauthorized() throws Exception
    {
        String filePath = MyFileUtils.getDataFromFile(INIT_DATA, "imageFilePath");
        FilePreUploadResponse response = uploadFile(filePath, 0L);
        url = buildUrl(userId1, response.getFileId());
        url = url + "?height=32&width=32"; 
        HttpURLConnection connection = getConnectionWithUnauthToken(url, METHOD_GET, null);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.TOKENUNAUTHORIZED, showResult);
    }
    
    @Test
    public void testAuthWithLink() throws Exception
    {
        RestFolderInfo folderInfo = createRandomFolder();
        FilePreUploadResponse fileInfo = uploadFile(MyFileUtils.getDataFromFile(INIT_DATA, "imageFilePath"), folderInfo.getId());
        INodeLink link = createLinkByExpireTime(userId1, fileInfo.getFileId(), null);
        String dateStr = MyTestUtils.getDateString();
        String authorization = MyTestUtils.getLinkAuthorization(link.getId(), link.getPlainAccessCode(), dateStr);
        String url = buildUrl(userId1, fileInfo.getFileId());
        url = url + "?height=32&width=32"; 
        HttpURLConnection connection = getConnection(url, METHOD_GET, authorization,null, MyTestUtils.getDateString(), null);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testNoWidthAndHeight() throws Exception
    {
        String filePath = MyFileUtils.getDataFromFile(INIT_DATA, "imageFilePath");
        FilePreUploadResponse response = uploadFile(filePath, 0L);
        url = buildUrl(userId1, response.getFileId());
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assert400(connection, showResult);
    }
    
    @Test
    public void testInvalidWidth() throws Exception
    {
        String filePath = MyFileUtils.getDataFromFile(INIT_DATA, "imageFilePath");
        FilePreUploadResponse response = uploadFile(filePath, 0L);
        url = buildUrl(userId1, response.getFileId());
        url = url + "?height=32&width=0"; 
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testInvalidHeigth() throws Exception
    {
        String filePath = MyFileUtils.getDataFromFile(INIT_DATA, "imageFilePath");
        FilePreUploadResponse response = uploadFile(filePath, 0L);
        url = buildUrl(userId1, response.getFileId());
        url = url + "?height=0&width=32"; 
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testNoSuchFile() throws Exception
    {
        String filePath = MyFileUtils.getDataFromFile(INIT_DATA, "imageFilePath");
        FilePreUploadResponse response = uploadFile(filePath, 0L);
        String url = buildUrl(userId1, 99999L);
        url = url + "?height=32&width=32"; 
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_FILE, showResult);
    }
    
    @Test
    public void testInvalidFileType() throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        String url = buildUrl(userId1, response.getFileId());
        url = url + "?height=32&width=32"; 
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_FILE_TYPE, showResult);
    }
    
    @Test
    public void testInvalidSpaceStatus() throws Exception
    {
//        String filePath = MyFileUtils.getDataFromFile(INIT_DATA, "imageFilePath");
//        FilePreUploadResponse response = uploadFile(filePath, 0L);
//        System.out.println(response.getFileId());
        url = buildUrl(userId1, 926L);
        url = url + "?height=32&width=32"; 
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_SPACE_STATUS, showResult);
    }
    
    private String buildUrl(long ownerId, long fileId)
    {
        return MyTestUtils.SERVER_URL_UFM_V2 + "files/" + ownerId + "/" + fileId + "/thumbUrl";
    }
}
