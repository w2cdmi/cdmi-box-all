package com.huawei.sharedrive.app.test.openapi.file;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.openapi.domain.node.FilePreUploadResponse;
import com.huawei.sharedrive.app.openapi.domain.node.RestFileVersionInfo;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.openapi.domain.node.RestVersionLists;
import com.huawei.sharedrive.app.test.openapi.FileBaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

import pw.cdmi.core.utils.RandomGUID;

/**
 * 文件版本列举测试类
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2014-7-7
 * @see
 * @since
 */
public class FileAPIVersionTest extends FileBaseAPITest
{
    private Long fileId;
    
    @Test
    public void testNormal() throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        fileId = response.getFileId();
        url = MyTestUtils.SERVER_URL_UFM_V2 + "files/" + userId1 + "/" + fileId + "/versions";
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testNormalWithOffsetAndLimit() throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        fileId = response.getFileId();
        url = MyTestUtils.SERVER_URL_UFM_V2 + "files/" + userId1 + "/" + fileId + "/versions";
        url = url + "?offset=0&limit=1000";
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testUnauthorized() throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        fileId = response.getFileId();
        url = MyTestUtils.SERVER_URL_UFM_V2 + "files/" + userId1 + "/" + fileId + "/versions";
        HttpURLConnection connection = getConnectionWithUnauthToken(url, METHOD_GET, null);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.TOKENUNAUTHORIZED, showResult);
    }
    
    @Test
    public void testOnlyOffset() throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        fileId = response.getFileId();
        url = MyTestUtils.SERVER_URL_UFM_V2 + "files/" + userId1 + "/" + fileId + "/versions";
        url = url + "?offset=0";
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testOnlyLimit() throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        fileId = response.getFileId();
        url = MyTestUtils.SERVER_URL_UFM_V2 + "files/" + userId1 + "/" + fileId + "/versions";
        url = url + "?limit=1";
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testInvalidOffset() throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        fileId = response.getFileId();
        url = MyTestUtils.SERVER_URL_UFM_V2 + "files/" + userId1 + "/" + fileId + "/versions";
        long invalidOffset = -1;
        url = url + "?offset=" + invalidOffset;
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testInvalidLimit() throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        fileId = response.getFileId();
        url = MyTestUtils.SERVER_URL_UFM_V2 + "files/" + userId1 + "/" + fileId + "/versions";
        long invalidLimit = 0;
        url = url + "?limit=" + invalidLimit;
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    // DTS2014101101562
    @Test
    public void testDeleteShareToListVersion() throws Exception
    {
        
        // 创建文件夹
        RestFolderInfo folderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5() + "Version", 0L);
        String path1 = "D:/1/copyAndMove.txt";
        String path2 = "D:/2/copyAndMove.txt";
        String path3 = "D:/3/copyAndMove.txt";
        // 上传文件版本
        FilePreUploadResponse fileResponse = uploadFile(path1, folderInfo.getId());
        uploadFile(path2, folderInfo.getId());
        uploadFile(path3, folderInfo.getId());
        
        // 添加共享
        String urlShare = MyTestUtils.SERVER_URL_UFM_V2 + "shareships/" + userId1 + "/" + folderInfo.getId();
        String body = MyFileUtils.getDataFromFile("testData/share/addShare.txt", "normal");
        HttpURLConnection connectionShare = getConnection(urlShare, METHOD_PUT, body);
        MyResponseUtils.assert200(connectionShare, showResult);
        
        // 删除共享
        String urlDelete = MyTestUtils.SERVER_URL_UFM_V2 + "shareships/" + userId1 + "/" + folderInfo.getId();
        urlDelete += "?userId=" + 1868 + "&type=user";
        HttpURLConnection connectionDelete = getConnection(urlDelete, METHOD_DELETE);
        MyResponseUtils.assert200(connectionDelete, showResult);
        
        // 列举文件版本
        url = MyTestUtils.SERVER_URL_UFM_V2 + "files/" + userId2 + "/" + fileResponse.getFileId()
            + "/versions";
        HttpURLConnection connection = getConnection(url, METHOD_GET, MyTestUtils.getTestUserToken2(), null);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_FILE, showResult);
        
    }
    
    @Test
    public void testNoSuchFile() throws Exception
    {
        url = MyTestUtils.SERVER_URL_UFM_V2 + "files/" + userId1 + "/" + 9999999 + "/versions";
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_FILE, showResult);
    }
    
    // 获取的是文件夹
    @Test
    public void testNoSuchFile1() throws Exception
    {
        RestFolderInfo folderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        url = MyTestUtils.SERVER_URL_UFM_V2 + "files/" + userId1 + "/" + folderInfo.getId() + "/versions";
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_FILE, showResult);
    }
    
    @Test
    public void testInvalidSpaceStatus() throws Exception
    {
        // FilePreUploadResponse response = uploadFile();
        // System.out.println(response.getFileId());
        url = MyTestUtils.SERVER_URL_UFM_V2 + "files/" + userId1 + "/" + 828 + "/versions";
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_SPACE_STATUS, showResult);
    }
    
    @Test
    public void testRestoreVersion() throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        fileId = response.getFileId();
        RestVersionLists list = listVersions(userId1, fileId);
        long versionId = 0;
        for (RestFileVersionInfo item : list.getVersions())
        {
            if (item.getType() == INode.TYPE_VERSION)
            {
                versionId = item.getId();
                break;
            }
        }
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "files/" + userId1 + "/" + versionId + "/restore";
        System.out.println("url is " + urlString);
        HttpURLConnection openurl = getConnection(urlString, METHOD_PUT);
        MyResponseUtils.assert200(openurl, showResult);
        
        url = MyTestUtils.SERVER_URL_UFM_V2 + "files/" + userId1 + "/" + fileId + "/versions";
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assert200(connection, showResult);
    }
    
}
