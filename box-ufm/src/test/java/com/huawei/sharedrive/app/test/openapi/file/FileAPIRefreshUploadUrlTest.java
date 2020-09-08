package com.huawei.sharedrive.app.test.openapi.file;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.openapi.domain.node.FilePreUploadResponse;
import com.huawei.sharedrive.app.openapi.domain.node.RefreshUploadUrlRquest;
import com.huawei.sharedrive.app.test.openapi.FileBaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

import pw.cdmi.core.utils.JsonUtils;

/**
 * 刷新文件上传地址测试类
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2014-7-7
 * @see
 * @since
 */
public class FileAPIRefreshUploadUrlTest extends FileBaseAPITest
{
    private static final String TEST_DATA = "testData/file/refreshFileUploadUrl.txt";
    
    @Test
    public void testNormal() throws Exception
    {
        FilePreUploadResponse preUploadResponse = getFilePreuploadResponse();
        RefreshUploadUrlRquest request = new RefreshUploadUrlRquest(preUploadResponse.getUploadUrl());
        String body = JsonUtils.toJson(request);
        url = buildUrl(preUploadResponse.getFileId());
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testUnauthorized() throws Exception
    {
        FilePreUploadResponse preUploadResponse = getFilePreuploadResponse();
        RefreshUploadUrlRquest request = new RefreshUploadUrlRquest(preUploadResponse.getUploadUrl());
        String body = JsonUtils.toJson(request);
        url = buildUrl(preUploadResponse.getFileId());
        HttpURLConnection connection = getConnectionWithUnauthToken(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.TOKENUNAUTHORIZED, showResult);
    }
    
    @Test
    public void testNoSuchFile() throws Exception
    {
        FilePreUploadResponse preUploadResponse = getFilePreuploadResponse();
        RefreshUploadUrlRquest request = new RefreshUploadUrlRquest(preUploadResponse.getUploadUrl());
        String body = JsonUtils.toJson(request);
        long fileId = 99999L;
        url = buildUrl(fileId);
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_FILE, showResult);
    }
    
    @Test
    public void testUnmatchUrl() throws Exception
    {
        FilePreUploadResponse preUploadResponse = getFilePreuploadResponse();
        String uploadUrl = "http://10.169.33.23:8080/api/c1f1da00cbb2e06cadf6a95f320eda70/xxxxxxxxxx";
        RefreshUploadUrlRquest request = new RefreshUploadUrlRquest(uploadUrl);
        String body = JsonUtils.toJson(request);
        url = buildUrl(preUploadResponse.getFileId());
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.UNMATCHED_UPLOADURL, showResult);
    }
    
    public String buildUrl(long fileId)
    {
        url = MyTestUtils.SERVER_URL_UFM_V2 + "files/" + userId1 + "/" + fileId + "/refreshurl";
        return url;
    }
    
    private FilePreUploadResponse getFilePreuploadResponse() throws Exception
    {
        String preUploadUrl = MyTestUtils.SERVER_URL_UFM_V2 + "files/" + userId1;
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "preUpload");
        HttpURLConnection connection = getConnection(preUploadUrl, METHOD_PUT, body);
        
        int returnCode = connection.getResponseCode();
        if (returnCode != 200)
        {
            throw new RuntimeException("File preupload failed!");
        }
        InputStream stream = null;
        BufferedReader in = null;
        try
        {
            stream = connection.getInputStream();
            in = new BufferedReader(new InputStreamReader(stream));
            String result = in.readLine();
            FilePreUploadResponse response = JsonUtils.stringToObject(result, FilePreUploadResponse.class);
            return response;
        }
        catch (Exception e)
        {
            throw new RuntimeException("Preupload failed!");
        }
        finally
        {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(stream);
            connection.disconnect();
        }
        
    }
    
}
