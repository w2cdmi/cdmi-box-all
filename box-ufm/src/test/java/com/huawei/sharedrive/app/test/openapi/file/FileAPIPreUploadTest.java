package com.huawei.sharedrive.app.test.openapi.file;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.openapi.domain.node.FilePreUploadResponse;
import com.huawei.sharedrive.app.openapi.domain.node.RestFileInfo;
import com.huawei.sharedrive.app.test.openapi.FileBaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;
import com.huawei.sharedrive.app.test.openapi.share.JSonUtils;

/**
 * 文件预上传测试类
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2014-7-7
 * @see
 * @since
 */
public class FileAPIPreUploadTest extends FileBaseAPITest
{
    private static final String TEST_DATA = "testData/file/filePreUpload.txt";
    
    public FileAPIPreUploadTest()
    {
        url = MyTestUtils.SERVER_URL_UFM_V2 + "files/" + userId1;
    }
    
    @Test
    public void testNormal() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "normal");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testAllParameter() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "allParameter");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testUnauthorized() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "normal");
        HttpURLConnection connection = getConnectionWithUnauthToken(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.TOKENUNAUTHORIZED, showResult);
    }
    
    @Test
    public void testFlashUpload() throws Exception
    {
        FilePreUploadResponse fileResponse = uploadFile();
        RestFileInfo fileInfo = getInodeInfo(userId1, fileResponse.getFileId());
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "flashUpload");
        body = body.replaceAll("#sha1#",fileInfo.getSha1() );
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assert201(connection, showResult);
    }
    
    @Test
    public void testNoSuchParent() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "noSuchParent");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_PARENT, showResult);
    }
    
    @Test
    public void testRepeatName() throws Exception
    {
        String repeatFolderName = MyFileUtils.getDataFromFile(TEST_DATA, "repeatFolderName");
        Long repeatFolderParent = Long.parseLong(MyFileUtils.getDataFromFile(TEST_DATA,
            "repeatFolderParentId"));
        
        // 创建同名目录
        createFolder(userId1, repeatFolderName, repeatFolderParent);
        
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "repeatName");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.FILES_CONFLICT, showResult);
    }
    
    @Test
    public void testEmptyName() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "emptyName");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testLongName() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "longName");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testNameContainSlash() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "nameContainSlash");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testNameContainBackSlash() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "nameContainBackSlash");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testNameStartWithPeriod() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "nameStartWithPeriod");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testNameEndtWithPeriod() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "nameEndWithPeriod");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testInvalidSpaceStatus() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "normal");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_SPACE_STATUS, showResult);
    }
    
    @Test
    public void testExceedQuota() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "normal");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.EXCEED_SPACE_QUOTA, showResult);
    }
    
    private RestFileInfo getInodeInfo(Long ownerId, Long fileId) throws Exception
    {
        String url = MyTestUtils.SERVER_URL_UFM_V2 + "files/" + ownerId + "/" + fileId;
        HttpURLConnection openurl = getConnection(url, METHOD_GET);
        InputStream stream = null;
        BufferedReader in = null;
        RestFileInfo inodeInfo = null;
        try
        {
            stream = openurl.getInputStream();
            in = new BufferedReader(new InputStreamReader(stream));
            String result = in.readLine();
            inodeInfo = JSonUtils.stringToObject(result, RestFileInfo.class);
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        finally
        {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(stream);
        }
        return inodeInfo;
    }
    
}
